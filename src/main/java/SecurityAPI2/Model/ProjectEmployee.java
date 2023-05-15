package SecurityAPI2.Model;

import SecurityAPI2.Model.Enum.Role;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

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
    @Column(name = "startDate")
    private Date startDate;
    @Column(name = "endDate")
    private Date endDate;
    public ProjectEmployee(User employee, Project project, String jobDescription, Date startDate, Date endDate){
        this.employee = employee;
        this.project = project;
        this.jobDescription = jobDescription;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
