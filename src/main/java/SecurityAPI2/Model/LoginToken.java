package SecurityAPI2.Model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity(name = "login_tokens")
public class LoginToken {
    @Id
    @Column(name = "hashed_uuid")
    private String hashedUuid;
}
