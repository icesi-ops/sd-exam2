# Kubernetes

# Configuration changes 

In order to prevent config server from trying to connect to Consul service discovery, I deleted those dependencies from []()

Firstly, I had to change the [bootstrap.properties](./app-pay/src/main/resources/bootstrap.properties) file of each microservice in order to make them point to the IP and port of the config server given by Kubernetes.

Change from 
```
spring.cloud.config.uri=http://app-config:8888
```
to
```
spring.cloud.config.uri=http://${CONFIG_NODEPORT_SERVICE_HOST}:${CONFIG_NODEPORT_SERVICE_PORT}
```

Secondly, I had to fix MySQL database authentication. I changed the DB password in [app pay properties](./config/app-pay-dev.properties) to match MYSQL DB password expected in [docker image built from here](./resources/mysql/Dockerfile/)

Then, for each microservice, I had to change .properties of [config files ](./config/) to point to env variables containing IP adresses determined by Kubernetes.
## Push built images to dockerhub

Build docker files for each microservices with special tags

```bash
cd ds-exams/midterm2

cd app-config 
docker build -t sebasgarciamo/app-config:mt-2 .
docker push sebasgarciamo/app-config:mt-2 
cd ..

cd app-pay 
docker build -t sebasgarciamo/app-pay:mt-2 .
docker push sebasgarciamo/app-pay:mt-2 
cd ..

cd app-invoice
docker build -t sebasgarciamo/app-invoice:mt-2 .
docker push sebasgarciamo/app-invoice:mt-2 
cd ..

cd app-transaction
docker build -t sebasgarciamo/app-transaction:mt-2 .
docker push sebasgarciamo/app-transaction:mt-2 
cd ..

# Create docker images for databases
cd resources

cd mysql
docker build -t sebasgarciamo/mysql-app-pay:mt-2 .
docker push sebasgarciamo/mysql-app-pay:mt-2 
cd ..

cd postgres
docker build -t sebasgarciamo/postgres-app-invoice:mt-2 .
docker push sebasgarciamo/postgres-app-invoice:mt-2 
cd ..

cd ..
```

## Step by step solution

```bash
kubectl create -f kubernetes/deployments/config.yaml
```
Once you execute this, you should see expect this in config pod logs:
![Config starting](./evidences/steps/step_1_config_starting.png)

```bash
kubectl create -f kubernetes/deployments/kafka.yaml
```
Once you execute this, you should see expect this in Kafka pod logs. It is mounting the advertised host to the environment variable that points to the IP determined by Kubernetes, this is configured in [kafka deployment yaml](./kubernetes/deployments/kafka.yaml):

![Kafka ready](./evidences/steps/step_5_kafka_ready.png)

### Pay

#### MySQL DB

```bash
kubectl create -f kubernetes/deployments/mysql_pay.yaml
```

Once you execute this, you should see expect this in MySQL pay DB pod logs. It is creating the database in which the pay miroservice will save payment operations and then listens for connections.

![MySQL starting](./evidences/steps/step_3_mysql_pay_starting.png)

#### Pay microservice

```bash
# Once the mysql database is running and waiting for connection, run pay. 
# This is done in order to avoid timeout errors. Otherwise, the pay microservice
# may restarts until the database is ready for connections.
kubectl create -f kubernetes/deployments/pay.yaml
```
When the above microservices are on, the pay microservice starts by fetching config from the config microservice. Notice that it is fetching the dev profile from the IP that we are referecing from $CONFIG_CLUSTERIP_SERVICE_SERVICE_HOST environment variable.

![Pay fetching config](./evidences/steps/step_2_pay_starting_fetching_config.png)

Then, it will start the connection to the database microservice available at the IP specified in the BD_PAY_CLUSTERIP_SERVICE_SERVICE_HOST environment variable at port 3306. Once it has connected, it will execute some operations in the DB such as ```drop table pay if exists```

![Pay connecting to DB](./evidences/steps/step_4_pay_connection_to_db.png)


After pay has established connection to the DB, it will connect to the Kafka microservice:

![Pay connecting to Kafka](./evidences/steps/step_5_pay_kafka_connection_ready.png)

### Invoice

