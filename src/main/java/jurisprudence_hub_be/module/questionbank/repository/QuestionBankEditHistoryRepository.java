package jurisprudence_hub_be.module.questionbank.repository;

import jurisprudence_hub_be.module.questionbank.entity.QuestionBankEditHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionBankEditHistoryRepository extends JpaRepository<QuestionBankEditHistory, String> {

    List<QuestionBankEditHistory> findByQuestionIdOrderByEditedAtDesc(String questionId);

    Page<QuestionBankEditHistory> findByEditedBy(String editedBy, Pageable pageable);

    Page<QuestionBankEditHistory> findByQuestionId(String questionId, Pageable pageable);

    void deleteByQuestionId(String questionId);
}