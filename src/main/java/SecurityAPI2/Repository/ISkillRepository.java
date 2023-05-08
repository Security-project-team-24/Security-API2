package SecurityAPI2.Repository;

import SecurityAPI2.Model.Skill;
import SecurityAPI2.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISkillRepository extends JpaRepository<Skill, Long> {
    Skill save(Skill skill);
}
