package jurisprudence_hub_be.module.exam.repository;

import jurisprudence_hub_be.module.exam.entity.ExamQuestionEssay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

@Repository
public interface ExamQuestionEssayRepository extends JpaRepository<ExamQuestionEssay, String> {

    List<ExamQuestionEssay> findByRoomIdOrderByOrderIndexAsc(String roomId);

    void deleteByRoomId(String roomId);

    @Query("SELECT q.room.id, COUNT(q) FROM ExamQuestionEssay q WHERE q.room.id IN :roomIds GROUP BY q.room.id")
    List<Object[]> countByRoomIdsGrouped(@Param("roomIds") Collection<String> roomIds);
}
