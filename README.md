# sd-exam2

Author:
- Christian Gallo Pelaez

Assigned microservice: Pay

Instructions:
1. kubectl apply -f namespace.yaml

# Config Microservice
2. kubectl apply -f app-config.yaml -n pay-app

# Kafka Deployment
3. kubectl apply -f kafka-deployment.yaml -n pay-app

# Pay Microservice
4. kubectl apply -f app-pay-db.yaml -n pay-app
5. kubectl apply -f app-pay.yaml -n pay-app

# Transaction Microservice
6. kubectl apply -f transaction-db-secret.yaml -n pay-app
7. kubectl apply -f app-transaction-db.yaml -n pay-app
7. kubectl apply -f app-transaction.yaml -n pay-app

# root_mongo
# password_mongo

Configuration files repository: 
- https://github.com/Gallo9923/app-pay-config.git