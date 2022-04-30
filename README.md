# sd-exam2
***Mateo Gallego Ramirez -A00347937***

## Creacion name space
Como primer paso se creo el *name space* **payapp**, este sera utilizado para el despluegue de todos los servicios necesarios. ejecute tambien:
``kubectl config set-context --current --namespace=payapp``

## Despliegue del servicio app-config
Como primer paso se modifico el código para eliminar el servicio de **Consul** del archivo ``build.gradle``  dentro de la carpeta de ``app-config`` comentando la línea N° 21:

``//    implementation 'org.springframework.cloud:spring-cloud-starter-consul-discovery'``

Posteriormente se creo la imagen del contenedor tomando el archivo ``Dokerfile`` dentro de la carpeta de ``app-config`` y fue subida a  **Dockerhub** con el nombre de ``ultrakiwi/appconfig:v.1.2``

Con esta imagen se creo el archivo de deployment  ``appconfig-deployment.yaml`` que encontrara en la carpeta ``files`` de este repositorio. Para ejecutar el alrchivo debe ejecutar el siguiente comando sobre la carpeta  que contiene el archivo:
``kubectl apply -f appconfig-deployment.yaml``

## Despliegue del servicio app-transaction
Al igual que en el servicio anterior se deben eliminar las de pendencias con **Consul**,  para posteriormente  posteriormente crear crear y subir la imagen del servicio a **Dockerhub** en este caso bajo el nombre ``ultrakiwi/app-transaction``.
Igualmente se creo el archivo de deplayment ``apptransaction-deployment.yaml`` ubucado en la carpeta ``files``. Para ejecutar el alrchivo debe ejecutar el siguiente comando sobre la carpeta  que contiene el archivo:
``kubectl apply -f apptransaction-deployment.yaml``

## Despliegue del servicio MongoDB
para este se creo el deployment de la base de datos usando la imagen oficial de MongoDB. el archivo de deployment es ``db-deployment.yaml``.  Para ejecutar el alrchivo debe ejecutar el siguiente comando sobre la carpeta  que contiene el archivo:
``kubectl apply -f db-deployment.yaml``

## Servicio de LoadBalancer de Kubernetes
Ahora que los servicios estan desplgados necesitamos poder acceder a ellos y para esto haremos uso del servicio de *Load Balancer de* **Kubernetes** que se encarga de poder dar acceso a los demás servicios y balancearlos. Para esto hicimos uso del archivo ``lb-service.yaml`` y ejecutamos el siguiente comando sobre la carpeta que contiene el archivo:
``kubectl apply -f lb-service.yaml``