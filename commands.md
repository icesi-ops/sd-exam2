## Creacion de la red
docker network create distribuidos

## Creacion de bases de datos
docker run -p 5432:5432 --name postgres --network distribuidos -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=db_invoice -d postgres:0.1.0

docker run -p 3306:3306 --name mysql --network distribuidos -e MYSQL_ROOT_PASSWORD=mysql -e MYSQL_DATABASE=db_operation -d mysql:0.1.0

docker run -p 27017:27017 --network distribuidos --name mongodb -d mongo

## Creacion de Kafka
docker run -p 2181:2181 -d -p 9092:9092 --name servicekafka --network distribuidos -e ADVERTISED_HOST=servicekafka -e NUM_PARTITIONS=3 johnnypark/kafka-zookeeper


#### consul ####
Modificar el archivo de application.properties de cada servicio (Descomentar lo de consul)(pay-app-spring-microservices/app-config/src/main/resources/application.properties)

Añadir la linea (Descomentar) implementation 'org.springframework.cloud:spring-cloud-starter-consul-discovery' into build.gradle depedencies

Instalar dnsmasq y crear un archivo de configuracion en el path /etc/dnsmasq.d en donde se incluya:
server=/consul/127.0.0.1#8600

Iniciar el servicio de dnsmasq (systemctl restart dnsmasq)

Modificar el archivo /etc/resolv.conf y agregar la siguiente linea (Esto hace que tome a consul como servidor DNS)
nameserver <consul dir>

Correr el comando: dig app-service.service.consul

Desplegar el contenedor usando:
docker run -d -p 8500:8500 -p 8600:8600/udp --network distribuidos --name consul consul:latest agent -server -bootstrap-expect 1 -ui -data-dir /tmp -client=0.0.0.0 


## Configuracion de imagenes
Aqui se debe ingresar en cada una de las carpetas de los servicios y se debe crear la imagen:
docker build -t icesiops/nombreservicio:tag .

docker build -t icesiops/appconfig:0.1.0 app-config/.
docker build -t icesiops/appinvoice:0.1.0 app-invoice/.
docker build -t icesiops/apppay:0.1.0 app-pay/.
docker build -t icesiops/apptransaction:0.1.0 app-transaction/.

Despues de creada las imagenes, se deben desplegar usando: (Importante attachar la red)

docker run -p 8888:8888 --network distribuidos --name app-config icesiops/appconfig:0.1.0
docker run -d -p 8006:8006 --network distribuidos --name app-invoice icesiops/appinvoice:0.1.0
docker run -d -p 8010:8010 --network distribuidos --name app-pay icesiops/apppay:0.1.0
docker run -d -p 8082:8082 --network distribuidos --name app-transaction icesiops/apptransaction:0.1.0

psql -h localhost -d db_invoice -U postgres -f data.sql


### Load Balancer ####
Crear dockerfile con esta informacion  
FROM haproxy:2.3
COPY haproxy.cfg /usr/local/etc/haproxy/haproxy.cfg

Crear la configuracion del  haproxy

defaults
   timeout connect 5s
   timeout client 1m
   timeout server 1m

frontend stats
   bind *:1936
   mode http
   stats uri /
   stats show-legends
   no log

frontend http_front
   bind *:80
   default_backend http_back

backend http_back
    balance roundrobin
    server-template mywebapp 1-10 _web._tcp.service.consul resolvers consul resolve-opts allow-dup-ip resolve-prefer ipv4 check

resolvers consul
    nameserver consul 127.0.0.1:8600
    accepted_payload_size 8192
    hold valid 5s

Construimos la imagen y corremos el contenedor

docker build -t icesiops/loadbalancer:0.1.0 .
docker run -d -p 9000:80 --network distribuidos --name loadbalancer icesiops/loadbalancer:0.1.0


### Application Gateway
Para guardar datos de identidad, creamos un storage como Redis.

docker run --network distribuidos -d --name express-gateway-data-store \
                -p 6379:6379 \
                redis:alpine


2. Start the Express-Gateway instance
Run the command inside appgw directory o keep in mind change the volume path to pointing to gateway.config.yml

docker run -d --name express-gateway \
    --network distribuidos \
    -v .:/var/lib/eg \
    -p 8080:8080 \
    -p 9876:9876 \
    express-gateway

3. uncoment #key-auth
4. connect to gw container
docker exec -it express-gateway sh

5. create users
eg users create

6. assign auth key
eg credentials create -c sebas -t key-auth -q

7. copy key 0tQLw8jIfq6Zq0lDDJH7Of:39wh7qxmNPgJMvlB9EywAS

8. Curl API endpoint as Sebas  with key credentials - SUCCESS!

curl -H "Authorization: apiKey ${keyId}:${keySecret}" http://localhost:8080/config/app-pay/dev

curl -H "Authorization: apiKey 7ArDYacgLpKYNiFKd160TR:3NpZ7m1J7cjWJWKAQPnvA2" http://localhost:8080/config/app-pay/dev
7ArDYacgLpKYNiFKd160TR:3NpZ7m1J7cjWJWKAQPnvA2