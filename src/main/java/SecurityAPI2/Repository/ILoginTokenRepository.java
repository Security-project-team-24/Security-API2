package SecurityAPI2.Repository;

import SecurityAPI2.Model.LoginToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ILoginTokenRepository extends JpaRepository<LoginToken, Long> {
    LoginToken findByHashedUuid(String hashedUuid);
}
