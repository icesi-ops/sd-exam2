# CONFIGURACIÓN PREVIA
## Agregar la sección #consul en el archivo "application.properties" de cada servicio.
```bash
    # Consul
    spring.cloud.consul.host=consul
    spring.cloud.consul.port=8500
    spring.cloud.consul.discovery.health-check-interval=5s
    spring.cloud.consul.discovery.prefer-ip-address=true
```
- app-invoice/src/main/resources/application.properties
- app-pay/src/main/resources/application.properties
- app-transaction/src/main/resources/application.properties

## Añadir dependencias en build.gradle de cada servicio.
implementation 'org.springframework.cloud:spring-cloud-starter-consul-discovery' into
implementation 'org.springframework.boot:spring-boot-starter-actuator'

## Instalar dnsmasq
```bash
sudo apt install dnsmasq
sudo touch /etc/dnsmasq.d/10-consul
sudo nano /etc/dnsmasq.d/10-consul
```
server=/consul/127.0.0.1#8600

## Iniciar el servicio de dnsmasq 
Esto hace que tome a consul como servidor DNS
```bash
sudo nano /etc/resolv.conf
```
nameserver 127.0.0.1
```bash
systemctl restart dnsmasq
```

## Loadbalancer configuration
En haproxy/haproxy.cfg
```bash
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
   mode http
   acl url_config path_beg /config
   use_backend config_back if url_config
   acl url_pay path_beg /pay
   use_backend pay_back if url_pay
   acl url_invoice path_beg /invoice
   use_backend invoice_back if url_invoice
   acl url_transaction path_beg /transaction
   use_backend transaction_back if url_transaction

   default_backend http_back

backend config_back
    mode http
    balance roundrobin
    http-request set-path "%[path,regsub(^/config/,/)]"
    server appconfig app-config.service.consul:8888 resolvers consul resolve-prefer ipv4 check
backend pay_back
    mode http
    balance roundrobin
    http-request set-path "%[path,regsub(^/pay/,/)]"
    server apppay app-pay.service.consul:8010 resolvers consul resolve-prefer ipv4 check
backend invoice_back
    mode http
    balance roundrobin
    http-request set-path "%[path,regsub(^/invoice/,/)]"
    server appinvoice app-invoice.service.consul:8006 resolvers consul resolve-prefer ipv4 check
backend transaction_back
    mode http
    balance roundrobin
    http-request set-path "%[path,regsub(^/transaction/,/)]"
    server apptransaction app-transaction.service.consul:8082 resolvers consul resolve-prefer ipv4 check
backend http_back
    mode http
    balance roundrobin
    server-template mywebapp 1-10 _web._tcp.service.consul resolvers consul resolve-prefer ipv4 check

resolvers consul
    nameserver consul consul:8600
    accepted_payload_size 8192
    hold valid 5s 

```
## apigateway configuration
En appgw/gateway.config.yml
```yml
http:
  port: 8080
admin:
  port: 9876
  host: localhost
apiEndpoints:
  appconfig:
    host: localhost
    paths: ['/config','/config/*']
  appinvoice:
    host: localhost
    paths: ['/invoice','/invoice/*']
  apppay:
    host: localhost
    paths: ['/pay','/pay/*']
  apptransaction:
    host: localhost
    paths: ['/transaction','/transaction/*']          
serviceEndpoints:
  appconfig:
   url: 'http://loadbalancer/config/'
  appinvoice:
   url: 'http://loadbalancer/invoice/'
  apppay:
   url: 'http://loadbalancer/pay/'
  apptransaction:
   url: 'http://loadbalancer/transaction/'     
policies:
  - basic-auth
  - cors
  - expression
  - key-auth
  - log
  - oauth2
  - proxy
  - rate-limit
pipelines:
  default:
    apiEndpoints:
      - appconfig
      - appinvoice
      - apppay
      - apptransaction
    policies:
    # Uncomment `key-auth:` when instructed to in the Getting Started guide.
      - key-auth:
      - proxy:
        - action:
            serviceEndpoint: appconfig 
            changeOrigin: true
            prependPath:  false
            ignorePath:   false
            stripPath:    false
        - action:
            serviceEndpoint: appinvoice 
            changeOrigin: true
            prependPath:  false
            ignorePath:   false
            stripPath:    false
        - action:
            serviceEndpoint: apppay 
            changeOrigin: true
            prependPath:  false
            ignorePath:   false
            stripPath:    false
        - action:
            serviceEndpoint: apptransaction 
            changeOrigin: true
            prependPath:  false
            ignorePath:   false
            stripPath:    false                                          
```
# BUILD
Crear build.sh
```bash
#!/bin/bash
sudo docker build -t icesiops/appconfig:0.1.0 app-config/.
sudo docker build -t icesiops/appinvoice:0.1.0 app-invoice/.
sudo docker build -t icesiops/apppay:0.1.0 app-pay/.
sudo docker build -t icesiops/apptransaction:0.1.0 app-transaction/.
sudo docker build -t icesiops/loadbalancer:0.1.0 haproxy/.
exit
```
Dar permisos de ejecución al archivo:
sudo chmod +x build.sh
Ejecutarlo:
./build.sh

