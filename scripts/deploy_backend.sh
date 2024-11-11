#!/bin/bash
# deploy_backend.sh

APP_TYPE=$1
REGION=$2
CONFIG_FILE=$3

# Load config
source <(sed 's/: /=/g' < $CONFIG_FILE)

echo "Deploying $APP_TYPE backend application in region $REGION"

# Run AWS CLI commands to deploy backend to EKS
aws eks --region $REGION update-kubeconfig --name $eks_cluster_name

# Apply Kubernetes manifest files
kubectl apply -f k8s-manifests/backend.yaml --namespace=$namespace
