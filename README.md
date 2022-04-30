# sd-exam2
Examen 2
Universidad ICESI
Curso: Sistemas Distribuidos
Docente: Joan S. Garcia D.
Tema: Orquestación de Contenedores (k8s)
Autor: María Alejandra Estacio. 

El sistema de pagos trabajado para la primera parte del semestre ha dado sus frutos y la compañía "MyPay" ha decidido comprar el código fuente y contratarlo a usted para el desarrollo del despliegue de los servicios usando Kubernetes como herramienta de orquestación de contenedores. Por lo que en una fase inicial ha solicitado hacer un PoC de como sería el despliegue del microservicio de config, kafka y algún otro microservicio del sistema (pay, transaction, invoices) con su respectiva DB.


Para el desarrollo del sistema del , primero cree el archivo .yml para el namespace con el nombre appinvoice-ns.yml, luego subí las imagenes de cada uno de los microservicios y de las bases de datos correspondientes a docker hub. 

Una vez teniendo listas las imagenes y el namespace, cree el Deployment correspondiente para el microservicio ConfigServer con 6 replicas y un RollingUpdate con máximo 2 pods unvailable y 3 maxSurge, además del service de tipo NodePort. Tambien cree el Deployment para el despliegue de los microservicio app-invoice con 2 replicas y un RollingUpdate con máximo 1 pods unvailable y 1 maxSurge, con un servicio tipo LoadBalancer, por otro lado, para que el despliegue de app-invoice funcione de forma correcta, se necesita de app-pay, por lo tanto, cree un Deployment para app-pay con un servicio tipo ClusterIP.

Finalmente, cree dos recursos tipo ReplicaSet para el despliegue de las bases de datos postgres y MySql, cada una con un service tipo ClusterIP. 
Para comprobar el estado de cada uno de los pods creados hice uso del comando Kubectl get pods, como se puede observar en la imagen de evidencia.jpg, pero los pods iniciaban corretacmente y despues de unos segundos cambiaba el estado del pod del microservicio app-pay por error. 





