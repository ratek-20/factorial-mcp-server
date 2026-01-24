APP := "factorial-mcp-server"
PORT := "7000"

build: # unset java home to to prevent unwanted env var injection from the IDE, just use the java in the PATH
    env -u JAVA_HOME mvn clean package -DskipTests
    docker build -t {{APP}} .

test:
    env -u JAVA_HOME mvn clean test

view-image:
    docker images | grep {{APP}}

run: # detached mode
    docker run -d \
        -p {{PORT}}:7000 \
        -e OAUTH2_APPLICATION_ID=oauth-app-id \
        -e OAUTH2_APPLICATION_SECRET=oauth-app-secret \
        -v factorial-mcp-server_cache:/app/data \
        --name {{APP}} {{APP}}
# replace oauth env vars accoring to your actual app data

debug:
    docker run -d \
        -p {{PORT}}:7000 -p 5005:5005 \
        -e OAUTH2_APPLICATION_ID=oauth-app-id \
        -e OAUTH2_APPLICATION_SECRET=oauth-app-secret \
        -e JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" \
        -v factorial-mcp-server_cache:/app/data \
        --name {{APP}} {{APP}}
# replace oauth env vars accoring to your actual app data

view-container:
    docker ps -a | grep {{APP}}

kill-container:
    docker stop {{APP}} || true
    docker rm {{APP}} || true

logs:
    docker exec -it {{APP}} sh -c 'tail -n 200 -f /app/data/app.log'

clear-cache:
    docker volume rm factorial-mcp-server_cache