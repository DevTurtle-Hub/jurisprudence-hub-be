package jurisprudence_hub_be.module.document.service.impl;

import jurisprudence_hub_be.common.exception.ResourceNotFoundException;
import jurisprudence_hub_be.common.security.util.SecurityUtil;
import jurisprudence_hub_be.module.document.constant.DocumentConstant;
import jurisprudence_hub_be.module.document.dto.request.LessonCreateRequest;
import jurisprudence_hub_be.module.document.dto.request.LessonContentRequest;
import jurisprudence_hub_be.module.document.dto.request.LessonUpdateRequest;
import jurisprudence_hub_be.module.document.dto.response.LessonContentResponse;
import jurisprudence_hub_be.module.document.dto.response.LessonDetailResponse;
import jurisprudence_hub_be.module.document.dto.response.LessonNavigationResponse;
import jurisprudence_hub_be.module.document.dto.response.LessonSummaryResponse;
import jurisprudence_hub_be.module.document.entity.Chapter;
import jurisprudence_hub_be.module.document.entity.Lesson;
import jurisprudence_hub_be.module.document.entity.LessonContent;
import jurisprudence_hub_be.module.document.mapper.LessonMapper;
import jurisprudence_hub_be.module.document.repository.ChapterRepository;
import jurisprudence_hub_be.module.document.repository.LessonContentRepository;
import jurisprudence_hub_be.module.document.repository.LessonRepository;
import jurisprudence_hub_be.module.document.service.LessonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LessonServiceImpl implements LessonService {

    private static final Logger log = LoggerFactory.getLogger(LessonServiceImpl.class);

    private final LessonRepository lessonRepository;
    private final ChapterRepository chapterRepository;
    private final LessonContentRepository lessonContentRepository;
    private final LessonMapper lessonMapper;

    public LessonServiceImpl(
            LessonRepository lessonRepository,
            ChapterRepository chapterRepository,
            LessonContentRepository lessonContentRepository,
            LessonMapper lessonMapper
    ) {
        this.lessonRepository = lessonRepository;
        this.chapterRepository = chapterRepository;
        this.lessonContentRepository = lessonContentRepository;
        this.lessonMapper = lessonMapper;
    }

    
    @Override
    @Transactional(readOnly = true)
    public List<LessonSummaryResponse> getAllLessons(String chapterId) {
        List<Lesson> lessons;
        if (chapterId != null && !chapterId.isBlank()) {
            lessons = lessonRepository.findAllByChapterIdOrderByOrderAsc(chapterId.trim());
        } else {
            lessons = lessonRepository.findAllLessonsOrdered();
        }
        return lessonMapper.toSummaryResponseList(lessons != null ? lessons : new ArrayList<>());
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public LessonDetailResponse getLessonDetail(String id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(DocumentConstant.MSG_LESSON_NOT_FOUND + id));

        LessonContent content = lessonContentRepository.findById(id).orElse(null);
        LessonContentResponse contentResponse = content != null
                ? lessonMapper.toContentResponse(content)
                : LessonContentResponse.empty();

        LessonNavigationResponse navigation = computeNavigation(id);

        String chapterId = lesson.getChapter() != null ? lesson.getChapter().getId() : null;
        String chapterTitle = lesson.getChapter() != null ? lesson.getChapter().getTitle() : null;

        return new LessonDetailResponse(
                lesson.getId(),
                chapterId,
                chapterTitle,
                lesson.getTitle(),
                lesson.getOrder(),
                contentResponse,
                navigation
        );
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public LessonSummaryResponse createLesson(LessonCreateRequest request) {
        String reqChapterId = request != null ? request.chapterId() : "";
        Chapter chapter = chapterRepository.findById(reqChapterId)
                .orElseThrow(() -> new ResourceNotFoundException(DocumentConstant.MSG_CHAPTER_NOT_FOUND + reqChapterId));

        int lessonOrder = request != null ? request.order() : 1;
        String lessonId = DocumentConstant.LESSON_ID_PREFIX + chapter.getOrder() + "-" + lessonOrder;
        if (lessonRepository.existsById(lessonId)) {
            lessonId = DocumentConstant.LESSON_ID_PREFIX + UUID.randomUUID().toString().replace("-", "").substring(0, DocumentConstant.DEFAULT_ID_RANDOM_LENGTH);
        }

        String createdBy = SecurityUtil.getCurrentUserId().orElse(null);

        Lesson lesson = lessonMapper.toEntity(request);
        lesson.setId(lessonId);
        lesson.setChapter(chapter);
        lesson.setTitle(request != null && request.title() != null ? request.title().trim() : "");
        lesson.setOrder(lessonOrder);
        lesson.setCreatedBy(createdBy);

        LessonContentRequest contentRequest = (request != null && request.content() != null) ? request.content() : LessonContentRequest.empty();
        LessonContent lessonContent = lessonMapper.toContentEntity(contentRequest);
        lessonContent.setLessonId(lesson.getId());
        lessonContent.setLesson(lesson);
        lesson.setContent(lessonContent);

        Lesson savedLesson = lessonRepository.save(lesson);
        log.info("Created lesson id: {} in chapter: {}", savedLesson.getId(), chapter.getId());

        return lessonMapper.toSummaryResponse(savedLesson);
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public LessonSummaryResponse updateLesson(String id, LessonUpdateRequest request) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(DocumentConstant.MSG_LESSON_NOT_FOUND + id));

        if (request != null) {
            lessonMapper.updateEntityFromRequest(request, lesson);

            if (request.content() != null) {
                LessonContent content = lesson.getContent();
                if (content == null) {
                    content = lessonContentRepository.findById(id)
                            .orElseGet(() -> {
                                LessonContent newContent = LessonContent.builder().lessonId(lesson.getId()).lesson(lesson).build();
                                lesson.setContent(newContent);
                                return newContent;
                            });
                    content.setLesson(lesson);
                    content.setLessonId(lesson.getId());
                    lesson.setContent(content);
                }
                lessonMapper.updateContentFromRequest(request.content(), content);
            }
        }

        @SuppressWarnings("null")
        Lesson savedLesson = lessonRepository.save(lesson);
        log.info("Updated lesson id: {}", savedLesson.getId());
        return lessonMapper.toSummaryResponse(savedLesson);
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public void deleteLesson(String id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(DocumentConstant.MSG_LESSON_NOT_FOUND + id));
        lessonRepository.delete(lesson);
        log.info("Deleted lesson id: {}", id);
    }

    private LessonNavigationResponse computeNavigation(String currentLessonId) {
        List<Lesson> allOrdered = lessonRepository.findAllLessonsOrdered();

        String prevId = null;
        String nextId = null;

        if (allOrdered != null && !allOrdered.isEmpty()) {
            for (int i = 0; i < allOrdered.size(); i++) {
                Lesson currentLesson = allOrdered.get(i);
                if (currentLesson != null && currentLesson.getId() != null && currentLesson.getId().equals(currentLessonId)) {
                    if (i > 0) {
                        Lesson prevLesson = allOrdered.get(i - 1);
                        if (prevLesson != null) {
                            prevId = prevLesson.getId();
                        }
                    }
                    if (i < allOrdered.size() - 1) {
                        Lesson nextLesson = allOrdered.get(i + 1);
                        if (nextLesson != null) {
                            nextId = nextLesson.getId();
                        }
                    }
                    break;
                }
            }
        }

        return new LessonNavigationResponse(prevId, nextId);
    }
}
