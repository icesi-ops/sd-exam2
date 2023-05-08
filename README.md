# sd-exam2 - Microservicios con Spring Boot
Aquí hemos desarrollado la siguiente arquitectura de microservicios, en la cual podemos acceder a las funcionalidades de los diferentes microservicios a través de un API Gateway, que por detrás se comunica con un Balanceador de carga y este a su vez con un Servidor de Consul que tiene registrados los microserivicios como agentes de Consul:

![Architecture](https://github.com/icesi-ops/training_microservices/blob/master/pay-app-spring-microservices/resources/microservicesarchitecture.png)

### Problemas encontrados:
- Hacer que el health check que realiza el servidor de Consul con sus agentes funcionara para los microservicios.

### Guía de ejecución:
Los comando necesarios para correr las contenedores se encuentran en el archivo `commands.md`
1. Iniciamos por construir y correr las imagenes de las bases de datos.
2. Instalamos dnsmasq
3. Creamos un archivo de configuración para dnsmasq (10-consul) en la ruta /etc/dnsmasq.d con el contenido `server=/consul/127.0.0.1#8600`
4. Iniciamos el servicio de dnsmasq
5. Modificamos el archivo /etc/resolv.conf y le añadimos la línea `nameserver 127.0.0.1` para que haga de serivor dns.
6. Corremos la imagen del Servidor Consul.
7. Construimos y corremos las imágenes de los microservicios.
8. Construimos y corremos la imagen del Balanceador de carga (haproxy).
9. Corremos las imagenes del express-gateway-store y el express-gateway.
<br>En este punto ya podemos acceder a los microservicios a través del API Gateway, pero nos dará un error de Unauthorised, porque no hemos proporcionado credenciales de autenticación, así que para esto:
10. Ingresamos al contenedor del express-gateway y nos creamos un usuario con `eg users create`.
11. Generamos las llaves de autenticación para nuestro usuario con `eg credentials create -c username -t key-auth -q` y las copiamos.
<br>
Listo, eso es todo, ahora podemos acceder a nuestro microservicios a través de la direccion `localhost:8080/ruta/al/microservicio` y para proporcionar las llaves de autenticación lo hacemos a través de curl con el siguiente comando:
<br>

```bash
curl -H "Authorization: apiKey ${keyId}:${keySecret}" http://localhost:8080/config/app-pay/dev
```
Y reemplazamos ${keyId}:${keySecret} por las llaves de autenticación que copiamos antes.
