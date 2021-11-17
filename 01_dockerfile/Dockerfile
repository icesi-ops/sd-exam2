
#Python
FROM alpine:latest
ENV DEBIAN_FRONTEND=noninteractive

RUN apk add --update \
    python3 \
    python3-dev \
    py3-pip \
    build-base \
    && rm -rf /var/cache/apk/*

COPY . /app
RUN pip3 freeze > requirements.txt

EXPOSE 8080

CMD ["/usr/bin/python", "/app/sources/microservice_a.py"]

#Postgresql

FROM ubuntu:16.04

#install package
RUN apt-get update -y
RUN apt-get install postgresql -y

#Configuracion del puerto postgres
EXPOSE 5432
CMD postgresql -m http.server 5432

#Nginx

#FROM nginx:latest
#RUN apt update -y
#RUN apt install -y nginx
#RUN apt install -y wget 
#RUN echo "ServerName 127.0.0.1" >> /etc/nginx/nginx.conf
#WORKDIR /var/www/html/



#CMD ["/usr/sbin/nginxctl", "-D", "FOREGROUND"]

