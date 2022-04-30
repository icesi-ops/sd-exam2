#### Maria Diomar Ordoñez Ordoñez, A00355009

# Kubernetes
## Namespace 
Primero se creo el archivo pay-namespace.yml de tipo Namespace que nos permite tener nuestro propio namespace para que todos los objetos esten dentro de este, y así se puedan ver entre ellos. 

    name: payapp-namespace

##Deployment microservicio ConfigServer

Luego se creo el archivo appconfig-deployment.yml de tipo Deployment para el despliegue del microservicio ConfigService, que declara 6 replicas y la estrataefia RollingUpdate con máximo 2 pods unvailable y 3 maxSurge.  Las configuraciones las busca en el repositorio https://github.com/OrdonezMaria1/training_microservices  

##Service para el tráfico hacia el microservicio ConfigServer, tipo NodePort

Para el para el tráfico hacia el microservicio ConfigServer se creo un Servicio tipo NodePort que se puede visualizar en la parte de abajo del archivo appconfig-deployment.yml

##Deployment microservicio Transaction

Para el microservicio Transaction se creo un archivo con el nombre apptransaction-deployment.yml que realiza 2 replicas y utiliza una estrategia RollingUpdate con máximo 1 pods unvailable y 1 maxSurge 

##Service para el tráfico hacia el microservicio Transaction, tipo LoadBalance

Para el para el tráfico hacia el microservicio Transaction se creo un Servicio tipo LoadBalance que se puede visualizar en la parte de abajo del archivo aapptransaction-deployment.yml

### Replicaset para el despliegue de la base de datos del microservicio, Mongo  

Para la base de datos se creo el archivo mongo-replicaset.yaml que realiza 2 replicas de este. 

### Service para el tráfico hacia la BD ClusterIP

Para este se utilizo el archivo kafka-deployment.yml. 

##Evidencias

##Pods 
![image](https://user-images.githubusercontent.com/47904094/166125215-b52c03a3-cb91-4b42-9dfa-814cb90a4b29.png)
##Services
![image](https://user-images.githubusercontent.com/47904094/166125240-02a93111-6cde-43cc-a867-8b0fcbca57c2.png)
##Deployments
![image](https://user-images.githubusercontent.com/47904094/166125259-13dfdd4c-e43b-4db5-bb2e-3eae5e84c2e9.png)
##Replicaset
![image](https://user-images.githubusercontent.com/47904094/166125292-96bffa55-3aa0-4a36-8a4a-fedee102857d.png)

###Orden de creación 
Primero se creo el namespace, luego el microservici de configuración, luego la base de datos Mongo y Kafka y por ultimo el microservicio de transación. 






