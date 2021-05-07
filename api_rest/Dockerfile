FROM openjdk:8-jdk-alpine
EXPOSE 5000:5000
RUN mkdir /app
COPY ./build/install/sd-exam-2/ /app/
WORKDIR /app/bin
COPY ./start.sh .
CMD ["./start.sh"]