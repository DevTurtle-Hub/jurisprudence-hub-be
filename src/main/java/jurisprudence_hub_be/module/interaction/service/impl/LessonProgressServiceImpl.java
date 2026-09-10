package jurisprudence_hub_be.module.interaction.service.impl;

import jurisprudence_hub_be.common.exception.ResourceNotFoundException;
import jurisprudence_hub_be.common.security.util.SecurityUtil;
import jurisprudence_hub_be.module.auth.constant.AuthConstant;
import jurisprudence_hub_be.module.auth.entity.User;
import jurisprudence_hub_be.module.auth.repository.UserRepository;
import jurisprudence_hub_be.module.document.constant.DocumentConstant;
import jurisprudence_hub_be.module.document.entity.Lesson;
import jurisprudence_hub_be.module.document.repository.LessonRepository;
import jurisprudence_hub_be.module.interaction.dto.request.LessonProgressRequest;
import jurisprudence_hub_be.module.interaction.dto.response.LessonProgressResponse;
import jurisprudence_hub_be.module.interaction.entity.UserLessonProgress;
import jurisprudence_hub_be.module.interaction.mapper.LessonProgressMapper;
import jurisprudence_hub_be.module.interaction.repository.LessonProgressRepository;
import jurisprudence_hub_be.module.interaction.service.LessonProgressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class LessonProgressServiceImpl implements LessonProgressService {

    private static final Logger log = LoggerFactory.getLogger(LessonProgressServiceImpl.class);

    private final LessonProgressRepository lessonProgressRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressMapper lessonProgressMapper;

    public LessonProgressServiceImpl(
            LessonProgressRepository lessonProgressRepository,
            UserRepository userRepository,
            LessonRepository lessonRepository,
            LessonProgressMapper lessonProgressMapper
    ) {
        this.lessonProgressRepository = lessonProgressRepository;
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
        this.lessonProgressMapper = lessonProgressMapper;
    }

    @Override
    @Transactional
    public LessonProgressResponse updateProgress(String lessonId, LessonProgressRequest request) {
        String currentUserId = SecurityUtil.requireCurrentUserId();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException(AuthConstant.MSG_USER_NOT_FOUND));

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException(DocumentConstant.MSG_LESSON_NOT_FOUND + lessonId));

        boolean completed = request != null && request.completed();

        UserLessonProgress progress = lessonProgressRepository
                .findByUserIdAndLessonId(currentUserId, lessonId)
                .orElseGet(() -> UserLessonProgress.builder()
                        .user(user)
                        .lesson(lesson)
                        .completed(false)
                        .lastReadAt(Instant.now())
                        .build());

        progress.setCompleted(completed);
        progress.setLastReadAt(Instant.now());

        UserLessonProgress saved = lessonProgressRepository.save(progress);
        log.info("Updated progress for user: {} on lesson: {}, completed: {}", currentUserId, lessonId, completed);

        return lessonProgressMapper.toResponse(saved);
    }
}
