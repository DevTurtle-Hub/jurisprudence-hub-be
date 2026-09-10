package jurisprudence_hub_be.module.questionbank.repository;

import jurisprudence_hub_be.module.questionbank.entity.QuestionBankMcOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionBankMcOptionRepository extends JpaRepository<QuestionBankMcOption, String> {

    List<QuestionBankMcOption> findByQuestionId(String questionId);

    void deleteByQuestionId(String questionId);
}