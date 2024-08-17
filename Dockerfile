FROM openjdk:17-jdk-slim

LABEL author="tamld-project"
RUN apt-get update && apt-get install -y --no-install-recommends net-tools && apt-get clean && rm -rf /var/lib/apt/lists/*
COPY ./src/main/resources/entrypoint.sh ./entrypoint.sh

#cho phep run scrip entrypoint.sh
RUN chmod +x ./entrypoint.sh

COPY ./target/*.jar /app/service.jar

#create log folder
#CMD [mkdir -p /var/log/kpro]

#CMD ["java", "-Xmx512m", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/service.jar"]
ENTRYPOINT ["./entrypoint.sh"]

EXPOSE 8080
