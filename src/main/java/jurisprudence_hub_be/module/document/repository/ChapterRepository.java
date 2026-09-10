package jurisprudence_hub_be.module.document.repository;

import jurisprudence_hub_be.module.document.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, String> {

    List<Chapter> findAllByOrderByOrderAsc();

    @Query("""
        SELECT DISTINCT c FROM Chapter c
        LEFT JOIN c.lessons l
        WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(l.title) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY c.order ASC
    """)
    List<Chapter> searchChapters(@Param("query") String query);
}
