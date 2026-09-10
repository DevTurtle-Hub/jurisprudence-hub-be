package jurisprudence_hub_be.module.exam.repository;

import jurisprudence_hub_be.module.exam.entity.SubmissionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionAnswerRepository extends JpaRepository<SubmissionAnswer, String> {

    List<SubmissionAnswer> findBySubmissionId(String submissionId);
}
