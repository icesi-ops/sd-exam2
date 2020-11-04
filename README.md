# Segundo parcial Sistemas Distribuidos

## Integrantes

**John Sebastian Urbano Lenis -----> A00292788**

**Jeferson Lerma Giron -----> A00301456**

**Jessica Alejandra Sánchez -----> A00030124**


## Examen 2
## Universidad ICESI
## Curso: Sistemas Distribuidos
**Docente: Joan Sebastián Garcìa Delgado**
**Tema: Automatización de infraestructura (Docker)**
**Correo: joan.garcia1 at correo.icesi.edu.co**

## Objetivos

Realizar de forma autónoma el aprovisionamiento automático de infraestructura
Diagnosticar y ejecutar de forma autónoma las acciones necesarias para lograr infraestructuras estables
Integrar servicios ejecutándose en nodos distintos

## Resumen general


El parcial consistía en utilizar las herramientas vistas en clase, algunas de ellas eran:
Aprovisionamiento con Ansible
Vagrant
Docker
Docker-compose
Docker swamr
La idea es desplegar aplicaciones compuestas de un backend y frontend para tener la oportunidad de escalarlas.

### Procedimiento


En primer lugar, tenemos el vagrantfile, que despliega las 4 máquinas virtuales de la siguiente manera:
* CentOS7 Master
* CentOS7 Worker1
* CentOS7 Worker2
* CentOS7 Worker3
Adicionalmente, este vagrantfile hace uso de ansible para el aprovisionamiento de las máquinas virtuales; en este caso en particular instala algunas herramientas como docker y docker compose para efectos académicos:

Cómo se aprecia anteriormente, se configuró en el vagrantfile el contenedor Master, se aprovisionó el mismo, se definieron los workers y también se asoció el aprovisionamiento con Ansible.


Una vez hecho esto se procedió a hacer uso de docker swarm. Esta parte se hizo manualmente de la siguiente manera:
Se inicia sesión mediante ssh en el nodo maestro. Se corren los siguientes comandos:

```
ip=$(hostname -I | awk '{print $2}')
docker swarm init --advertise-addr $ip
```

Esto con el fin de generar un token para conectar los workers con el master:

Token que posteriormente se ingresa a los workers para que queden conectados al swarm.
Se crean los volúmenes dentro de las máquinas, necesarios para la persistencia de la base de datos:
Esto se logra mediante el comando:

```
sudo docker volume create --name <name of volume> --opt type=tmpfs  --opt device=tmpfs  --opt 0=size=<size in mb>m
```

Esto se hace en cada una de las máquinas. Y con el comando:

```
docker inspect <name of volume>
```
Podemos saber la ubicación de ese volumen.

Ahora, se crea un docker-compose, con el fin de desplegar los contenedores necesarios para el despliegue de la aplicación:

Una vez, se tiene el docker-compose.yml listo. Se puede empezar a correr a modo de servicio mediante la herramienta docker stack.

Con esta herramienta podemos escalar el servicio mediante el parámetro  réplicas.

Para correr el archivo docker-compose.yml a modo de servicio usamos el comando:
