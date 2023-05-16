package SecurityAPI2.Service;

import SecurityAPI2.Exceptions.RegistrationApprovalNonExistingException;
import SecurityAPI2.Model.RegistrationApproval;
import SecurityAPI2.Repository.IRegistrationApprovalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegistrationApprovalService {
    @Autowired
    private IRegistrationApprovalRepository registrationApprovalRepository;
    
    public RegistrationApproval findById(String id){
        RegistrationApproval registrationApproval = registrationApprovalRepository.findByHMACHash(id);
        if(registrationApproval != null) return registrationApproval;
        throw new RegistrationApprovalNonExistingException();
    }
    public RegistrationApproval save(RegistrationApproval registrationApproval){
        return registrationApprovalRepository.save(registrationApproval);
    }
    public void delete(RegistrationApproval registrationApproval){
        registrationApprovalRepository.delete(registrationApproval);
    }
}
