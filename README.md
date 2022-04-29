# sd-exam2

Author:
- Christian Gallo Pelaez

Instructions
1. kubectl apply -f namespace.yaml
2. kubectl apply -f app-config.yaml -n pay-app
3. kubectl apply -f kafka-deployment.yaml -n pay-app
4. kubectl apply -f app-pay-db.yaml -n pay-app
5. kubectl apply -f app-pay.yaml -n pay-app