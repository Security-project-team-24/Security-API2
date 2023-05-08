package SecurityAPI2.Service;

import SecurityAPI2.Dto.ProjectDto;
import SecurityAPI2.Dto.ProjectEmployeeRequest;
import SecurityAPI2.Model.Project;
import SecurityAPI2.Model.ProjectEmployee;
import SecurityAPI2.Repository.IProjectEmployeeRepository;
import SecurityAPI2.Repository.IProjectRepository;
import SecurityAPI2.Repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    @Autowired
    private IProjectRepository projectRepository;
    public Project Create(Project project) {
        return projectRepository.save(project);
    }
    public List<Project> FindAll() {
        return projectRepository.findAll();
    }

}
