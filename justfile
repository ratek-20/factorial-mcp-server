APP := "factorial-mcp-server"
PORT := "7000"

build: # unset java home to to prevent unwanted env var injection from the IDE, just use the java in the PATH
    env -u JAVA_HOME mvn clean package -DskipTests
    docker build -t {{APP}} .

view-image:
    docker images | grep {{APP}}

run: # detached mode
    docker run -d -p {{PORT}}:7000 --name {{APP}} {{APP}}

debug:
    docker run -d -p {{PORT}}:7000 -p 5005:5005 \
        -e JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" \
        --name {{APP}} {{APP}}

view-container:
    docker ps -a | grep {{APP}}

stop:
    docker stop {{APP}} || true
    docker rm {{APP}} || true

logs:
    docker exec -it {{APP}} sh -c 'tail -n 200 -f /app/logs/app.log'

