#!/bin/bash
sudo docker build -t icesiops/appconfig:0.1.0 app-config/.
sudo docker build -t icesiops/appinvoice:0.1.0 app-invoice/.
sudo docker build -t icesiops/apppay:0.1.0 app-pay/.
sudo docker build -t icesiops/apptransaction:0.1.0 app-transaction/.
sudo docker build -t icesiops/loadbalancer:0.1.0 haproxy/.

exit