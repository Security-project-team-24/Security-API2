package SecurityAPI2.Model;

import SecurityAPI2.Model.Enum.Seniority;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity(name = "engineers")
public class Engineer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(mappedBy = "engineer", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Skill> skills;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @Column(name = "cvUrl", columnDefinition="TEXT")
    private String cvUrl;
    @Column(name = "seniority")
    private Seniority seniority;
    @Column(name = "hire_date", nullable = true)
    private LocalDate hireDate;

    public Engineer(User user, Seniority seniority) {
        this.user = user;
        this.seniority = seniority;
    }
}
