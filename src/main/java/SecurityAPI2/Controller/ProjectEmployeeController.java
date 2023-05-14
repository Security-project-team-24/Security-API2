package SecurityAPI2.Controller;

import SecurityAPI2.Dto.ProjectEmployeeDto;
import SecurityAPI2.Dto.ProjectEmployeeRequest;
import SecurityAPI2.Dto.UpdateEngineerProjectDto;
import SecurityAPI2.Mapper.ProjectEmployeeMapper;
import SecurityAPI2.Model.Project;
import SecurityAPI2.Model.ProjectEmployee;
import SecurityAPI2.Model.User;
import SecurityAPI2.Security.JwtUtils;
import SecurityAPI2.Service.ProjectEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("/project-employee")
public class ProjectEmployeeController {
    @Autowired
    private ProjectEmployeeService projectEmployeeService;
    @Autowired
    private ProjectEmployeeMapper projectEmployeeMapper;
    @Autowired
    JwtUtils jwtUtils;
    @PostMapping("")
    public ResponseEntity<Long> addProjectEmployee(@Valid @RequestBody ProjectEmployeeRequest req) {
        ProjectEmployee projectEmployee = projectEmployeeService.addProjectEmployee(req);
        return ResponseEntity.ok(projectEmployee.getId());
    }
    @GetMapping("/{projectId}/engineers")
    public ResponseEntity<List<ProjectEmployeeDto>> findAllEngineersOnProject(@Valid @PathVariable Long projectId) {
        List<ProjectEmployee> engineers = projectEmployeeService.findAllEngineersOnProject(projectId);
        return ResponseEntity.ok(projectEmployeeMapper.projectEmployeesToProjectEmployeeDtos(engineers));
    }

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectEmployeeDto>> findAllEngineerProjects(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader){
        final User user = jwtUtils.getUserFromToken(authHeader);
        List<ProjectEmployee> projects = projectEmployeeService.findAllEngineerProjects(user.getId());
        return ResponseEntity.ok(projectEmployeeMapper.projectEmployeesToProjectEmployeeDtos(projects));
    }

    @PatchMapping("/description/update")
    public ResponseEntity<ProjectEmployeeDto> updateJobDescription(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader, @Valid @RequestBody UpdateEngineerProjectDto updateEngineerProjectDto){
        final User user = jwtUtils.getUserFromToken(authHeader);
        ProjectEmployee updatedProjectEmployee = projectEmployeeService.updateJobDescription(user, updateEngineerProjectDto.getProjectId(), updateEngineerProjectDto.getDescription());
        return ResponseEntity.ok(projectEmployeeMapper.projectEmployeeToProjectEmployeeDto(updatedProjectEmployee));
    }
}
