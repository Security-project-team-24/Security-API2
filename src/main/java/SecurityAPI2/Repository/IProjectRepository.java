package SecurityAPI2.Repository;

import SecurityAPI2.Model.Project;
import SecurityAPI2.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface IProjectRepository extends JpaRepository<Project, Long> {
    Page<Project> findAll(Pageable pageable);
}
