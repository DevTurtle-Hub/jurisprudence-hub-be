package jurisprudence_hub_be.module.document.repository;

import jurisprudence_hub_be.module.document.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, String> {

    List<Lesson> findAllByChapterIdOrderByOrderAsc(String chapterId);

    @Query("SELECT l FROM Lesson l JOIN l.chapter c ORDER BY c.order ASC, l.order ASC")
    List<Lesson> findAllLessonsOrdered();

    @Query("SELECT l FROM Lesson l WHERE l.chapter.id = :chapterId AND LOWER(l.title) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY l.order ASC")
    List<Lesson> searchByChapterIdAndTitle(@Param("chapterId") String chapterId, @Param("query") String query);
}
