# sd-exam2
# Namespace
microk8s.kubectl create -f exam2-ns.yml

# Config
- microk8s.kubectl delete -f app-config.yml
- microk8s.kubectl create -f app-config.yml

# Kafka
- microk8s.kubectl delete -f kafka-deployment.yml
- microk8s.kubectl create -f kafka-deployment.yml

# MONGO DB
- microk8s.kubectl delete -f mongo-deployment.yml
- microk8s.kubectl create -f mongo-deployment.yml

# TRANSACTION
- microk8s.kubectl delete -f app-transaction.yml
- microk8s.kubectl create -f app-transaction.yml