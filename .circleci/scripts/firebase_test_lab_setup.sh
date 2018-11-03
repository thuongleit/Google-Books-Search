#!/bin/sh

# Check if Google Cloud Credentials env is set, exit if not 
if [ -z "$GCLOUD_SERVICE_KEY" ]; then
  echo "GCLOUD_SERVICE_KEY env variable is empty. Exiting."
  exit 1
fi

# Check if Google Project Id env is set, exit if not
if [ -z "$PROJECT_ID" ]; then
    echo "PROJECT_ID env variable is empty. Exiting."
    exit 1
fi

# Check if Google Service Account env is set, exit if not
if [ -z "$SERVICE_ACCOUNT" ]; then
    echo "SERVICE_ACCOUNT env variable is empty. Exiting."
    exit 1
fi

# Export to secrets file
echo "$GCLOUD_SERVICE_KEY" | base64 -di > "${HOME}"/gcp-key.json

# Set project ID
gcloud --quiet config set project "${PROJECT_ID}"

# Authentication
gcloud auth activate-service-account "${SERVICE_ACCOUNT}" --key-file "${HOME}"/gcp-key.json

# Delete secret key
rm "${HOME}/gcp-key.json"
