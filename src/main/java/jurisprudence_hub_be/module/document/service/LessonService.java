package jurisprudence_hub_be.module.document.service;

import java.util.List;
import jurisprudence_hub_be.module.document.dto.request.LessonCreateRequest;
import jurisprudence_hub_be.module.document.dto.request.LessonUpdateRequest;
import jurisprudence_hub_be.module.document.dto.response.LessonDetailResponse;
import jurisprudence_hub_be.module.document.dto.response.LessonSummaryResponse;

public interface LessonService {

    List<LessonSummaryResponse> getAllLessons(String chapterId);

    LessonDetailResponse getLessonDetail(String id);

    LessonSummaryResponse createLesson(LessonCreateRequest request);

    LessonSummaryResponse updateLesson(String id, LessonUpdateRequest request);

    void deleteLesson(String id);
}
