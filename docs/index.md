---
title: Factorial MCP Server - AI-Powered Time Off Management for Claude, Gemini & Copilot
description: Open-source Model Context Protocol (MCP) server that connects AI agents like Claude Code, Gemini CLI, and GitHub Copilot to Factorial HR for natural language vacation and time-off management.
keywords: factorial mcp server, factorial hr api, model context protocol, claude ai tools, gemini cli mcp, github copilot mcp, time off management ai, vacation management automation, hr ai assistant, oauth2 factorial
---

# Factorial MCP Server

> **Manage your time off with AI.** Connect Claude, Gemini, or GitHub Copilot to Factorial HR and handle vacation requests, approvals, and leave management through natural language.

## What Is This?

An open-source [Model Context Protocol (MCP)](https://modelcontextprotocol.io/) server for [Factorial HR](https://factorialhr.com/) that enables AI agents to manage time-off operations. Instead of clicking through HR dashboards, ask your AI assistant: *"Request 3 vacation days starting next Monday"* or *"How many vacation days do I have left?"*

**Compatible with:** Claude Code, Claude Desktop, Gemini CLI, GitHub Copilot, and any MCP-compliant AI client.

---

## Key Features

- 🤖 **Natural Language Interface** - Manage vacation through conversation
- 🔐 **Secure OAuth2 Authentication** - Industry-standard authorization
- 🏖️ **Complete Time-Off Operations** - Request, approve, update, and cancel leave
- 📊 **Employee Queries** - Check balances, employee info, and leave types
- 🐳 **One-Command Setup** - Docker-based with persistent tokens
- ⚡ **Smart Caching** - Optimized API calls for better performance

---

## What You Can Do

### For Employees
- Check remaining vacation days
- Submit time-off requests with custom dates
- View and modify existing requests
- Query available leave types

### For Managers
- Approve team member requests
- Review pending time-off requests
- Check team vacation balances

All through natural conversation with your AI assistant.

---

## Quick Start

### Prerequisites
- [Docker](https://docs.docker.com/get-docker/) installed
- AI client: [Claude Code](https://claude.com/product/claude-code), [Gemini CLI](https://geminicli.com/), or [GitHub Copilot](https://github.com/features/copilot)
- OAuth credentials from your company admin

### Setup (3 Steps)

**1. Get OAuth credentials** from your company administrator (see [README](../README.md#-what-your-company-admin-needs-to-do))

**2. Create `.env` file:**
```bash
OAUTH2_APPLICATION_ID=your-app-id
OAUTH2_APPLICATION_SECRET=your-app-secret
```

**3. Add to your AI client:**

**Claude Code/Desktop:**
```bash
claude mcp add --transport stdio mcp-factorial -- \
  docker run -i --rm -p 7000:7000 \
  -v factorial-mcp-server_cache:/app/data \
  --env-file /path/to/.env \
  ghcr.io/ratek-20/factorial-mcp-server:latest \
  --transport stdio
```

**Gemini CLI / GitHub Copilot:** See [full setup instructions](../README.md#installation-steps)

---

## How It Works

```
AI Client → STDIO → MCP Server (Docker) → OAuth2 → Factorial API
```

1. Your AI assistant sends tool requests via STDIO
2. The server authenticates with Factorial using OAuth2
3. API requests are made and responses cached intelligently
4. Results return to your AI for natural language responses

**Technology:** Java 25, Spring Boot 3.5.9, Spring AI 1.1.2, Docker

---

## Available Tools

| Tool | Description |
|------|-------------|
| `authorize` | OAuth2 authorization flow |
| `get_current_employee` | Your employee info |
| `get_employee` | Look up any employee |
| `get_available_vacation_days` | Check vacation balance |
| `get_leave_types` | List leave categories |
| `request_time_off` | Submit new requests |
| `read_time_offs` | View all requests |
| `approve_time_off` | Approve requests (managers) |
| `update_time_off` | Modify existing requests |
| `delete_time_off` | Cancel requests |

---

## Security

- ✅ OAuth2 authentication with Factorial
- ✅ Runs locally on your machine
- ✅ Encrypted HTTPS communication
- ✅ Secure token storage in Docker volumes
- ✅ Open source and auditable

---

## Example Usage

Once configured, use natural language with your AI:

- *"How many vacation days do I have left?"*
- *"Request 5 days off from December 20-24"*
- *"Show my pending time-off requests"*
- *"Approve Sarah's vacation request"*
- *"Cancel my time off request from next week"*

On first use, authorize via browser with your Factorial credentials (one-time setup).

---

## Resources

- **Full Documentation:** [README](../README.md)
- **GitHub:** [ratek-20/factorial-mcp-server](https://github.com/ratek-20/factorial-mcp-server)
- **Docker Image:** [ghcr.io/ratek-20/factorial-mcp-server](https://ghcr.io/ratek-20/factorial-mcp-server)
- **Issues & Support:** [GitHub Issues](https://github.com/ratek-20/factorial-mcp-server/issues)
- **Model Context Protocol:** [modelcontextprotocol.io](https://modelcontextprotocol.io/)
- **Factorial API:** [apidoc.factorialhr.com](https://apidoc.factorialhr.com/)

---

## Contributing

Contributions welcome! See the [development guide](../README.md#-technology--development) for setup instructions. Built with Java, Spring Boot, and Spring AI.

---

## License

MIT License - See [LICENSE](../LICENSE) file.

---

*Built for the MCP community. [Star on GitHub](https://github.com/ratek-20/factorial-mcp-server) ⭐*
