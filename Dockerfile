FROM openjdk:14-alpine
COPY build/libs/micronaut-bug-instrumenter-*-all.jar micronaut-bug-instrumenter.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "micronaut-bug-instrumenter.jar"]