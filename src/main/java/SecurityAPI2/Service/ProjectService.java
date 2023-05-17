package SecurityAPI2.Service;

import SecurityAPI2.Dto.ProjectDto;
import SecurityAPI2.Dto.ProjectEmployeeRequest;
import SecurityAPI2.Model.Project;
import SecurityAPI2.Model.ProjectEmployee;
import SecurityAPI2.Repository.IProjectEmployeeRepository;
import SecurityAPI2.Repository.IProjectRepository;
import SecurityAPI2.Repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final IProjectRepository projectRepository;
    public Project Create(Project project) {
        return projectRepository.save(project);
    }
    public Page<Project> FindAll(int pageSize, int pageNumber) {
        return projectRepository.findAll(PageRequest.of(pageNumber, pageSize));
    }

}
