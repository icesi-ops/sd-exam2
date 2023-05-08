# 📑Microservicios con Spring Boot - Parcial 2

![Architecture](https://github.com/icesi-ops/training_microservices/raw/master/pay-app-spring-microservices/resources/microservicesarchitecture.png)

El microservicio de invoices, debe listar las facturas de clientes y además debe consumir una cola para cambiar el estado de la factura cuando esta se paga a través del microservicio de pago. El microservicio de pago debe registrar el pago en su respectiva bd y además debe dejar un mensaje en una cola para actualizar la factura en el microservicio de facturas y además debe dejar un mensaje en una cola para registrar el movimiento en el microservicio de transacciones. El microservicio de transacciones debe listar las transacciones de una factura, además debe consumir una cola para obtener las transacciones de pago del microservicio de pago. Todos los microservicios deben consumir la cadena de conexión desde el servicio de configuración centralizada.

La información de los endpoints disponibles por microservicio se incluyen en el documento de INFO.md


## 👨‍💻Presentado por

 - Jose Luís Restrepo Obando - A00361597
 
## 📚Tecnologías utilizadas

- Spring Boot `Java Framework JDK v11+`
- Gradle `Gestor de dependencias`
- Postman `Test de endpoints/servicios rest`
- Postgresql `Base de Datos`
- MySQL `Base de Datos`
- MongoDB `Base de Datos NoSQL`
- Kafka `Gestor de Mensajería`
- Github `Repositorio para proyecto y Configuraciones de micorservicios`

## ✅Cambios realizados
Teniendo en cuenta los avances realizados en las clases, ahora debemos realizar algunos cambios. Para empezar debemos:
1. Realizar el `build` y el `run` de:
	- SQL
	- Postgres `(Recordar cargar datos en la DB)`
	- Mongo `(run)`
	- Kafka `(run)`
	-  Consul `(run)`
2. En el archivo [haproxy.cfg](pay-app-spring-microservices/haproxy/haproxy.cfg) se deben agregar el direccionando las solicitudes HTTP que contienen `/pay`, `/invoice` o `/transaction` en la ruta de la URL a diferentes backends, que también se deben agregar, para permitir la distribución del tráfico y la gestión de la carga en diferentes servidores.
3. En [gateway.config.yml](pay-app-spring-microservices/appgw/gateway.config.yml) se agregaron nuevos endpoints API (`appinvoice`, `apppay`, `apptransaction`) con sus correspondientes `paths` y `serviceEndpoints`. Además, se actualizaron las acciones del pipeline `default` para dirigir las solicitudes a los nuevos endpoints correspondientes.
4. Ahora debemos agregar los servicios de `Consul` y agregar el módulo `Spring Boot`. En el `build.gradle` de:
	- [app-invoice](pay-app-spring-microservices/app-invoice/build.gradle)
	- [app-pay](pay-app-spring-microservices/app-pay/build.gradle)
	- [app-transaction](pay-app-spring-microservices/app-transaction/build.gradle)
5. Debemos dejar la configuración de conexión y descubrimiento de los servicios a través de `Consul`. Dicha configuración se debe agregar en el `application.properties` de cada servicio:
	- [app-invoice](pay-app-spring-microservices/app-invoice/src/main/resources/application.properties)
	- [app-pay](pay-app-spring-microservices/app-pay/build.gradle)
	- [app-transaction](pay-app-spring-microservices/app-transaction/build.gradle)
6. En los archivos de configuración de `Spring Boot` se debe también agregar la configuración que se agregó en el paso 5. Finalmente cada archivo quedaría así:
	- [app-invoice-dev](pay-app-spring-microservices/config/app-invoice-dev.properties)
	- [app-pay-dev](pay-app-spring-microservices/config/app-pay-dev.properties)
	- [app-transaction-dev](pay-app-spring-microservices/config/app-transaction-dev.properties)
7. Para continuar, debemos realizar el `build` y el `run` de:
	- app-config
	- app-invoice
	- app-pay
	- app-transaction
	- haproxy `(loadbalancer)`
8. Por ultimo hacemos el `run` para la `gateway` y generamos las `apikey` para realizar el proeso de autorización.

## 👾Demo
[Start Demo]()
