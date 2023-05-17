package SecurityAPI2.Model;

import SecurityAPI2.Model.Enum.Role;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity(name = "registration_approvals")
public class RegistrationApproval {
    @Id
    @Column(name = "hmac_hash")
    private String HMACHash;
    @Column(name = "email")
    private String email;
    @Column(name = "date")
    private LocalDateTime date;
    
    public RegistrationApproval(User user){
        email = user.getEmail();
        date = LocalDateTime.now();
    }
}
