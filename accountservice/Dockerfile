FROM java:11-jre

ADD ./target/account-service.jar /app/

CMD ["java","-Xmx200m","-jar","/app/account-service.jar"]

EXPOSE 6000