# RUN
Crear docker compose
```yml
version: '3.7'
services:
  postgres:
    image: postgres:latest
    restart: always
    container_name: postgres
    ports:
      - "5432:5432"
    environment:
      - POSTGRES_PASSWORD=postgres
      - POSTGRES_DB=db_invoice 
    networks:
      - distribuidos
  mysql:
    image: mysql:latest
    restart: always
    container_name: mysql
    ports:
      - "3306:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=mysql
      - MYSQL_DATABASE=db_operation 
    networks:
      - distribuidos
  mongodb:
    image: mongo
    restart: always
    container_name: mongodb
    ports:
      - "27017:27017"
    networks:
      - distribuidos
  servicekafka:
    image: johnnypark/kafka-zookeeper
    restart: always
    container_name: servicekafka
    ports:
      - "2181:2181"
      - "9092:9092"
    environment:
      - ADVERTISED_HOST=servicekafka 
      - NUM_PARTITIONS=3
    networks:
      - distribuidos 
  consul:
    image: consul:latest
    restart: always
    container_name: consul
    command: "agent -server -bootstrap-expect 1 -ui -data-dir /tmp -client=0.0.0.0"
    ports:
      - "8500:8500"
      - "8600:8600/udp"
    networks:
      - distribuidos  
  appconfig:
    #build: ./app-config
    image: icesiops/appconfig:0.1.0
    restart: always
    container_name: app-config
    ports:
      - "8888:8888"
    networks:
      - distribuidos 
    depends_on:
      - servicekafka
      - consul
  appinvoice:
    #build: ./app-invoice
    image: icesiops/appinvoice:0.1.0
    restart: always
    container_name: app-invoice
    ports:
      - "8006:8006"
    networks:
      - distribuidos
    depends_on:
      - appconfig
      - postgres
      - servicekafka
      - consul
  apppay:
    #build: ./app-pay
    image: icesiops/apppay:0.1.0
    restart: always
    container_name: app-pay
    ports:
      - "8010:8010"
    networks:
      - distribuidos
    depends_on:
      - appconfig
      - mysql
      - servicekafka
      - consul
  apptransaction:
    #build: ./app-transaction
    image: icesiops/apptransaction:0.1.0
    restart: always
    container_name: app-transaction
    ports:
      - "8082:8082"
    networks:
      - distribuidos
    depends_on:
      - appconfig
      - mongodb
      - servicekafka
      - consul
  loadbalancer:
    #build: ./haproxy
    image: icesiops/loadbalancer:0.1.0
    restart: always
    container_name: loadbalancer
    ports:
      - "9000:80"
    networks:
      - distribuidos
    depends_on:
      - appconfig
      - appinvoice
      - apppay
      - apptransaction
  expressgatewayds:
    image: redis:alpine
    container_name: express-gateway-data-store
    restart: always
    ports:
      - "6379:6379"
    networks:
      - distribuidos
    depends_on:
      - loadbalancer
  expressgateway:
    image: express-gateway
    container_name: express-gateway
    restart: always
    ports:
      - "9876:9876"
      - "8080:8080"
    volumes:
      - ./appgw:/var/lib/eg
    networks:
      - distribuidos
    depends_on:
      - loadbalancer
      - expressgatewayds
networks:
  distribuidos:
    name: distribuidos
    external: false
```
```bash
# En versiones anteriores docker-compose up
sudo docker compose up
```
# TEST
Ingresamos datos a la base de datos postgres
```bash
sudo apt install postgresql
psql -h localhost -d db_invoice -U postgres -f resources/postgres/data.sql
```
## Prueba consul
En un navegador web ingresamos a:http://localhost:8500 y en services deberian de aparecer nuestros servicios.

## Prueba loadbalancer
En un navegador web ingresamos a: http://localhost:9000/invoice/all
El servicio estará bien si obtenemos el siguiente resultado:
```json
[{"idInvoice":1,"amount":1000.0,"state":0},{"idInvoice":2,"amount":5000.0,"state":1},{"idInvoice":3,"amount":300.0,"state":0},{"idInvoice":4,"amount":600.0,"state":0},{"idInvoice":5,"amount":400.0,"state":0}]
```
## Prueba apigateway
1. uncoment #key-auth
2. connect to gw container:
```bash
docker exec -it express-gateway sh
```
3. create users
```bash
eg users create
```
Pondremos un name, lastname y username, el resto se puede dejar en blanco.
4. assign auth key
Como mi username es jhons, el comando fue:
```bash
eg credentials create -c jhons -t key-auth -q
```
Esto me generará una key, la copiamos ya que con eso se tendrá acceso a los servicios.
5. Accedemos a un servicio.
curl -H "Authorization: apiKey ${keyId}:${keySecret}" http://localhost:8080/config/app-pay/dev
```bash
curl -H "Authorization: apiKey 0EwRG05svapldaoGlGXtge:3VrJZzo4wt9X299IdD1hee" http://localhost:8080/config/app-pay/dev
```
