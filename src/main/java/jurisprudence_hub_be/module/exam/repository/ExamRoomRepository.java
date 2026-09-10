package jurisprudence_hub_be.module.exam.repository;

import jurisprudence_hub_be.module.exam.entity.ExamRoom;
import jurisprudence_hub_be.module.exam.enums.ExamRoomStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExamRoomRepository extends JpaRepository<ExamRoom, String> {

    boolean existsByCode(String code);

    Optional<ExamRoom> findByCode(String code);

    @Query("""
        SELECT r FROM ExamRoom r
        WHERE (:status IS NULL OR r.status = :status)
        AND (:search IS NULL OR :search = '' 
             OR LOWER(r.title) LIKE LOWER(CONCAT('%', :search, '%'))
             OR LOWER(r.code) LIKE LOWER(CONCAT('%', :search, '%'))
             OR LOWER(r.description) LIKE LOWER(CONCAT('%', :search, '%')))
        ORDER BY r.createdAt DESC
    """)
    Page<ExamRoom> searchRooms(
            @Param("search") String search,
            @Param("status") ExamRoomStatus status,
            Pageable pageable
    );
}
