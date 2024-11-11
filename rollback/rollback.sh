#!/bin/bash

TARGET_REGION=$1
ROLLBACK_VERSION=$2

echo "Rolling back to version $ROLLBACK_VERSION in region $TARGET_REGION..."

# Example rollback commands for EKS, EC2, etc.
# Replace these commands with actual rollback logic based on your infrastructure.

if [ "$TARGET_REGION" == "me-south-1" ]; then
    # Commands specific to Saudi Arabia region
    echo "Rolling back in Primary Region (Saudi Arabia)..."
    # Add rollback steps here, e.g., restoring EKS deployment to previous version
fi

if [ "$TARGET_REGION" == "eu-west-1" ]; then
    # Commands specific to Europe (Ireland) region
    echo "Rolling back in Secondary Region (Ireland)..."
    # Add rollback steps here, e.g., restoring EKS deployment to previous version
fi

echo "Rollback to $ROLLBACK_VERSION completed in region $TARGET_REGION."
