FROM gradle:6.6-jdk14 as builder
WORKDIR /sd-exam2
COPY . .
RUN gradle installDist

FROM openjdk:8-jdk-alpine
EXPOSE 5000:5000
RUN mkdir /app
COPY --from=builder /sd-exam2/build/install/sd-exam-2/ /app/
WORKDIR /app/bin
COPY ./start.sh .
RUN chmod +x ./start.sh
CMD ["./start.sh"]