APP := "factorial-mcp-server"
PORT := "7000"

build:
    docker build -t {{APP}} .

test:
    env -u JAVA_HOME mvn clean test

view-image:
    docker images | grep {{APP}}

run:
    docker run -i --rm \
        -p {{PORT}}:7000 \
        -e OAUTH2_APPLICATION_ID=oauth-app-id \
        -e OAUTH2_APPLICATION_SECRET=oauth-app-secret \
        -v factorial-mcp-server_cache:/app/data \
        --name {{APP}} {{APP}}
# replace oauth env vars according to your actual app data

debug:
    docker run -i --rm \
        -p {{PORT}}:7000 -p 5005:5005 \
        -e OAUTH2_APPLICATION_ID=oauth-app-id \
        -e OAUTH2_APPLICATION_SECRET=oauth-app-secret \
        -e JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" \
        -v factorial-mcp-server_cache:/app/data \
        --name {{APP}} {{APP}}
# replace oauth env vars accoring to your actual app data

inspect:
    npx @modelcontextprotocol/inspector -- \
          docker run --rm -i \
              -p 7000:7000 -p 5005:5005 \
              -v factorial-mcp-server_cache:/app/data \
              -e OAUTH2_APPLICATION_ID=oauth-app-id \
              -e OAUTH2_APPLICATION_SECRET=oauth-app-secret \
              -e JAVA_TOOL_OPTIONS=\"-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005\" \
              factorial-mcp-server:latest \
              --name factorial-mcp-server

view-container:
    docker ps -a | grep {{APP}}

kill-container:
    docker stop {{APP}} || true
    docker rm {{APP}} || true

logs:
    docker exec -it {{APP}} sh -c 'tail -n 200 -f /app/data/app.log'

clear-cache:
    docker volume rm factorial-mcp-server_cache