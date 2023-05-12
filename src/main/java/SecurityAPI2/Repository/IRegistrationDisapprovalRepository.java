package SecurityAPI2.Repository;

import SecurityAPI2.Model.RegistrationDisapproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface IRegistrationDisapprovalRepository extends JpaRepository<RegistrationDisapproval, Long> {
    @Query("select rd from registration_disapprovals rd where rd.email=:email and rd.date>:time")
    public List<RegistrationDisapproval> FindInLast2Weeks(@Param("email")String email, @Param("time") LocalDateTime time);
}
