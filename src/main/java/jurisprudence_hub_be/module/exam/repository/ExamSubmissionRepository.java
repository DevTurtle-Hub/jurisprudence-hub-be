package jurisprudence_hub_be.module.exam.repository;

import jurisprudence_hub_be.module.exam.entity.ExamSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExamSubmissionRepository extends JpaRepository<ExamSubmission, String> {

    Optional<ExamSubmission> findByReceiptId(String receiptId);

    boolean existsByRoomIdAndCandidateVerificationId(String roomId, String candidateVerificationId);

    Optional<ExamSubmission> findByCandidateVerificationId(String candidateVerificationId);

    void deleteByRoomId(String roomId);
}
