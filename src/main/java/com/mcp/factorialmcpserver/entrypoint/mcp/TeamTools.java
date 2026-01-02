package com.mcp.factorialmcpserver.entrypoint.mcp;

import com.mcp.factorialmcpserver.model.Team;
import com.mcp.factorialmcpserver.service.api.teams.TeamsClient;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
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
    public List<Team> getTeams() {
        return teamsClient.getTeams();
    }

    @McpTool(name = "get_team", description = "Returns a single team by its ID.")
    public Team getTeam(Long id) {
        return teamsClient.getTeam(id);
    }

    @McpTool(name = "create_team", description = "Creates a team by name and description.")
    public Team createTeam(String name, @McpToolParam(required = false) String description) {
        return teamsClient.createTeam(name, description);
    }
}
