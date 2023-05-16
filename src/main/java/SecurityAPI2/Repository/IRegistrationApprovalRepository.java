package SecurityAPI2.Repository;

import SecurityAPI2.Model.RegistrationApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRegistrationApprovalRepository extends JpaRepository<RegistrationApproval, String> {
    RegistrationApproval findByHMACHash(String hmacHash);
}
