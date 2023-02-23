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

import java.util.List;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepo;
    private final UserService userService;
    private final ProjectService projectService;

    private final static String TEAM_MEMBER = "ROLE_TEAM_MEMBER";
    private final static String TEAM_MANAGER = "ROLE_TEAM_MANAGER";


    @Override
    public Team getTeamOfMember(String username) {
        AppUser user = userService.getUser(username);

        return user.getTeam();
    }

    @Override
    public List<Team> getTeams(String name) {
        return teamRepo.findByName(name);
    }

    @Override
    public Team saveTeam(CreateTeamRequest request) {
        try {
            Team team = new Team(request.getTeamName());
            AppUser user = userService.getUser(request.getUsername());
            Role role = userService.getRole(TEAM_MANAGER);
            user.getRoles().add(role);
            user.setTeam(team);

            team.setCreator(user);
            team.getMembers().add(user);
            teamRepo.save(team);

            return team;
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            return null;
        }

    }

    @Override
    public Team deleteTeam(Long id) {
        Team team = teamRepo.findById(id).orElse(null);
        assert team != null;
        team.getMembers().forEach(user -> {
            user.setTeam(null);
            user.getRoles()
                    .removeIf(role -> role.getName().equals(TEAM_MEMBER) || role.getName().equals(TEAM_MANAGER));
        });
        teamRepo.delete(team);
        return team;
    }

    @Override
    public Team createProject(ProjectTeamRequest request) {
        try {
            Project project = projectService.createProject(request.getProjectName());
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
    public Team deleteProject(ProjectTeamRequest request) {
        try {
            Project project = projectService.getProjectById(request.getProjectId());
            Team team = teamRepo.findById(request.getTeamId()).orElseThrow(() ->
                    new RuntimeException("Team is not found in database with id: " + request.getTeamId()));

            team.getProjectList().remove(project);
            projectService.deleteProject(request.getProjectId());
            return team;
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            return null;
        }
    }

    @Override
    public Team addMember(MemberTeamRequest request) {
        try {
            Team team = teamRepo.findById(request.getTeamId()).orElseThrow(() ->
                    new RuntimeException("Team is not found in database with id: " + request.getTeamId()));
            AppUser user = userService.getUser(request.getUsername());
            Role role = userService.getRole(TEAM_MEMBER);

            user.getRoles().add(role);
            user.setTeam(team);
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
            Team team = teamRepo.findById(request.getTeamId()).orElseThrow(() ->
                    new RuntimeException("Team is not found in database with id: " + request.getTeamId()));
            AppUser user = userService.getUser(request.getUsername());

            user.getRoles().removeIf(role -> role.getName().equals(TEAM_MEMBER));
            user.setTeam(null);
            team.getMembers().remove(user);
            return team;
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            return null;
        }
    }

    @Override
    public AppUser addTeamManager(String username) {
        try {
            AppUser user = userService.getUser(username);
            Role role = userService.getRole(TEAM_MEMBER);
            if (!user.getRoles().contains(role)) throw new RuntimeException("User is not a member of any team");

            role = userService.getRole(TEAM_MANAGER);
            user.getRoles().add(role);
            return user;
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            return null;
        }
    }
}
