echo ">>> CREATING GLOBAL MICROSERVICES"

echo "Deploying config micro..."
kubectl delete -f kubernetes/deployments/config.yaml
kubectl create -f kubernetes/deployments/config.yaml
echo "Done."

echo "Deploying kafka micro..."
kubectl delete -f kubernetes/deployments/kafka.yaml
kubectl create -f kubernetes/deployments/kafka.yaml
echo "Done."


echo ">>> CREATING PAY MICROSERVICES"

echo "Deploying MySQL DB micro..."
kubectl delete -f kubernetes/deployments/mysql_pay.yaml
kubectl create -f kubernetes/deployments/mysql_pay.yaml
echo "Done."

echo "Deploying pay micro..."
kubectl delete -f kubernetes/deployments/pay.yaml
kubectl create -f kubernetes/deployments/pay.yaml
echo "Done."


echo ">>> CREATING INVOICE MICROSERVICES"

echo "Deploying invoice Postgres DB"
kubectl delete -f kubernetes/deployments/postgres_invoice.yaml
kubectl create -f kubernetes/deployments/postgres_invoice.yaml
echo "Done."


echo "Deploying invoice micro..."
kubectl delete -f kubernetes/deployments/invoice.yaml
kubectl create -f kubernetes/deployments/invoice.yaml
echo "Done"

echo ">>> CREATING TRANSACTION MICROSERVICES"

echo "Deploying mongo DB micro..."
kubectl delete -f kubernetes/deployments/mongodb_transaction.yaml
kubectl create -f kubernetes/deployments/mongodb_transaction.yaml

echo "Deploying transaction micro..."
kubectl delete -f kubernetes/deployments/transaction.yaml
kubectl create -f kubernetes/deployments/transaction.yaml
echo "Done."

