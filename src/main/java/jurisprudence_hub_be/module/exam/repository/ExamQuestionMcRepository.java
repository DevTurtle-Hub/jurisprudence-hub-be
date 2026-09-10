package jurisprudence_hub_be.module.exam.repository;

import jurisprudence_hub_be.module.exam.entity.ExamQuestionMc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamQuestionMcRepository extends JpaRepository<ExamQuestionMc, String> {

    List<ExamQuestionMc> findByRoomIdOrderByOrderIndexAsc(String roomId);

    void deleteByRoomId(String roomId);
}
