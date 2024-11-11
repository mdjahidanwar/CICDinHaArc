#!/bin/bash
# deploy_clickhouse.sh

REGION=$1
CONFIG_FILE=$2

# Load config
source <(sed 's/: /=/g' < $CONFIG_FILE)

echo "Deploying ClickHouse database in region $REGION"

# Create or update the ClickHouse instance on EC2 with EBS attached
aws ec2 run-instances \
    --image-id $ami_id \
    --count 1 \
    --instance-type $instance_type \
    --key-name $key_name \
    --security-group-ids $security_group_id \
    --subnet-id $subnet_id \
    --region $REGION

# Setup backup with EBS and multi-AZ
