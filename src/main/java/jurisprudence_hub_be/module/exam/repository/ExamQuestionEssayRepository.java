package jurisprudence_hub_be.module.exam.repository;

import jurisprudence_hub_be.module.exam.entity.ExamQuestionEssay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamQuestionEssayRepository extends JpaRepository<ExamQuestionEssay, String> {

    List<ExamQuestionEssay> findByRoomIdOrderByOrderIndexAsc(String roomId);

    void deleteByRoomId(String roomId);
}
