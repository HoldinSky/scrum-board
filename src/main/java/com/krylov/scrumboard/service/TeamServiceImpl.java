package com.krylov.scrumboard.service;

import com.krylov.scrumboard.entity.AppUser;
import com.krylov.scrumboard.entity.Project;
import com.krylov.scrumboard.entity.Role;
import com.krylov.scrumboard.entity.Team;
import com.krylov.scrumboard.repository.TeamRepository;
import com.krylov.scrumboard.request.CreateTeamRequest;
import com.krylov.scrumboard.request.MemberTeamRequest;
import com.krylov.scrumboard.request.ProjectTeamRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepo;
    private final UserService userService;
    private final ProjectService projectService;
    @Override
    public Team saveTeam(CreateTeamRequest request) {
        Team team = new Team(request.getTeamName());
        try {
            AppUser user = userService.getUser(request.getUsername());
            Role role= userService.getRole("ROLE_TEAM_MANAGER");
            user.getRoles().add(role);

            team.setCreator(user);
            team.getMembers().add(user);
            teamRepo.save(team);
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
        }

        return team;
    }

    @Override
    public Team deleteTeam(Long id) {
        Team team = teamRepo.findById(id).orElse(null);
        assert team != null;
        teamRepo.delete(team);
        return team;
    }

    @Override
    public Team addProject(ProjectTeamRequest request) {
        try {
            Project project = projectService.getProjectById(request.getProjectId());
            Team team = teamRepo.findById(request.getTeamId()).orElseThrow(() ->
                    new RuntimeException("Team is not found in database with id: " + request.getTeamId()));
            team.getProjectList().add(project);
            return team;
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            return null;
        }
    }

    @Override
    public Team addMember(MemberTeamRequest request) {
        try {
            AppUser user = userService.getUser(request.getUsername());
            Team team = teamRepo.findById(request.getTeamId()).orElseThrow(() ->
                    new RuntimeException("Team is not found in database with id: " + request.getTeamId()));
            team.getMembers().add(user);
            return team;
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            return null;
        }
    }

    @Override
    public Team removeMember(MemberTeamRequest request) {
        try {
            AppUser user = userService.getUser(request.getUsername());
            Team team = teamRepo.findById(request.getTeamId()).orElseThrow(() ->
                    new RuntimeException("Team is not found in database with id: " + request.getTeamId()));
            team.getMembers().remove(user);
            return team;
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            return null;
        }
    }

    @Override
    public Team removeProject(ProjectTeamRequest request) {
        try {
            Project project = projectService.getProjectById(request.getProjectId());
            Team team = teamRepo.findById(request.getTeamId()).orElseThrow(() ->
                    new RuntimeException("Team is not found in database with id: " + request.getTeamId()));
            team.getProjectList().remove(project);
            return team;
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            return null;
        }
    }


}
