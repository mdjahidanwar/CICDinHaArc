pipeline {
    agent any

    environment {
        PRIMARY_REGION = "me-south-1"
        SECONDARY_REGION = "eu-west-1"
    }

    stages {
        stage('Build and Test') {
            parallel {
                stage('Build and Test React SPA') {
                    steps {
                        echo "Building and testing React SPA"
                        sh "./scripts/deploy_frontend.sh react ${PRIMARY_REGION} ./configs/frontend_config.yaml"
                    }
                }
                stage('Build and Test Svelte SPA') {
                    steps {
                        echo "Building and testing Svelte SPA"
                        sh "./scripts/deploy_frontend.sh svelte ${PRIMARY_REGION} ./configs/frontend_config.yaml"
                    }
                }
                stage('Build and Test PHP API') {
                    steps {
                        echo "Building and testing PHP API"
                        sh "./scripts/deploy_backend.sh php ${PRIMARY_REGION} ./configs/backend_config.yaml"
                    }
                }
                stage('Build and Test ClickHouse') {
                    steps {
                        echo "Building and testing ClickHouse"
                        sh "./scripts/deploy_clickhouse.sh ${PRIMARY_REGION} ./configs/clickhouse_config.yaml"
                    }
                }
                stage('Build and Test Media Server') {
                    steps {
                        echo "Building and testing Media Server"
                        sh "./scripts/deploy_media_server.sh ${PRIMARY_REGION} ./configs/media_server_config.yaml"
                    }
                }
            }
        }

        stage('Deploy to Primary Region') {
            parallel {
                stage('Deploy React SPA') {
                    steps {
                        echo "Deploying React SPA to Primary Region"
                        sh "./scripts/deploy_frontend.sh react ${PRIMARY_REGION} ./configs/frontend_config.yaml"
                    }
                }
                stage('Deploy Svelte SPA') {
                    steps {
                        echo "Deploying Svelte SPA to Primary Region"
                        sh "./scripts/deploy_frontend.sh svelte ${PRIMARY_REGION} ./configs/frontend_config.yaml"
                    }
                }
                stage('Deploy PHP API') {
                    steps {
                        echo "Deploying PHP API to Primary Region"
                        sh "./scripts/deploy_backend.sh php ${PRIMARY_REGION} ./configs/backend_config.yaml"
                    }
                }
                stage('Deploy ClickHouse') {
                    steps {
                        echo "Deploying ClickHouse to Primary Region"
                        sh "./scripts/deploy_clickhouse.sh ${PRIMARY_REGION} ./configs/clickhouse_config.yaml"
                    }
                }
                stage('Deploy Media Server') {
                    steps {
                        echo "Deploying Media Server to Primary Region"
                        sh "./scripts/deploy_media_server.sh ${PRIMARY_REGION} ./configs/media_server_config.yaml"
                    }
                }
            }
        }

        stage('Deploy to Secondary Region') {
            parallel {
                stage('Deploy React SPA') {
                    steps {
                        echo "Deploying React SPA to Secondary Region"
                        sh "./scripts/deploy_frontend.sh react ${SECONDARY_REGION} ./configs/frontend_config.yaml"
                    }
                }
                stage('Deploy Svelte SPA') {
                    steps {
                        echo "Deploying Svelte SPA to Secondary Region"
                        sh "./scripts/deploy_frontend.sh svelte ${SECONDARY_REGION} ./configs/frontend_config.yaml"
                    }
                }
                stage('Deploy PHP API') {
                    steps {
                        echo "Deploying PHP API to Secondary Region"
                        sh "./scripts/deploy_backend.sh php ${SECONDARY_REGION} ./configs/backend_config.yaml"
                    }
                }
                stage('Deploy ClickHouse') {
                    steps {
                        echo "Deploying ClickHouse to Secondary Region"
                        sh "./scripts/deploy_clickhouse.sh ${SECONDARY_REGION} ./configs/clickhouse_config.yaml"
                    }
                }
                stage('Deploy Media Server') {
                    steps {
                        echo "Deploying Media Server to Secondary Region"
                        sh "./scripts/deploy_media_server.sh ${SECONDARY_REGION} ./configs/media_server_config.yaml"
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline execution completed.'
        }
        success {
            echo 'Deployment succeeded!'
        }
        failure {
            echo 'Deployment failed. Initiating rollback.'
            build job: 'Rollback-Only', parameters: [
                string(name: 'TARGET_REGION', value: "${PRIMARY_REGION}"),
                string(name: 'ROLLBACK_VERSION', value: 'last-stable')
            ]
        }
    }
}
