Overview
This folder structure and deployment setup allows for automated deployments of:

Front-End Applications (React and Svelte SPAs)
Backend API (PHP monolithic application in EKS)
ClickHouse Database (on EC2 with EBS/SSD)
Media Server (Python scripts for video encryption in EKS)

# Prerequisites
# AWS CLI - Install the AWS CLI to execute deployment scripts.
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install

# kubectl - For deploying applications to EKS clusters.
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
chmod +x kubectl
sudo mv kubectl /usr/local/bin/


# Usage
Clone the repository and navigate to the Jenkins workspace.
Edit Configuration Files: Customize YAML files in configs/ to specify your infrastructure setup.

# Run Deployment Scripts:
Frontend: ./scripts/deploy_frontend.sh react me-south-1 ./configs/frontend_config.yaml
Backend: ./scripts/deploy_backend.sh php me-south-1 ./configs/backend_config.yaml
ClickHouse: ./scripts/deploy_clickhouse.sh me-south-1 ./configs/clickhouse_config.yaml
Media Server: ./scripts/deploy_media_server.sh me-south-1 ./configs/media_server_config.yaml
Jenkins Pipeline: The Jenkinsfile can be configured in Jenkins to automate these steps across multiple regions.

# Folder Structure
scripts/: Contains deployment scripts for each service.
configs/: Holds configuration files for services.




# Rollback Process

This document describes the process for rolling back application components to a previous stable version. 

### Requirements
- Jenkins pipeline to initiate rollback.
- Access to the required version in container registry or artifact repository.
- AWS Credentials stored in Jenkins to authenticate and deploy across `me-south-1` and `eu-west-1`.

### Parameters
- **TARGET_REGION**: Region to deploy the rollback (`me-south-1` for primary and `eu-west-1` for secondary).
- **ROLLBACK_VERSION**: The application version to rollback to, defaults to `last-stable`.

### Rollback Script: `rollback.sh`
The `rollback.sh` script handles the rollback for:
1. Front-end applications (React & Svelte)
2. Backend monolithic API
3. ClickHouse database
4. Python media server

### Usage
1. **Jenkins Pipeline**: The pipeline `rollback_jenkinsfile` can be run manually, with options to select the region and version.
2. **Freestyle Job**: Alternatively, use the Freestyle job configuration to run `rollback.sh` with parameters.

### Execution Order
1. **Frontend Rollback**
2. **Backend Rollback**
3. **Database Rollback**
4. **Media Server Rollback**

### Notifications
Notifications are sent based on success or failure of the rollback process.

