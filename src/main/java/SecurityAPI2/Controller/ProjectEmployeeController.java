package SecurityAPI2.Controller;

import SecurityAPI2.Dto.ProjectEmployeeDto;
import SecurityAPI2.Dto.ProjectEmployeeRequest;
import SecurityAPI2.Model.ProjectEmployee;
import SecurityAPI2.Service.ProjectEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/project-employee")
public class ProjectEmployeeController {
    @Autowired
    private ProjectEmployeeService projectEmployeeService;
    @PostMapping("")
    public ResponseEntity<Long> addProjectEmployee(@Valid @RequestBody ProjectEmployeeRequest req) {
        ProjectEmployee projectEmployee = projectEmployeeService.addProjectEmployee(req);
        return ResponseEntity.ok(projectEmployee.getId());
    }
}
