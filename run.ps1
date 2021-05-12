docker-compose down
docker rmi zeronetdev/sd-exam-2
docker build -t zeronetdev/sd-exam-2 .
docker push zeronetdev/sd-exam-2
docker-compose up -d --build