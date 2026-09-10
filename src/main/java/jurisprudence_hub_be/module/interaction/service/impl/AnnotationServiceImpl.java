package jurisprudence_hub_be.module.interaction.service.impl;

import jurisprudence_hub_be.common.exception.ForbiddenException;
import jurisprudence_hub_be.common.exception.ResourceNotFoundException;
import jurisprudence_hub_be.common.security.util.SecurityUtil;
import jurisprudence_hub_be.module.auth.constant.AuthConstant;
import jurisprudence_hub_be.module.auth.entity.User;
import jurisprudence_hub_be.module.auth.repository.UserRepository;
import jurisprudence_hub_be.module.document.constant.DocumentConstant;
import jurisprudence_hub_be.module.document.entity.Lesson;
import jurisprudence_hub_be.module.document.repository.LessonRepository;
import jurisprudence_hub_be.module.interaction.constant.InteractionConstant;
import jurisprudence_hub_be.module.interaction.dto.request.AnnotationCreateRequest;
import jurisprudence_hub_be.module.interaction.dto.response.AnnotationResponse;
import jurisprudence_hub_be.module.interaction.entity.UserAnnotation;
import jurisprudence_hub_be.module.interaction.mapper.AnnotationMapper;
import jurisprudence_hub_be.module.interaction.repository.AnnotationRepository;
import jurisprudence_hub_be.module.interaction.service.AnnotationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnnotationServiceImpl implements AnnotationService {

    private static final Logger log = LoggerFactory.getLogger(AnnotationServiceImpl.class);

    private final AnnotationRepository annotationRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final AnnotationMapper annotationMapper;

    public AnnotationServiceImpl(
            AnnotationRepository annotationRepository,
            UserRepository userRepository,
            LessonRepository lessonRepository,
            AnnotationMapper annotationMapper
    ) {
        this.annotationRepository = annotationRepository;
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
        this.annotationMapper = annotationMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnotationResponse> getLessonAnnotations(String lessonId) {
        String currentUserId = SecurityUtil.requireCurrentUserId();
        List<UserAnnotation> annotations = annotationRepository
                .findAllByUserIdAndLessonIdOrderByCreatedAtDesc(currentUserId, lessonId);

        return annotationMapper.toResponseList(annotations);
    }

    @Override
    @Transactional
    public AnnotationResponse createAnnotation(String lessonId, AnnotationCreateRequest request) {
        String currentUserId = SecurityUtil.requireCurrentUserId();

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException(AuthConstant.MSG_USER_NOT_FOUND));

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException(DocumentConstant.MSG_LESSON_NOT_FOUND + lessonId));

        UserAnnotation annotation = annotationMapper.toEntity(request);
        annotation.setId(InteractionConstant.ANNOTATION_ID_PREFIX + System.currentTimeMillis());
        annotation.setUser(user);
        annotation.setLesson(lesson);
        annotation.setSelectedText(request != null && request.selectedText() != null ? request.selectedText().trim() : "");
        annotation.setKind(request != null && request.kind() != null ? request.kind().trim() : InteractionConstant.DEFAULT_KIND);
        annotation.setColor(request != null && request.color() != null ? request.color().trim() : InteractionConstant.DEFAULT_COLOR);
        annotation.setNote(request != null && request.note() != null ? request.note().trim() : null);

        UserAnnotation saved = annotationRepository.save(annotation);
        log.info("Saved annotation id: {} for user: {} on lesson: {}", saved.getId(), currentUserId, lessonId);

        return annotationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteAnnotation(String id) {
        String currentUserId = SecurityUtil.requireCurrentUserId();
        UserAnnotation annotation = annotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(InteractionConstant.MSG_ANNOTATION_NOT_FOUND + id));

        boolean isOwner = annotation.getUser() != null && annotation.getUser().getId() != null && annotation.getUser().getId().equals(currentUserId);
        boolean isAdmin = SecurityUtil.isAdmin();
        if (!isOwner && !isAdmin) {
            throw new ForbiddenException(InteractionConstant.MSG_ANNOTATION_DELETE_FORBIDDEN);
        }

        annotationRepository.delete(annotation);
        log.info("Deleted annotation id: {} by user: {} (isAdmin: {})", id, currentUserId, isAdmin);
    }
}
