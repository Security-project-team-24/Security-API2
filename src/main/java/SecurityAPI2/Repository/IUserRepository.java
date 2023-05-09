package SecurityAPI2.Repository;

import SecurityAPI2.Model.Enum.Role;
import SecurityAPI2.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User save(User user);
    List<User> findAll();
}
