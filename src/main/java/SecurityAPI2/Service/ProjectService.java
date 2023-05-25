package SecurityAPI2.Service;

import SecurityAPI2.Model.Project;
import SecurityAPI2.Repository.IProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final IProjectRepository projectRepository;
    public Project create(Project project) {
        return projectRepository.save(project);
    }
    public Page<Project> findAll(int pageSize, int pageNumber) {
        return projectRepository.findAll(PageRequest.of(pageNumber, pageSize));
    }

}
