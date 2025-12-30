# Factorial Mcp Server

This server is able to connect AI Agents to the popular HRMS Factorial.

## Tech stack
- Java 25
- Maven
- Spring Boot / Spring AI

## Build
```bash
./mvnw clean package
```

## Configure
Define the following option in 
```json
{
  "mcpServers": {
    "mcp-factorial": {
      "command": "<path-to-java-25-executable>",
      "args": ["-jar", "<path-to-generated-jar-file>"],
      "timeout": 30000,
      "trust": false,
      "cwd": "<path-to-desired-execution-folder>",
      "includeTools": [
        "get_employees"
      ]
    }
  } 
}
```
Use "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=localhost:5005" as the 1st arg to enable remote debugging on port 5005

This configuration works in Gemini CLI, for other clients it may slightly vary
