package jurisprudence_hub_be.module.document.service;

import jurisprudence_hub_be.module.document.dto.request.ChapterCreateRequest;
import jurisprudence_hub_be.module.document.dto.request.ChapterUpdateRequest;
import jurisprudence_hub_be.module.document.dto.response.ChapterResponse;

import java.util.List;

public interface ChapterService {

    List<ChapterResponse> getAllChapters(Boolean includeLessons, String search);

    ChapterResponse getChapterById(String id);

    ChapterResponse createChapter(ChapterCreateRequest request);

    ChapterResponse updateChapter(String id, ChapterUpdateRequest request);

    void deleteChapter(String id);
}
