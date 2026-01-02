package com.mcp.factorialmcpserver.entrypoint.mcp;

import com.mcp.factorialmcpserver.model.Team;
import com.mcp.factorialmcpserver.service.api.teams.TeamsClient;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TeamTools {

    private final TeamsClient teamsClient;

    @Autowired
    public TeamTools(TeamsClient teamsClient) {
        this.teamsClient = teamsClient;
    }

    @McpTool(name = "get_teams", description = "Returns the list of the teams of the company.")
    public List<Team> getEmployees() {
        return teamsClient.getTeams();
    }
}
