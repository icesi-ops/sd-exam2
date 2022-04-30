#!/bin/bash
#Primero incie el nodo de minikube
echo -----------------------------------
echo Se estan desplegando los recursos

kubectl apply -f NSexam.yaml
kubectl apply -f ReplicaMySQL.yaml
kubectl apply -f kafka.yaml
kubectl apply -f DeployAppConfig.yaml
kubectl apply -f DeployAppPay.yaml

#IR al namespace para hacer pruebas
echo -----------------------------------
echo Redirigiendo al NS

kubectl config set-context --current --namespace=exam-2
