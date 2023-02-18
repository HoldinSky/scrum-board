package com.krylov.scrumboard.service;


import com.krylov.scrumboard.entity.Project;
import com.krylov.scrumboard.entity.Team;
import com.krylov.scrumboard.request.CreateTeamRequest;
import com.krylov.scrumboard.request.MemberTeamRequest;
import com.krylov.scrumboard.request.ProjectTeamRequest;

public interface TeamService {
    Team saveTeam(CreateTeamRequest request);
    Team deleteTeam(Long id);
    Team addProject(ProjectTeamRequest request);
    Team addMember(MemberTeamRequest request);
    Team removeMember(MemberTeamRequest request);
    Team removeProject(ProjectTeamRequest request);
}
