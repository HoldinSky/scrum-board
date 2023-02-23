package com.krylov.scrumboard.service;


import com.krylov.scrumboard.entity.AppUser;
import com.krylov.scrumboard.entity.Team;
import com.krylov.scrumboard.request.CreateTeamRequest;
import com.krylov.scrumboard.request.MemberTeamRequest;
import com.krylov.scrumboard.request.ProjectTeamRequest;

import java.util.List;

public interface TeamService {
    Team getTeamOfMember(String username);
    List<Team> getTeams(String name);
    Team saveTeam(CreateTeamRequest request);
    Team deleteTeam(Long id);
    Team createProject(ProjectTeamRequest request);
    Team deleteProject(ProjectTeamRequest request);
    Team addMember(MemberTeamRequest request);
    Team removeMember(MemberTeamRequest request);
    AppUser addTeamManager(String username);
}
