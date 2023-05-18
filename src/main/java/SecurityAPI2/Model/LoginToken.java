package SecurityAPI2.Model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity(name = "login_tokens")
public class LoginToken {
    @Id
    @Column(name = "uuid")
    private UUID uuid;
    @Column(name = "email")
    private String email;
    @Column(name = "expiration_date_time")
    private LocalDateTime expirationDateTime;
}
