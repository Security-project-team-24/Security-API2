package SecurityAPI2.Repository;

import SecurityAPI2.Model.Enum.Role;
import SecurityAPI2.Model.Enum.Status;
import SecurityAPI2.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    Page<User> findAllByStatus(Pageable pageable,Status status);
    Page<User> findAll(Pageable pageable);
    List<User> findByRoleInAndStatus(List<Role> roles, Status status);
}
