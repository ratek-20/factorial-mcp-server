# ---- build ----
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /workspace

COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package


# ---- create custom jre minimizing its size ----
FROM eclipse-temurin:25-jdk AS jre
WORKDIR /app

COPY --from=build /workspace/target/factorial-mcp-server.jar factorial-mcp-server.jar

# print the modules needed by the jar on a file
RUN jdeps \
  --ignore-missing-deps \
  --print-module-deps \
  --multi-release 25 \
  --class-path factorial-mcp-server.jar \
  factorial-mcp-server.jar > modules.txt

# append modules not detected by jdeps (agent only needed for debug, but I add it here anyway since its size is so small that I prefer avoid building a custom debug image)
RUN echo "java.naming,java.desktop,jdk.httpserver,jdk.jdwp.agent" > modules.base
RUN cat modules.base modules.jdeps \
  | tr ',' '\n' \
  | sed '/^\s*$/d' \
  | sort -u \
  | paste -sd, - \
  > modules.txt

# build the actual jre
RUN jlink \
  --add-modules $(cat modules.txt) \
  --strip-debug \
  --no-man-pages \
  --no-header-files \
  --compress=2 \
  --output /opt/jre


# ---- runtime ----
FROM debian:bookworm-slim
LABEL authors="ratek20"
WORKDIR /app

COPY --from=jre /opt/jre /opt/jre
COPY --from=jre /app/factorial-mcp-server.jar /app/factorial-mcp-server.jar

ENV JAVA_HOME=/opt/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

RUN mkdir -p data && chown -R 1000:1000 data
VOLUME ["data"]
USER 1000:1000

EXPOSE 7000

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar factorial-mcp-server.jar"]
