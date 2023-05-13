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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "email")
    private String email;
    @Column(name = "date")
    private LocalDateTime date;
    @Column(name = "name")
    private String name;
    @Column(name = "surname")
    private String surname;
    @Column(name = "phoneNumber")
    private String phoneNumber;
    @Column(name = "role")
    private Role role;
    
    public RegistrationApproval(User user){
        email = user.getEmail();
        date = LocalDateTime.now();
        name = user.getName();
        surname = user.getSurname();
        role = user.getRole();
        phoneNumber = user.getPhoneNumber();
    }
}
