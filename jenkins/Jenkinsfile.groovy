pipeline {
    agent any
    environment {
        // Hard-code the AWS regions
        PRIMARY_REGION = 'me-south-1'  // Primary region
        SECONDARY_REGION = 'eu-west-1'  // Secondary region
        ECR_REPO = 'aws_account_id.dkr.ecr.region.amazonaws.com/repository_name'
    }
    stages {
        stage('Build React SPA') {
            steps {
                script {
                    sh 'cd react-app && npm install && npm run build'
                }
            }
        }
        stage('Build Svelte SPA') {
            steps {
                script {
                    sh 'cd svelte-app && npm install && npm run build'
                }
            }
        }
        stage('Build API and Media Server') {
            steps {
                script {
                    sh 'docker build -t php-api .'
                    sh 'docker build -t python-media-server .'
                }
            }
        }
        stage('Push Docker Images to ECR') {
            steps {
                script {
                    sh 'aws ecr get-login-password --region $PRIMARY_REGION | docker login --username AWS --password-stdin $ECR_REPO'
                    sh 'docker tag php-api:latest $ECR_REPO/php-api:latest'
                    sh 'docker tag python-media-server:latest $ECR_REPO/python-media-server:latest'
                    sh 'docker push $ECR_REPO/php-api:latest'
                    sh 'docker push $ECR_REPO/python-media-server:latest'
                }
            }
        }
        stage('Deploy to Primary Region') {
            steps {
                script {
                    sh "kubectl --region $PRIMARY_REGION apply -f kubernetes/eks/php-api-deployment.yaml"
                    sh "kubectl --region $PRIMARY_REGION apply -f kubernetes/eks/python-media-server-deployment.yaml"
                }
            }
        }
        stage('Deploy to Secondary Region') {
            steps {
                script {
                    sh "kubectl --region $SECONDARY_REGION apply -f kubernetes/eks/php-api-deployment.yaml"
                    sh "kubectl --region $SECONDARY_REGION apply -f kubernetes/eks/python-media-server-deployment.yaml"
                }
            }
        }
        stage('Deploy Frontend to S3 and CloudFront') {
            steps {
                script {
                    sh 'aws s3 sync react-app/build s3://my-react-app-bucket'
                    sh 'aws s3 sync svelte-app/build s3://my-svelte-app-bucket'
                    sh 'aws cloudfront create-invalidation --distribution-id YOUR_CF_DISTRIBUTION_ID --paths "/*"'
                }
            }
        }
        stage('Schema Changes for ClickHouse') {
            steps {
                script {
                    sh 'aws ssm send-command --document-name "AWS-RunShellScript" --targets "Key=instanceIds,Values=instance_id" --parameters "commands=[\"/path/to/schema_migration_script\"]"'
                }
            }
        }
        stage('Health Check') {
            steps {
                script {
                    // Ensure the services are healthy in both regions
                    sh "curl --silent --fail http://primary-region-api-url/health"
                    sh "curl --silent --fail http://secondary-region-api-url/health"
                }
            }
        }
    }
    post {
        failure {
            script {
                echo "Deployment failed. Rolling back..."
                // Trigger rollback to previous stable state
                sh './rollback.sh'
            }
        }
    }
}
