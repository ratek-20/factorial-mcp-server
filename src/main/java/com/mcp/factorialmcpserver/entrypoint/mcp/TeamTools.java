package com.mcp.factorialmcpserver.entrypoint.mcp;

import com.mcp.factorialmcpserver.model.Employee;
import com.mcp.factorialmcpserver.model.Membership;
import com.mcp.factorialmcpserver.model.Team;
import com.mcp.factorialmcpserver.service.api.employees.EmployeesClient;
import com.mcp.factorialmcpserver.service.api.teams.TeamsClient;
import com.mcp.factorialmcpserver.service.exception.TeamNotFoundException;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TeamTools {

    private final TeamsClient teamsClient;
    private final EmployeesClient employeesClient;

    @Autowired
    public TeamTools(TeamsClient teamsClient, EmployeesClient employeesClient) {
        this.teamsClient = teamsClient;
        this.employeesClient = employeesClient;
    }

    @McpTool(name = "get_teams", description = "Returns the list of the teams of the company.")
    public List<Team> getTeams() {
        return teamsClient.getTeams();
    }

    @McpTool(name = "get_team_composition", description = "Returns the list of employees belonging to a team by team name.")
    public List<Employee> getTeamComposition(String name) {
        final Team team = teamsClient.getTeams().stream()
                .filter(team1 -> name.equalsIgnoreCase(team1.name()))
                .findFirst()
                .orElseThrow(() -> new TeamNotFoundException("Team not found with name: " + name));
        final List<Membership> memberships = teamsClient.getMemberships(List.of(team.id()));
        final List<Long> employeeIds = memberships.stream()
                .map(Membership::employeeId)
                .toList();
        return employeesClient.getEmployees().stream()
                .filter(e -> employeeIds.contains(e.id()))
                .toList();
    }


}
