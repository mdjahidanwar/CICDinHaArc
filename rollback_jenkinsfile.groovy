pipeline {
    agent any

    parameters {
        choice(
            name: 'TARGET_REGION',
            choices: ['me-south-1', 'eu-west-1'],
            description: 'Select the region for rollback'
        )
        string(
            name: 'ROLLBACK_VERSION',
            defaultValue: 'last-stable',
            description: 'Specify the version to rollback to (e.g., last-stable)'
        )
    }

    environment {
        TARGET_REGION = "${params.TARGET_REGION}"
        ROLLBACK_VERSION = "${params.ROLLBACK_VERSION}"
    }

    stages {
        stage('Prepare Rollback') {
            steps {
                echo "Preparing rollback for region ${TARGET_REGION} to version ${ROLLBACK_VERSION}"
            }
        }
        
        stage('Execute Rollback') {
            steps {
                echo "Running rollback.sh script..."
                sh "./rollback/rollback.sh ${TARGET_REGION} ${ROLLBACK_VERSION}"
            }
        }
    }

    post {
        success {
            echo 'Rollback completed successfully.'
        }
        failure {
            echo 'Rollback failed. Check logs for details.'
        }
    }
}
