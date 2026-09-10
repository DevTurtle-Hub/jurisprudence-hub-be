package jurisprudence_hub_be.module.exam.repository;

import jurisprudence_hub_be.module.exam.entity.ExamQuestionMcOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamQuestionMcOptionRepository extends JpaRepository<ExamQuestionMcOption, String> {

    List<ExamQuestionMcOption> findByQuestionIdOrderByLabelAsc(String questionId);
}
