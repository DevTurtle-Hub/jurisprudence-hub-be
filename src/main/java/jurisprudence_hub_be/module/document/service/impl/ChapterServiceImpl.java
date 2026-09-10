package jurisprudence_hub_be.module.document.service.impl;

import jurisprudence_hub_be.common.exception.ResourceNotFoundException;
import jurisprudence_hub_be.module.document.constant.DocumentConstant;
import jurisprudence_hub_be.module.document.dto.request.ChapterCreateRequest;
import jurisprudence_hub_be.module.document.dto.request.ChapterUpdateRequest;
import jurisprudence_hub_be.module.document.dto.response.ChapterResponse;
import jurisprudence_hub_be.module.document.entity.Chapter;
import jurisprudence_hub_be.module.document.mapper.ChapterMapper;
import jurisprudence_hub_be.module.document.repository.ChapterRepository;
import jurisprudence_hub_be.module.document.service.ChapterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class ChapterServiceImpl implements ChapterService {

    private static final Logger log = LoggerFactory.getLogger(ChapterServiceImpl.class);

    private final ChapterRepository chapterRepository;
    private final ChapterMapper chapterMapper;

    public ChapterServiceImpl(ChapterRepository chapterRepository, ChapterMapper chapterMapper) {
        this.chapterRepository = chapterRepository;
        this.chapterMapper = chapterMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterResponse> getAllChapters(Boolean includeLessons, String search) {
        boolean shouldInclude = includeLessons == null || includeLessons;

        List<Chapter> chapters;
        if (search != null && !search.isBlank()) {
            chapters = chapterRepository.searchChapters(search.trim());
        } else {
            chapters = chapterRepository.findAllByOrderByOrderAsc();
        }

        return chapters.stream()
                .map(chapter -> {
                    ChapterResponse response = chapterMapper.toResponse(chapter);
                    if (response == null) {
                        return null;
                    }
                    if (!shouldInclude) {
                        return new ChapterResponse(
                                response.id(),
                                response.title(),
                                response.order(),
                                response.description(),
                                Collections.emptyList(),
                                response.createdAt(),
                                response.updatedAt()
                        );
                    }
                    var sortedLessons = response.lessons() != null
                            ? response.lessons().stream()
                                    .filter(java.util.Objects::nonNull)
                                    .sorted(Comparator.comparingInt(l -> l.order()))
                                    .toList()
                            : Collections.<jurisprudence_hub_be.module.document.dto.response.LessonSummaryResponse>emptyList();

                    return new ChapterResponse(
                            response.id(),
                            response.title(),
                            response.order(),
                            response.description(),
                            sortedLessons,
                            response.createdAt(),
                            response.updatedAt()
                    );
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public ChapterResponse getChapterById(String id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(DocumentConstant.MSG_CHAPTER_NOT_FOUND + id));

        return chapterMapper.toResponse(chapter);
    }

    @Override
    @Transactional
    public ChapterResponse createChapter(ChapterCreateRequest request) {
        int order = request != null ? request.order() : 1;
        String chapterId = DocumentConstant.CHAPTER_ID_PREFIX + order;
        if (chapterRepository.existsById(chapterId)) {
            chapterId = DocumentConstant.CHAPTER_ID_PREFIX + (chapterRepository.count() + 1);
        }

        Chapter chapter = chapterMapper.toEntity(request);
        chapter.setId(chapterId);
        chapter.setTitle(request != null && request.title() != null ? request.title().trim() : "");
        chapter.setOrder(order);
        chapter.setDescription(request != null && request.description() != null ? request.description().trim() : null);

        Chapter saved = chapterRepository.save(chapter);
        log.info("Created chapter with id: {}", saved.getId());

        return chapterMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public ChapterResponse updateChapter(String id, ChapterUpdateRequest request) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(DocumentConstant.MSG_CHAPTER_NOT_FOUND + id));

        chapterMapper.updateEntityFromRequest(request, chapter);
        Chapter saved = chapterRepository.save(chapter);
        log.info("Updated chapter with id: {}", saved.getId());

        return chapterMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public void deleteChapter(String id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(DocumentConstant.MSG_CHAPTER_NOT_FOUND + id));

        chapterRepository.delete(chapter);
        log.info("Deleted chapter with id: {}", id);
    }
}
