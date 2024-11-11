#!/bin/bash
# deploy_frontend.sh

APP_TYPE=$1
REGION=$2
CONFIG_FILE=$3

# Load config
source <(sed 's/: /=/g' < $CONFIG_FILE)

echo "Deploying $APP_TYPE frontend application in region $REGION"

# Check app type and deploy
if [ "$APP_TYPE" == "react" ]; then
  aws s3 sync ./dist s3://$s3_bucket --region $REGION
  aws cloudfront create-invalidation --distribution-id $cloudfront_distribution_id --paths "/*" --region $REGION
elif [ "$APP_TYPE" == "svelte" ]; then
  aws s3 sync ./dist s3://$s3_bucket --region $REGION
  aws cloudfront create-invalidation --distribution-id $cloudfront_distribution_id --paths "/*" --region $REGION
else
  echo "Invalid frontend application type: $APP_TYPE. Use 'react' or 'svelte'."
  exit 1
fi
