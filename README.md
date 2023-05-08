# Microservicios con Spring Boot

En este repositorio se despliega un Application Gateway, Haproxy, SpringBoot Microservices y Data Bases. 

Los pasos para realizar este proyecto se encuentran en el archivo [Guia de comandos](commands.md).

## Pasos 
De forma sencilla, los pasos teoricos a seguir son:

1. Construir (Build) y desplegar (run) las bases de datos (Mongo, SQL y Postgres)
2. Construir (Build) y desplegar (run) el servidor de Consul  
3. Agregar la dependencia de consul en todos los properties de los servicios, al igual que agregar las lineas de configuracion de consul en el gradle.
4. Construir (Build) y desplegar (run) los servicios (Config, Invoice, Pay y Transactions)
5. Editar el archivo de haproxy.cfg con la informacion del backend de los servicios
6. Construir (Build) y desplegar (run) el loadbalancer 
7. Crear la base de datos Redis para el Application Gateway
8. En el directorio de Appgw, desplegar el Application Gateway
9. Acceder al contenedor de AG (docker exect)
10. Crear un usuario (eg create user) y generar una key (eg credentials create -c user -t key-auth -q)
11. Realizar un curl -H "Authorization: apiKey ${keyId}:${keySecret}" http://localhost:8080/config/app-pay/dev

EJEMPLO: key 7ArDYacgLpKYNiFKd160TR:3NpZ7m1J7cjWJWKAQPnvA2
curl -H "Authorization: apiKey 7ArDYacgLpKYNiFKd160TR:3NpZ7m1J7cjWJWKAQPnvA2" http://localhost:8080/config/app-pay/dev

Para acceder a los servicios usando el loadbalancer:
localhost:9000/{app-name}


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
