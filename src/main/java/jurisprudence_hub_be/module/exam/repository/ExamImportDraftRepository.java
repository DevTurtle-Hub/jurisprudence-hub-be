package jurisprudence_hub_be.module.exam.repository;

import jurisprudence_hub_be.module.exam.entity.ExamImportDraft;
import jurisprudence_hub_be.module.exam.enums.DraftStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamImportDraftRepository extends JpaRepository<ExamImportDraft, String> {

    Optional<ExamImportDraft> findByIdAndStatus(String id, DraftStatus status);

    List<ExamImportDraft> findByStatusOrderByCreatedAtDesc(DraftStatus status);
}
