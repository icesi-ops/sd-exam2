FROM ubuntu:18.04 as builder
WORKDIR /certs

RUN apt-get update && \
    apt-get install -y openssl && \
    openssl genrsa -des3 -passout pass:asdf -out server.pass.key 2048 && \
    openssl rsa -passin pass:asdf -in server.pass.key -out server.key && \
    rm server.pass.key && \
    openssl req -new -key server.key -out server.csr \
        -subj "/C=UK/ST=Warwickshire/L=Leamington/O=OrgName/OU=IT Department/CN=example.com" && \
    openssl x509 -req -days 365 -in server.csr -signkey server.key -out server.crt

FROM traefik:v2.0-alpine

CMD ["--api.insecure=true","--entrypoints.web.address=:80","--entrypoints.websecure.address=:443","--providers.docker","--log.level=DEBUG","--providers.file.directory=/etc/traefik/dynamic_conf"]
COPY --from=builder . /tools/certs
COPY conf.yml /etc/traefik/dynamic_conf/conf.yml