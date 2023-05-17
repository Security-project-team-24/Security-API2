package SecurityAPI2.Repository;

import SecurityAPI2.Model.Engineer;
import SecurityAPI2.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface IEngineerRepository extends JpaRepository<Engineer, Long> {
    Engineer findByUser(User user);
}
