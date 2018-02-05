#!/bin/sh
echo Starting Files-API version: $FILES_API_VER
rm -rf /home/centos/egar-files-api/scripts/kube/files-api-deployment.yaml; envsubst < "/home/centos/egar-files-api/scripts/kube/files-api-deployment-template.yaml" > "/home/centos/egar-files-api/scripts/kube/files-api-deployment.yaml"
kubectl create -f /home/centos/egar-files-api/scripts/kube/files-api-deployment.yaml
kubectl create -f /home/centos/egar-files-api/scripts/kube/files-api-service.yaml

