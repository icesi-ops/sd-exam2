<<<<<<< HEAD
# sd-exam2
Here, enjoy !
=======
# Payments Microservices

This repository is a docker practice for my Distribuited Systems course and it's based on this project: https://github.com/icesi-ops/training_microservices/tree/master/pay-app-spring-microservices

## Changes

    1. The application properties of each application on the configuration server now include information about the consul port and server.

    2. The application now includes the actuator dependency to facilitate health checks management.

    3. The loadbalancer has been configured to redirect incoming traffic to the appropriate microservice using consul as the DNS.

    4. The gateway configuration now includes the respective API and service endpoints, as well as traffic policies. 

## Evidence

[Demo](https://clipchamp.com/watch/RuTAekPzfXZ)

## Review the configuration files
- [Consul configuration on config server](config)
- [Loadbalancer configuration](haproxy/haproxy.cfg)
- [API Gateway configuration](appgw/gateway.config.yml)
>>>>>>> master
