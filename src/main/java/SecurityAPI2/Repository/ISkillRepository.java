package SecurityAPI2.Repository;

import SecurityAPI2.Model.Skill;
import SecurityAPI2.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ISkillRepository extends JpaRepository<Skill, Long> {
    Skill save(Skill skill);

    List<Skill> findAllByEngineerId(Long engineerId);


    @Query(nativeQuery = true, value = "delete from skills where id = :id")
    @Modifying
    @Transactional
    void deleteById(@Param("id") Long skillId);
}
