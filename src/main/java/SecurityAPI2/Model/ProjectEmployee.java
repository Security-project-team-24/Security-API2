package SecurityAPI2.Model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity(name = "projectEmployee")
public class ProjectEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="employeeId")
    private User employee;
    @Column(name = "jobDescription")
    private String jobDescription;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}
