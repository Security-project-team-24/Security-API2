package SecurityAPI2.Controller;

import SecurityAPI2.Dto.ProjectEmployeeDto;
import SecurityAPI2.Dto.ProjectEmployeeRequest;
import SecurityAPI2.Mapper.ProjectEmployeeMapper;
import SecurityAPI2.Model.ProjectEmployee;
import SecurityAPI2.Service.ProjectEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
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
}
