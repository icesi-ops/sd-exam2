#!/bin/bash
docker-compose down
chmod +x start.sh
chmod +x db/setup_db.sh
gradle installDist
docker rmi zeronetdev/sd-exam-2
docker build -t zeronetdev/sd-exam-2 .
docker push zeronetdev/sd-exam-2
docker-compose up -d --build