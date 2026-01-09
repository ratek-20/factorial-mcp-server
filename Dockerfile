FROM eclipse-temurin:25-jre

LABEL authors="ratek20"

WORKDIR /app

# JVM tuning base per container
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

COPY target/factorial-mcp-server.jar factorial-mcp-server.jar

EXPOSE 7000

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar factorial-mcp-server.jar"]
