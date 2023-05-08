package SecurityAPI2.Service;

import SecurityAPI2.Dto.ProjectEmployeeRequest;
import SecurityAPI2.Model.Project;
import SecurityAPI2.Model.ProjectEmployee;
import SecurityAPI2.Model.User;
import SecurityAPI2.Repository.IProjectEmployeeRepository;
import SecurityAPI2.Repository.IProjectRepository;
import SecurityAPI2.Repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectEmployeeService {
    @Autowired
    private IProjectEmployeeRepository projectEmployeeRepository;
    @Autowired
    private IProjectRepository projectRepository;
    @Autowired
    private IUserRepository userRepository;
    public ProjectEmployee addProjectEmployee(ProjectEmployeeRequest request) {
        Project project = projectRepository.getById(request.getProjectId());
        User employee = userRepository.getById(request.getEmployeeId());
        ProjectEmployee projectEmployee = new ProjectEmployee(employee, project, request.getJobDescription());
        projectEmployeeRepository.save(projectEmployee);
        return projectEmployee;
    }
}
