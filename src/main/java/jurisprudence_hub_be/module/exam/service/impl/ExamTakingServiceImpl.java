package jurisprudence_hub_be.module.exam.service.impl;

import jurisprudence_hub_be.common.exception.BadRequestException;
import jurisprudence_hub_be.common.exception.ResourceNotFoundException;
import jurisprudence_hub_be.module.exam.constant.ExamConstant;
import jurisprudence_hub_be.module.exam.dto.response.ExamTakingRoomResponse;
import jurisprudence_hub_be.module.exam.entity.CandidateVerification;
import jurisprudence_hub_be.module.exam.entity.ExamQuestionEssay;
import jurisprudence_hub_be.module.exam.entity.ExamQuestionMc;
import jurisprudence_hub_be.module.exam.entity.ExamQuestionMcOption;
import jurisprudence_hub_be.module.exam.entity.ExamRoom;
import jurisprudence_hub_be.module.exam.repository.ExamQuestionEssayRepository;
import jurisprudence_hub_be.module.exam.repository.ExamQuestionMcRepository;
import jurisprudence_hub_be.module.exam.repository.ExamRoomRepository;
import jurisprudence_hub_be.common.service.RedisCacheService;
import jurisprudence_hub_be.module.exam.service.CandidateService;
import jurisprudence_hub_be.module.exam.service.ExamTakingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExamTakingServiceImpl implements ExamTakingService {

    private static final String EXAM_TAKING_CACHE_PREFIX = "exam:taking:room:";
    private static final Duration EXAM_TAKING_TTL = Duration.ofMinutes(30);

    private final ExamRoomRepository examRoomRepository;
    private final ExamQuestionMcRepository examQuestionMcRepository;
    private final ExamQuestionEssayRepository examQuestionEssayRepository;
    private final CandidateService candidateService;
    private final RedisCacheService redisCacheService;

    @Override
    @Transactional(readOnly = true)
    public ExamTakingRoomResponse getExamForTaking(String roomId, String sessionToken) {
        // 1. Xác thực sessionToken của thí sinh
        CandidateVerification candidate = candidateService.getValidSession(sessionToken);

        String lookupRoomId = roomId != null ? roomId.toUpperCase() : "";
        ExamRoom room = examRoomRepository.findById(roomId != null ? roomId : "")
                .or(() -> examRoomRepository.findByCode(lookupRoomId))
                .orElseThrow(() -> new ResourceNotFoundException(ExamConstant.MSG_ROOM_NOT_FOUND + roomId));

        // Kiểm tra xem thí sinh có thuộc phòng thi này không (null-safe)
        if (candidate.getRoom() == null || !room.getId().equals(candidate.getRoom().getId())) {
            throw new BadRequestException(ExamConstant.MSG_CANDIDATE_NOT_REGISTERED);
        }

        return getSanitizedExamPayload(room);
    }

    private ExamTakingRoomResponse getSanitizedExamPayload(ExamRoom room) {
        String cacheKey = EXAM_TAKING_CACHE_PREFIX + room.getId();
        ExamTakingRoomResponse cached = redisCacheService.get(cacheKey, ExamTakingRoomResponse.class);
        if (cached != null) {
            return cached;
        }

        // 2. Lấy câu hỏi trắc nghiệm và KHỬ HOÀN TOÀN đáp án đúng, giải thích, căn cứ pháp lý
        List<ExamQuestionMc> mcQuestions = examQuestionMcRepository.findByRoomIdOrderByOrderIndexAsc(room.getId());
        List<ExamTakingRoomResponse.SanitizedMcQuestion> sanitizedMcList = mcQuestions.stream().map(mc -> {
            List<ExamQuestionMcOption> options = mc.getOptions() != null ? mc.getOptions() : Collections.emptyList();
            List<ExamTakingRoomResponse.SanitizedMcOption> sanitizedOptions = options.stream().map(opt ->
                    ExamTakingRoomResponse.SanitizedMcOption.builder()
                            .id(opt.getId())
                            .label(opt.getLabel())
                            .text(opt.getOptionText())
                            .build()
            ).toList();

            return ExamTakingRoomResponse.SanitizedMcQuestion.builder()
                    .id(mc.getId())
                    .order(mc.getOrderIndex())
                    .context(mc.getExplanation())
                    .question(mc.getQuestionText())
                    .options(sanitizedOptions)
                    // TUYỆT ĐỐI KHÔNG SET correctAnswer, legalReference!
                    .build();
        }).toList();

        // 3. Lấy câu hỏi tự luận (KHỬ HOÀN TOÀN rubric/barem chấm điểm chi tiết để chống gian lận)
        List<ExamQuestionEssay> essayQuestions = examQuestionEssayRepository.findByRoomIdOrderByOrderIndexAsc(room.getId());
        List<ExamTakingRoomResponse.SanitizedEssayQuestion> sanitizedEssayList = essayQuestions.stream().map(essay ->
                ExamTakingRoomResponse.SanitizedEssayQuestion.builder()
                        .id(essay.getId())
                        .order(essay.getOrderIndex())
                        .title(essay.getTitle())
                        .context(essay.getContext())
                        .prompt(essay.getPrompt())
                        .maxScore(essay.getMaxScore())
                        .rubric(List.of()) // Khử sạch rubric để bảo mật tuyệt đối
                        .build()
        ).toList();

        ExamTakingRoomResponse response = ExamTakingRoomResponse.builder()
                .id(room.getId())
                .code(room.getCode())
                .title(room.getTitle())
                .description(room.getDescription())
                .durationMinutes(room.getDurationMinutes())
                .multipleChoiceQuestions(sanitizedMcList)
                .essayQuestions(sanitizedEssayList)
                .build();

        redisCacheService.set(cacheKey, response, EXAM_TAKING_TTL);
        return response;
    }
}
