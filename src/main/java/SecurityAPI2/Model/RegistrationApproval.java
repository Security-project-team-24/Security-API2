package SecurityAPI2.Model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity(name = "registration_approvals")
public class RegistrationApproval {
    @Id
    @Column(name = "hashed_uuid")
    private String hashedUuid;
}
