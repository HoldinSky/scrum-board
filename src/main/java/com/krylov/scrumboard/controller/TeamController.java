package com.krylov.scrumboard.controller;

import com.krylov.scrumboard.entity.AppUser;
import com.krylov.scrumboard.entity.Team;
import com.krylov.scrumboard.request.CreateTeamRequest;
import com.krylov.scrumboard.request.MemberTeamRequest;
import com.krylov.scrumboard.request.ProjectTeamRequest;
import com.krylov.scrumboard.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@RestController
@RequestMapping("/api/team")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @GetMapping
    public ResponseEntity<?> showMyTeam() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();
        Team team = teamService.getTeamOfMember(username);

        if (team == null) {
            return ResponseEntity
                    .status(BAD_REQUEST.value())
                    .header("error", "You are not part of any teams!")
                    .body("Please join a team before You can access this page");
        }

        return ResponseEntity.ok(team);
    }

    @GetMapping("/find")
    public ResponseEntity<?> findTeam(@RequestBody String name) {
        return ResponseEntity.ok(teamService.getTeams(name));
    }

    @PostMapping
    public ResponseEntity<Team> saveTeam(@RequestParam(name = "name") String teamName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();

        var request = new CreateTeamRequest(teamName, username);
        Team team = teamService.saveTeam(request);
        return ResponseEntity.ok(team);
    }

    @DeleteMapping
    public ResponseEntity<Team> deleteTeam(@RequestParam(name = "id") Long teamId) {
        Team team = teamService.deleteTeam(teamId);
        return ResponseEntity.ok(team);
    }

    @PutMapping("/member")
    public ResponseEntity<Team> addMember(@RequestBody MemberTeamRequest request) {
        return ResponseEntity.ok(teamService.addMember(request));
    }

    @DeleteMapping("/member")
    public ResponseEntity<Team> removeMember(@RequestBody MemberTeamRequest request) {
        return ResponseEntity.ok(teamService.removeMember(request));
    }

    @PutMapping("/manager")
    public ResponseEntity<AppUser> addManager(@RequestParam(name = "name") String username) {
        return ResponseEntity.ok(teamService.addTeamManager(username));
    }

    @PutMapping("/project")
    public ResponseEntity<Team> addProject(@RequestBody ProjectTeamRequest request) {
        return ResponseEntity.ok(teamService.createProject(request));
    }

    @DeleteMapping("/project")
    public ResponseEntity<Team> deleteProject(@RequestBody ProjectTeamRequest request) {
        return ResponseEntity.ok(teamService.deleteProject(request));
    }



}
