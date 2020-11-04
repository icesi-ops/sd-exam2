# Parcial 2 - Sistemas distribuidos 2020-2

## Integrante

Juan David Paz Dorado - **A00328036**


Este es el repositorio correspondiente al segundo parcial de sistemas distribuidos. 
Incluye los archivos necesarios para poder realizar el aprovisionamiento de la plataforma requerida en el enunciado principal. La cual consta de un servicio backend y un servicio de base de datos que se unen por medio de la herramienta docker-compose para su funcionamiento. 


## Descripción general 

Para realizar exitosamente el parcial, se necesitó de cuatro maquinas que corresponden a: 
**1.** Maquina Master.  **-->** 192.168.50.10

**2.** Maquina Nodo 1.      **-->**         192.168.50.11

**3.** Maquina Nodo 2. 		 **-->**		192.168.50.12

**4.** Maquina Nodo 3. 	 **-->**		192.168.51.13

Para el despliegue automático de estas maquinas se utilizó la herramienta **Vagrant**. Para la gestión de la configuración de las maquinas virtuales se uso **Ansible**.Y para las conexiones de los servicios y su debido uso se utilizo **docker** y **docker-compose**

El sistema operativo de las maquinas anteriormente mencionadas fue **centos/7**

**Uso de Vagrant**
Para hacer uso de esta herramienta se debe saber la manera de trabajar con Ansible, Con lo cual se define en Vagrantfile la creación de las cuatro maquinas (una master y tres nodos). Y por supuesto las cuatro configuraciones de playbooks que deben correr en las maquinas cuando se inicien. Este procedimiento sera explicado en la sustentación.

Ya con el archivo Vagrantfile creado, se procede a ejecutar el siguiente comando

    vagrant up 


**Uso de Ansible**

Para utilizar esta herramienta es primordial conocer la estrategia de playbooks, las cuales son las "instrucciones" que se indicaran a cada maquina. En este parcial, se tienen cuatro playbooks que son necesario para el funcionamiento del mismo.

**1.** dependencies_playbook

En este archivo estan todas las herramientas que se deben instalar en las maquinas para su funcionamiento. Docker, Docker-compose, Glusters. Y

**2.** swarm_playbook

En el primer parcial, la relación "maestro-esclavo" se realizaba por medio SaltStack en el Vagrantfile. En este caso, esta relación se hará por medio de este playbooks. En donde estamos definiendo la maquina master como la principal y los nodos como trabajadores, Todo esto por medio del intercambio de tokens, propios de la herramienta swarm.

**3.** gluster_playbooks

En este playbook esta configurada el tema de almacenamiento de los nodos. En donde se comprueba que estan conectadas, se crean los volúmenes. Y se configura para que sea persistentes tanto el Master como en nodos trabajadores.

**4.** files_playbooks

En este playbook se esta transfiriendo los archivos necesarios para poder correr la aplicación. La carpeta correspondiente a la db. La carpeta correspondiente al backend. Y el archivo docker-compose.yml para poder correr la aplicación.



## Backend

Para el backend se utilizo el lenguaje python. Se definieron tres cosas importantes. 

Primero una archivo requirements.txt en donde se llama la herramienta Flask y mysql-connector, las cuales son requeridos para su funcionamiento.

Segundo, un arhivo app.py en donde se define la conexión con la base datos para poder realizar el correspondiente SELECT.

Tercero, un archivo Dockerfile en donde se construirá una imagen de dicha configuración. En este archivo se esta utilizando el puerto 5000 y se ejecuta el archivo app.py


## Base de datos

Para la realización de la base de datos se utilizo mysql. La base de datos consiste en una tabla celulares, en donde se pueden hacer consultas de tipo SELECT.

**1.** Lo primero que se realizó fue crear el archivo init.sql donde esta explicitamente la creación de la base de datos, de la tabla y los INSERT INTO para llenar con algunos datos de pruebas.

**2.** Tener presente hacer uso de una imagen mysql para poder tener lista las configuraciones de base que son necesario para correr la base de datos.


## Integración

 - La primera parte de la Integración se realiza cuando se ejecute el swarm_playbook. Pues como mencionaba antes, aquí es donde se unen las maquinas y se configuran para trabajar de manera organizada y jerárquica, pues se tiene un líder y tres trabajadores.

Para comprobar que esto funciona correctamente se debe ejecutar después del vagrant up el siguiente comando.

De esta manera podemos comprobar de manera visual que hay cuatro nodos listos para trabajar, y en donde uno esta definido como el líder.


    docker node ls


 - La segunda parte de la integración se realiza por medio del archivo docker-compose.yml. En este archivo se esta construyendo todo lo necesario para correr los dos servicios. Y lo mas importante, se unen por medio de la función "links" la base de datos al backend. Esta manera es super optima pues no es necesario usar direcciones ip para buscar la base de datos, evitando así problemas en la conexión.

Luego de tener lo anterior realizado, se debe iniciar la aplicación con el siguiente comando.

    docker-compose up

## Problemas encontrados

**1.** El primer problema que se encontró fue el desconocimiento que se tenía sobre algunas herramientas. Con lo cual fue muy necesario hacer uso de los videos de las clases e información en google que ayudará a solucionar el problema. 

**2.** Hubo muchos problemas con la nomenclatura de los playbooks. Aunque esta herramienta ya fue usada anteriormente, para este parcial fue necesario usar muchas instrucciones y diferentes nombres de funciones que ayudaran a configurar las maquinas de la mejor manera. Al igual que el punto 1, este problema fue solucionado poco a poco por medio de busquedas en google.

**3.** Hubo un problema con la creación de los discos en el Vagrantfile, el rendimiento de la maquina disminuyo, con lo cual fue necesario bajar el tamaño de los discos de cinco Gb a una Gb.

**4** De igual manera, hubo un problema mínimo al momento de realizar commits en el repositorio. Debido al tamaño de los discos. Esto se soluciono de manera sencilla creando un .gitignore

**5** El ultimo y principal problema que aún no se ha podido solucionar, es que la aplicación falla al momento de realizar solicitudes curl. Se probo de muchas maneras para solucionar este error pero aun no se ha podido corregir.
 
