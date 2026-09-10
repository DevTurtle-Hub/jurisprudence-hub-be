package jurisprudence_hub_be.module.questionbank.repository;

import jurisprudence_hub_be.module.questionbank.entity.QuestionBank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionBankRepository extends JpaRepository<QuestionBank, String> {

    // Find by draft status
    Page<QuestionBank> findByIsDraft(boolean isDraft, Pageable pageable);

    // Find by category and draft status
    Page<QuestionBank> findByCategoryAndIsDraft(String category, boolean isDraft, Pageable pageable);

    // Find by question type and draft status
    Page<QuestionBank> findByQuestionTypeAndIsDraft(String questionType, boolean isDraft, Pageable pageable);

    // Search by question text
    @Query("SELECT q FROM QuestionBank q WHERE q.questionText LIKE %:keyword% AND q.isDraft = :isDraft")
    Page<QuestionBank> searchByQuestionText(@Param("keyword") String keyword, @Param("isDraft") boolean isDraft, Pageable pageable);

    // Find by created by
    Page<QuestionBank> findByCreatedByAndIsDraft(String createdBy, boolean isDraft, Pageable pageable);

    // Complex search with multiple filters
    @Query("SELECT q FROM QuestionBank q WHERE " +
           "(:category IS NULL OR q.category = :category) AND " +
           "(:questionType IS NULL OR q.questionType = :questionType) AND " +
           "(:keyword IS NULL OR q.questionText LIKE %:keyword%) AND " +
           "q.isDraft = :isDraft")
    Page<QuestionBank> searchQuestions(
            @Param("category") String category,
            @Param("questionType") String questionType,
            @Param("keyword") String keyword,
            @Param("isDraft") boolean isDraft,
            Pageable pageable);

    // Count by category
    long countByCategory(String category);

    // Count by question type
    long countByQuestionType(String questionType);

    // Count drafts vs published
    long countByIsDraft(boolean isDraft);
}