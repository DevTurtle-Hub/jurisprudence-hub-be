package jurisprudence_hub_be.module.document.repository;

import jurisprudence_hub_be.module.document.entity.LessonContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonContentRepository extends JpaRepository<LessonContent, String> {
}
