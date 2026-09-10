package jurisprudence_hub_be.module.exam.repository;

import jurisprudence_hub_be.module.exam.entity.CandidateVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateVerificationRepository extends JpaRepository<CandidateVerification, String> {

    Optional<CandidateVerification> findBySessionToken(String sessionToken);

    Optional<CandidateVerification> findByRoomIdAndCccd(String roomId, String cccd);

    long countByRoomId(String roomId);

    void deleteByRoomId(String roomId);
}
