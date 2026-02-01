# 🏢 Factorial MCP Server

> **Delegate your time-off management to LLMs.** An MCP server that connects AI agents to Factorial, the popular HR management system, enabling employees to handle vacation requests, approvals, and time-off operations through natural language.

[![Docker](https://img.shields.io/badge/docker-ghcr.io-blue)](https://ghcr.io/ratek-20/factorial-mcp-server)
[![Java](https://img.shields.io/badge/java-25-orange)](https://openjdk.org/)
[![Spring AI](https://img.shields.io/badge/spring--ai-1.1.2-green)](https://spring.io/projects/spring-ai)
[![MCP](https://img.shields.io/badge/MCP-compatible-purple)](https://modelcontextprotocol.io/)

---

## 📖 What is This?

This is an [MCP (Model Context Protocol)](https://modelcontextprotocol.io/docs/getting-started/intro) server for [Factorial](https://factorialhr.com/), the popular HR management system.

It runs on your local machine and exposes tools that AI agents like [Claude Code](https://claude.com/product/claude-code), [Gemini CLI](https://geminicli.com/), or [GitHub Copilot CLI](https://github.com/features/copilot/cli) can use to help you manage time-off operations via natural language.

**How it works:**
- The server communicates with AI clients using the **STDIO protocol**
- Authorization is handled securely via **OAuth2**
- You talk to your AI agent, and it handles the interactions with the Factorial system

---

## 🎬 Demo

<!-- TODO: Add demo screenshots/videos here -->

_Coming soon: Screenshots and videos demonstrating the server in action._

---

## 🛠️ Available Tools

The server exposes the following tools to AI agents:

### 🔐 Authorization
- **`authorize`** - Initialize OAuth2 authorization flow with Factorial

### 👤 Employee Information
- **`get_current_employee`** - Get your employee ID and full name
- **`get_employee`** - Look up any employee by their full name

### 🏖️ Time Off Management
- **`get_available_vacation_days`** - Check remaining vacation days for an employee
- **`get_leave_types`** - List all available leave types (vacation, sick leave, etc.)
- **`request_time_off`** - Submit a new time-off request
  - Supports custom date ranges (YYYY-MM-DD format)
  - Optional leave type (defaults to vacation)
  - Optional half-day specification
- **`read_time_offs`** - View all time-off requests for an employee
- **`approve_time_off`** - Approve a pending time-off request (requires manager permissions)
- **`update_time_off`** - Modify dates or type of an existing request
- **`delete_time_off`** - Cancel a time-off request

---

## 🚀 Setup

### 🏢 What Your Company Admin Needs to Do

1. **Create and configure an OAuth Application in Factorial:**
   - As described in the [official API documentation](https://apidoc.factorialhr.com/docs/create-a-new-oauth-application), navigate to the [OAuth Applications page](https://api.factorialhr.com/oauth/applications) and configure:
   - **Name:** Something descriptive like `"OAuth app for MCP server authorization"`
   - **Redirect URI:** `http://127.0.0.1:7000/oauth2-callback`
   - **Confidential:** ✅ Checked (API requests are server-side, which is secure)
   - **Scopes/Roles:** Select the following:
     - `Api Public`
     - `TimeOff`
     - `Employees`

2. **Share credentials with employees:**
   - Provide the **OAuth Application ID** and **Secret** to employees who want to use the server

### 👨‍💻 What You (The Employee) Need to Do

#### Prerequisites
- An AI agent client installed:
  - [Claude Code](https://claude.com/product/claude-code)
  - [Gemini CLI](https://geminicli.com/)
  - [GitHub Copilot](https://github.com/features/copilot)
- [Docker](https://docs.docker.com/get-docker/) installed

#### Installation Steps

1. **Create a `.env` file** with your OAuth credentials:
   ```bash
   OAUTH2_APPLICATION_ID=<your-actual-id>
   OAUTH2_APPLICATION_SECRET=<your-actual-secret>
   ```

2. **Configure the MCP server in your AI client:**

   <details>
   <summary><strong>Gemini CLI</strong> (<code>~/.gemini/settings.json</code>)</summary>

   ```json
   {
     "mcpServers": {
       "mcp-factorial": {
         "command": "docker",
         "args": [
           "run",
           "-i",
           "--rm",
           "-p",
           "7000:7000",
           "-v",
           "factorial-mcp-server_cache:/app/data",
           "--env-file",
           "<absolute-path-to-your-.env-file>",
           "ghcr.io/ratek-20/factorial-mcp-server:latest",
           "--transport",
           "stdio"
         ],
         "timeout": 120000,
         "trust": false,
         "includeTools": [
           "get_available_vacation_days",
           "request_time_off",
           "read_time_offs",
           "approve_time_off",
           "update_time_off",
           "delete_time_off",
           "get_leave_types",
           "get_current_employee",
           "get_employee",
           "authorize"
         ]
       }
     }
   }
   ```
   </details>

   <details>
   <summary><strong>GitHub Copilot</strong> (<code>~/.copilot/mcp-config.json</code>)</summary>

   ```json
   {
     "mcpServers": {
       "mcp-factorial": {
         "tools": [
           "get_available_vacation_days",
           "request_time_off",
           "read_time_offs",
           "approve_time_off",
           "update_time_off",
           "delete_time_off",
           "get_leave_types",
           "get_current_employee",
           "get_employee",
           "authorize"
         ],
         "command": "docker",
         "args": [
           "run",
           "-i",
           "--rm",
           "-p",
           "7000:7000",
           "-v",
           "factorial-mcp-server_cache:/app/data",
           "--env-file",
           "<absolute-path-to-your-.env-file>",
           "ghcr.io/ratek-20/factorial-mcp-server:latest",
           "--transport",
           "stdio"
         ]
       }
     }
   }
   ```
   </details>

   <details>
   <summary><strong>Claude Desktop/Code</strong></summary>

   **Config path:**
   - macOS: `~/Library/Application Support/Claude/claude_desktop_config.json`
   - Windows: `%APPDATA%\Claude\claude_desktop_config.json`
   - Linux: `~/.config/Claude/claude_desktop_config.json`

   ```json
   {
     "mcpServers": {
       "mcp-factorial": {
         "command": "docker",
         "args": [
           "run",
           "-i",
           "--rm",
           "-p",
           "7000:7000",
           "-v",
           "factorial-mcp-server_cache:/app/data",
           "--env-file",
           "<absolute-path-to-your-.env-file>",
           "ghcr.io/ratek-20/factorial-mcp-server:latest",
           "--transport",
           "stdio"
         ],
         "timeout": 120000,
         "trust": false,
         "includeTools": [
           "get_available_vacation_days",
           "request_time_off",
           "read_time_offs",
           "approve_time_off",
           "update_time_off",
           "delete_time_off",
           "get_leave_types",
           "get_current_employee",
           "get_employee",
           "authorize"
         ]
       }
     }
   }
   ```
   </details>

   > ⚠️ **Important:** The syntax and structure may vary between AI clients. Consult your client's documentation for the exact configuration format.

3. **Launch your AI client** - The MCP server will automatically connect when the client starts.

---

## 🔐 Authorization Flow

The first time your AI client attempts to use any of the server's tools, the OAuth2 authorization flow will begin:

1. A browser window will open automatically (or you'll receive a URL to open manually)
2. Log in with your Factorial employee credentials
3. Approve the authorization request
4. You're all set!

**After successful authorization:**
- Your access token is stored in a Docker named volume (`factorial-mcp-server_cache`)
- Even if you stop/restart the server, it will reload the token from persistent storage
- You won't need to re-authorize unless the token expires or is revoked

---

## 🏗️ Architecture

### 🔧 Tool Invocation Flow

<!-- TODO: Add architecture diagram here -->

_Diagram placeholder: Tool invocation flow_

When an AI agent invokes a tool, the server:
1. Receives the request via STDIO
2. Makes one or more HTTP requests to the [Factorial Public API](https://apidoc.factorialhr.com/)
3. **Caches GET responses** in memory to reduce API calls and improve performance
4. Returns the formatted result back to the AI client

**Caching Strategy:**
- Only `GET` requests are cached (read operations)
- Write operations (`POST`, `PUT`, `DELETE`) always hit the API
- Cache is stored in-memory for the lifetime of the server process

### 🔑 Authorization Architecture

<!-- TODO: Add OAuth flow diagram here -->

_Diagram placeholder: OAuth2 flow_

The server exposes **port 7000** with a minimal HTTP server that handles OAuth callbacks:

1. When authorization is needed, the server generates an OAuth URL
2. The user opens this URL in a browser (automatically or manually)
3. After successful login, Factorial redirects to `http://127.0.0.1:7000/oauth2-callback?code=...`
4. The server's HTTP endpoint receives the authorization code
5. The code is exchanged for an access token via Factorial's OAuth API
6. The access token is stored in a **Docker named volume** for persistence

**Token Persistence:**
- Tokens are saved to `/app/data` inside the container
- This directory is mounted to a Docker named volume
- On server restart, the token is loaded from the volume into memory
- No need to re-authorize unless the token is invalid/expired

---

## 💻 Technology & Development

### Tech Stack

**Core:**
- **Java 25** - Latest LTS Java version
- **Spring Boot 3.5.9** - Application framework
- **Spring AI 1.1.2** - MCP server implementation
- **Spring Web** - HTTP server for OAuth callbacks

**Testing:**
- **JUnit Jupiter 5** - Test framework
- **Mockito 5.12.0** - Mocking framework
- **Instancio 5.5.1** - Test data generation
- **WireMock 3.13.2** - HTTP mocking for integration tests
- **Spring Boot Test** - Integration testing support
- **JaCoCo 0.8.14** - Code coverage reporting

**CI/CD:**
- **GitHub Actions** - Automated testing and Docker image publishing
- Docker images are automatically published to `ghcr.io/ratek-20/factorial-mcp-server`

### 🧑‍💻 Contributing

Contributions are welcome! Whether you want to fix bugs, add features, or improve documentation, feel free to open an issue or submit a pull request.

#### Local Development

This project uses [just](https://github.com/casey/just) as a command runner. Install it, then use these commands:

```bash
# Build the Docker image
just build

# Run tests
just test

# Run the server locally (requires OAuth credentials in command)
just run

# Run with remote debugging enabled on port 5005
just debug

# Inspect the server with the MCP Inspector
just inspect

# View logs
just logs

# Clean up
just kill-container
just clear-cache
```

See the [`justfile`](justfile) for all available commands.

#### Development Workflow

1. **Make your changes** in a feature branch
2. **Write tests** for new functionality
3. **Run tests** with `just test`
4. **Build locally** with `just build`
5. **Test manually** with `just debug` or `just inspect`
6. **Submit a pull request**

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🤝 Support

- **Issues:** [GitHub Issues](https://github.com/ratek-20/factorial-mcp-server/issues)
- **Discussions:** [GitHub Discussions](https://github.com/ratek-20/factorial-mcp-server/discussions)

---

## 🙏 Acknowledgments

- [Model Context Protocol](https://modelcontextprotocol.io/) - For the MCP specification
- [Factorial](https://factorialhr.com/) - For their public API
- [Spring AI](https://spring.io/projects/spring-ai) - For MCP server implementation
