package SecurityAPI2.Controller;

import SecurityAPI2.Dto.ProjectEmployeeDto;
import SecurityAPI2.Dto.ProjectEmployeeRequest;

import SecurityAPI2.Dto.UpdateEngineerProjectDto;
import SecurityAPI2.Mapper.ProjectEmployeeMapper;
import SecurityAPI2.Model.Project;
import SecurityAPI2.Model.ProjectEmployee;
import SecurityAPI2.Model.User;
import SecurityAPI2.Security.JwtUtils;

import SecurityAPI2.Dto.UserDto;
import SecurityAPI2.Mapper.ProjectEmployeeMapper;
import SecurityAPI2.Mapper.UserMapper;
import SecurityAPI2.Model.ProjectEmployee;
import SecurityAPI2.Model.User;

import SecurityAPI2.Service.AuthService;
import SecurityAPI2.Service.ProjectEmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("/project-employee")
@RequiredArgsConstructor
public class ProjectEmployeeController {
    private final ProjectEmployeeService projectEmployeeService;
    private final ProjectEmployeeMapper projectEmployeeMapper;
    private final UserMapper userMapper;
    private final AuthService authService;
    @PostMapping("")
    @PreAuthorize("isAuthenticated() and hasAuthority('ADMIN') or hasAuthority('PROJECTMANAGER')")
    public ResponseEntity<Long> addProjectEmployee(@Valid @RequestBody ProjectEmployeeRequest req) {
        ProjectEmployee projectEmployee = projectEmployeeService.addProjectEmployee(req);
        return ResponseEntity.ok(projectEmployee.getId());
    }
    @GetMapping("/{projectId}/engineers")
    @PreAuthorize("isAuthenticated() and hasAuthority('ADMIN') or hasAuthority('PROJECTMANAGER')")
    public ResponseEntity<List<ProjectEmployeeDto>> findAllEngineersOnProject(@Valid @PathVariable Long projectId) {
        List<ProjectEmployee> engineers = projectEmployeeService.findAllEngineersOnProject(projectId);
        return ResponseEntity.ok(projectEmployeeMapper.projectEmployeesToProjectEmployeeDtos(engineers));
    }

    @GetMapping("/projects")
    @PreAuthorize("isAuthenticated() and hasAuthority('ENGINEER')")
    public ResponseEntity<List<ProjectEmployeeDto>> findAllEngineerProjects(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader){
        final User user = authService.getUserFromToken(authHeader);
        List<ProjectEmployee> projects = projectEmployeeService.findAllEngineerProjects(user.getId());
        return ResponseEntity.ok(projectEmployeeMapper.projectEmployeesToProjectEmployeeDtos(projects));
    }

    @PatchMapping("/description/update")
    @PreAuthorize("isAuthenticated() and hasAuthority('ENGINEER')")
    public ResponseEntity<ProjectEmployeeDto> updateJobDescription(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader, @Valid @RequestBody UpdateEngineerProjectDto updateEngineerProjectDto) {
        final User user = authService.getUserFromToken(authHeader);
        ProjectEmployee updatedProjectEmployee = projectEmployeeService.updateJobDescription(user, updateEngineerProjectDto.getProjectId(), updateEngineerProjectDto.getDescription());
        return ResponseEntity.ok(projectEmployeeMapper.projectEmployeeToProjectEmployeeDto(updatedProjectEmployee));
    }

    @GetMapping("/available/{projectId}")
    @PreAuthorize("isAuthenticated() and hasAuthority('ADMIN') or hasAuthority('PROJECTMANAGER')")
    public ResponseEntity<List<UserDto>> findAllEmployeesNotWorkingOnProject(@Valid @PathVariable Long projectId) {
        List<User> employees = projectEmployeeService.findAllEmployeesNotWorkingOnProject(projectId);
        return ResponseEntity.ok(userMapper.usersToUserDtos(employees));
    }

    @GetMapping("/projects/manager")
    @PreAuthorize("isAuthenticated() and hasAuthority('PROJECTMANAGER')")
    public ResponseEntity<List<ProjectEmployeeDto>> findManagerProjects(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader){
        final User user = authService.getUserFromToken(authHeader);
        List<ProjectEmployee> projects = projectEmployeeService.findManagerProjects(user);
        return ResponseEntity.ok(projectEmployeeMapper.projectEmployeesToProjectEmployeeDtos(projects));
    }

    @DeleteMapping("")
    @PreAuthorize("isAuthenticated() and hasAuthority('PROJECTMANAGER')")
    public ResponseEntity removeEmployeeFromProject(@Valid @RequestBody ProjectEmployeeRequest projectEmployeeRequest){
        projectEmployeeService.removeEmployeeFromProject(projectEmployeeRequest.getProjectId(), projectEmployeeRequest.getEmployeeId());
        return ResponseEntity.ok().build();
    }
}
