#!/bin/sh
kubectl delete -f /home/centos/egar-files-api/scripts/kube/files-api-deployment.yaml
kubectl delete -f /home/centos/egar-files-api/scripts/kube/files-api-service.yaml

