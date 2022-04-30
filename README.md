# sd-exam2
# Namespace
microk8s.kubectl create -f exam2-ns.yml

# Config
microk8s.kubectl delete -f app-config.yml
microk8s.kubectl create -f app-config.yml

# Kafka
microk8s.kubectl delete -f kafka-deployment.yml
microk8s.kubectl create -f kafka-deployment.yml

# MYSQL
microk8s.kubectl delete -f mysql_pay.yml
microk8s.kubectl create -f mysql_pay.yml

# APP-PAY
microk8s.kubectl delete -f pay.yml
microk8s.kubectl create -f pay.yml

# POSTGRES
microk8s.kubectl delete -f postgres_invoice.yml
microk8s.kubectl create -f postgres_invoice.yml

# APP-INVOICE
microk8s.kubectl delete -f invoice.yml
microk8s.kubectl create -f invoice.yml

# MONGO DB
microk8s.kubectl delete -f mongo-deployment.yml
microk8s.kubectl create -f mongo-deployment.yml

# TRANSACTION
microk8s.kubectl delete -f app-transaction.yml
microk8s.kubectl create -f app-transaction.yml