package SecurityAPI2.Repository;

import SecurityAPI2.Model.Engineer;
import SecurityAPI2.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

public interface IEngineerRepository extends JpaRepository<Engineer, Long> {
    Engineer findByUser(User user);
    Page<Engineer> findByUserEmailContainingIgnoreCaseAndUserNameContainingIgnoreCaseAndUserSurnameContainingIgnoreCaseAndHireDateBetween(String email, String firstName, String surname, LocalDate fromDate, LocalDate toDate, Pageable pageable);
}
