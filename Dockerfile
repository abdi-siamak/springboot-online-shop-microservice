FROM openjdk:17-jdk-slim
COPY target/springboot-online-shop-*.jar app.jar
#ENTRYPOINT ["java", "-jar", "/app.jar"]

# Add OpenTelemetry agent
COPY opentelemetry-javaagent.jar /otel/opentelemetry-javaagent.jar

# Run app with agent
ENTRYPOINT ["java", "-javaagent:/otel/opentelemetry-javaagent.jar", "-jar", "app.jar"]