#### Postgres DB

```bash
kubectl create -f kubernetes/deployments/postgres_invoice.yaml
```

![Postgres DB ready](./evidences/steps/step_6_postgres_start.png)

Once the postgres DB is ready and listening for connections, we can start the invoice microservice.
#### Invoice microservice

```bash
kubectl create -f kubernetes/deployments/invoice.yaml
```

First, it fetches the configuration from config:
![Invoice config](./evidences/steps/step_7_invoice_config.png)

Then, is is able to connect to the database:
![Invoice Postgres](./evidences/steps/step_7.1_invoice_postgres.png)

Finally, it starts to listen to kafka in order to consume messages
![Invoice kafka](./evidences/steps/step_7.2_invoice_kafka.png)

### Transaction

#### Transaction DB

First, we setup the database:

```bash
kubectl create -f kubernetes/deployments/mongodb_transaction.yaml
```

#### Transaction microservice

Once we have verified that the database is listening for connections, we can launch the transaction microservice:

```bash
kubectl create -f kubernetes/deployments/transaction.yaml
```

First, it is able to fetch the configuration from the config server.

![Transaction config](./evidences/steps/step_8_transaction_config.png)

Then, it starts the connection to the database successfully:

![Mongo transaction](./evidences/steps/step_8.1_transaction_mongodb.png)

And finally, it is able to subscribe to the Kafka message queue

![Mongo kafka](./evidences/steps/step_8.2_transaction_kafka.png)

# Evidences

**Notice that the number of pods running does not match the number of replicas in yaml files, this is due to the fact that my local setup is not able to handle such big computational burden and starts evicting pods, hence, I tested the cofniguration with only one replica per microservice**
## Pods running 

Notice that the invoice microservice is not even created because of the computational limit described above.

![Pods running](./evidences/steps/pods_running_evidence.png)

## Services running

The load balancer makes it possible to access to microservices through localhost:

![Services running](./evidences/steps/services_evidence.png)
## Endpoints

### Config

![Config endpoint](./evidences/steps/step_1_config_endpoint_test.png)

### Pay

![Pay endpoint](./evidences/steps/step_2_pay_endpoint_test.png)

In addition to receive POST requests, the service also is able to produce the message via Kafka.

![Pay kafka](./evidences/steps/step_2_pay_post_kafka_message_sent.png)

### Invoice

![Invoice endpoint](./evidences/steps/step_7.3_invoice_endpoint_test.png)

Is able to receive the message produced by pay-app successfully

![Invoice kafka](./evidences/steps/invoice_logs_evidence.png)


## Transaction

![Transaction endpoint](./evidences/steps/step_2_transaction_endpoint_test.png)

Is able to receive the message produced by pay-app successfully

![Transaction kafka](./evidences/steps/step_2_transaction_recevied_message.png)
# Microservicios con Spring Boot

![Architecture](./resources/microservicesarchitecture.png)

## Información de los microservicios
El microservicio de invoices, debe listar las facturas de clientes y además debe consumir una cola para cambiar el estado de la factura cuando esta se paga a través del microservicio de pago.
El microservicio de pago debe registrar el pago en su respectiva bd y además debe dejar un mensaje en una cola para actualizar la factura en el microservicio de facturas y además debe dejar un mensaje en una cola para registrar el movimiento en el microservicio de transacciones.
El microservicio de transacciones debe listar las transacciones de una factura, además debe consumir una cola para obtener las transacciones de pago del microservicio de pago.
Todos los microservicios deben consumir la cadena de conexión desde el servicio de configuración centralizada.

La información de los endpoints disponibles por microservicio se incluyen en el documento de INFO.md
## Scripts de creación de bases de datos

La informacion de como crear las bases de datos y sus respectivas tablas se incluyen en google.com

## Tecnologías utilizadas

- Spring Boot (Java Framework JDK v11+)
- Gradle (Gestor de dependencias)
- Postman (Test de endpoints/servicios rest)
- Postgresql (Base de Datos)
- MySQL (Base de Datos)
- MongoDB (Base de Datos NoSQL)
- Kafka (Gestor de Mensajería)
- Github (Repositorio para proyecto y Configuraciones de micorservicios)
