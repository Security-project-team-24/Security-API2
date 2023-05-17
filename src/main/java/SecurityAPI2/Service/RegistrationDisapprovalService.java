package SecurityAPI2.Service;

import SecurityAPI2.Model.Project;
import SecurityAPI2.Model.RegistrationDisapproval;
import SecurityAPI2.Repository.IRegistrationDisapprovalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class RegistrationDisapprovalService {

    private final IRegistrationDisapprovalRepository registrationDisapprovalRepository;
    
    public RegistrationDisapproval Create(RegistrationDisapproval registrationDisapproval) {
        return registrationDisapprovalRepository.save(registrationDisapproval);
    }
    public boolean isRegistrationDisapprovedInNearPast(String email){
        return registrationDisapprovalRepository.FindInLast2Weeks(email,LocalDateTime.now().minus(2, ChronoUnit.WEEKS)).size() != 0;
    }
}
