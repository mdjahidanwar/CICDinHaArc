#!/bin/bash
# deploy_media_server.sh

REGION=$1
CONFIG_FILE=$2

# Load config
source <(sed 's/: /=/g' < $CONFIG_FILE)

echo "Deploying Media Server in region $REGION"

# Deploy Media Server on EKS
aws eks --region $REGION update-kubeconfig --name $eks_cluster_name

# Apply Kubernetes manifest files for Media Server
kubectl apply -f k8s-manifests/media_server.yaml --namespace=$namespace
