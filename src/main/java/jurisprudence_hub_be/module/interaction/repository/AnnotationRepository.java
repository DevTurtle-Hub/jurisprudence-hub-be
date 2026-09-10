package jurisprudence_hub_be.module.interaction.repository;

import jurisprudence_hub_be.module.interaction.entity.UserAnnotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnnotationRepository extends JpaRepository<UserAnnotation, String> {

    List<UserAnnotation> findAllByUserIdAndLessonIdOrderByCreatedAtDesc(String userId, String lessonId);

    Optional<UserAnnotation> findByIdAndUserId(String id, String userId);
}
