package jurisprudence_hub_be.module.exam.service.impl;

import jurisprudence_hub_be.common.exception.BadRequestException;
import jurisprudence_hub_be.common.exception.ResourceNotFoundException;
import jurisprudence_hub_be.module.exam.constant.ExamConstant;
import jurisprudence_hub_be.module.exam.dto.request.CreateEssayQuestionRequest;
import jurisprudence_hub_be.module.exam.dto.request.CreateExamRoomRequest;
import jurisprudence_hub_be.module.exam.dto.request.CreateMcOptionRequest;
import jurisprudence_hub_be.module.exam.dto.request.CreateMcQuestionRequest;
import jurisprudence_hub_be.module.exam.dto.response.ExamRoomListResponse;
import jurisprudence_hub_be.module.exam.dto.response.ExamRoomResponse;
import jurisprudence_hub_be.module.exam.dto.response.ExamRoomSummaryResponse;
import jurisprudence_hub_be.module.exam.entity.ExamQuestionEssay;
import jurisprudence_hub_be.module.exam.entity.ExamQuestionMc;
import jurisprudence_hub_be.module.exam.entity.ExamQuestionMcOption;
import jurisprudence_hub_be.module.exam.entity.ExamRoom;
import jurisprudence_hub_be.module.exam.enums.ExamRoomStatus;
import jurisprudence_hub_be.module.exam.repository.CandidateVerificationRepository;
import jurisprudence_hub_be.module.exam.repository.ExamQuestionEssayRepository;
import jurisprudence_hub_be.common.service.RedisCacheService;
import jurisprudence_hub_be.module.exam.repository.ExamQuestionMcOptionRepository;
import jurisprudence_hub_be.module.exam.repository.ExamQuestionMcRepository;
import jurisprudence_hub_be.module.exam.repository.ExamRoomRepository;
import jurisprudence_hub_be.module.exam.repository.ExamSubmissionRepository;
import jurisprudence_hub_be.module.exam.service.ExamRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExamRoomServiceImpl implements ExamRoomService {

    private static final String EXAM_ROOM_CACHE_PREFIX = "exam:room:";
    private static final String EXAM_TAKING_CACHE_PREFIX = "exam:taking:room:";
    private static final Duration EXAM_ROOM_TTL = Duration.ofHours(2);

    private final ExamRoomRepository examRoomRepository;
    private final ExamQuestionMcRepository examQuestionMcRepository;
    private final ExamQuestionMcOptionRepository examQuestionMcOptionRepository;
    private final ExamQuestionEssayRepository examQuestionEssayRepository;
    private final ExamSubmissionRepository examSubmissionRepository;
    private final CandidateVerificationRepository candidateVerificationRepository;
    private final RedisCacheService redisCacheService;

    @Override
    @Transactional
    public ExamRoomResponse createRoom(CreateExamRoomRequest request, String createdBy) {
        String code = request != null && request.getCode() != null ? request.getCode().trim().toUpperCase() : "";
        if (examRoomRepository.existsByCode(code)) {
            throw new BadRequestException(String.format(ExamConstant.MSG_ROOM_CODE_EXISTS, code));
        }

        String roomId = "room-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String title = request != null && request.getTitle() != null ? request.getTitle().trim() : "";
        String description = request != null ? request.getDescription() : null;
        int duration = (request != null && request.getDurationMinutes() > 0) ? request.getDurationMinutes() : 60;
        ExamRoomStatus status = (request != null && request.getStatus() != null) ? request.getStatus() : ExamRoomStatus.OPEN;

        ExamRoom room = ExamRoom.builder()
                .id(roomId)
                .code(code)
                .title(title)
                .description(description)
                .durationMinutes(duration)
                .totalAttempts(0)
                .status(status)
                .createdBy(createdBy)
                .build();

        ExamRoom savedRoom = examRoomRepository.save(room);

        // Lưu danh sách câu hỏi trắc nghiệm
        List<ExamRoomResponse.McQuestionDetail> mcDetails = new ArrayList<>();
        List<ExamQuestionMc> mcsToSave = new ArrayList<>();
        List<ExamQuestionMcOption> optsToSave = new ArrayList<>();

        if (request.getMultipleChoiceQuestions() != null) {
            int order = 1;
            for (CreateMcQuestionRequest mcReq : request.getMultipleChoiceQuestions()) {
                String mcId = "mc-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
                String rawAns = mcReq.getCorrectAnswer() != null ? mcReq.getCorrectAnswer().trim() : "";
                if (rawAns.length() == 1 && rawAns.matches("(?i)[A-D]")) {
                    rawAns = rawAns.toUpperCase();
                }

                ExamQuestionMc mc = ExamQuestionMc.builder()
                        .id(mcId)
                        .room(savedRoom)
                        .orderIndex(mcReq.getOrder() > 0 ? mcReq.getOrder() : order)
                        .questionText(mcReq.getQuestion())
                        .context(mcReq.getContext())
                        .correctAnswer(rawAns)
                        .explanation(mcReq.getExplanation())
                        .legalReference(mcReq.getLegalReference())
                        .build();

                mcsToSave.add(mc);

                List<ExamRoomResponse.McOptionDetail> optDetails = new ArrayList<>();
                if (mcReq.getOptions() != null) {
                    for (CreateMcOptionRequest optReq : mcReq.getOptions()) {
                        String optId = "opt-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
                        ExamQuestionMcOption opt = ExamQuestionMcOption.builder()
                                .id(optId)
                                .question(mc)
                                .label(optReq.getLabel() != null ? optReq.getLabel().toUpperCase() : "A")
                                .optionText(optReq.getText())
                                .build();
                        optsToSave.add(opt);

                        optDetails.add(ExamRoomResponse.McOptionDetail.builder()
                                .id(optId)
                                .label(opt.getLabel())
                                .text(opt.getOptionText())
                                .build());
                    }
                }

                mcDetails.add(ExamRoomResponse.McQuestionDetail.builder()
                        .id(mcId)
                        .order(mc.getOrderIndex())
                        .question(mc.getQuestionText())
                        .context(mc.getContext())
                        .options(optDetails)
                        .correctAnswer(mc.getCorrectAnswer())
                        .explanation(mc.getExplanation())
                        .legalReference(mc.getLegalReference())
                        .build());
                order++;
            }
        }

        if (!mcsToSave.isEmpty()) {
            examQuestionMcRepository.saveAll(mcsToSave);
        }
        if (!optsToSave.isEmpty()) {
            examQuestionMcOptionRepository.saveAll(optsToSave);
        }

        // Lưu danh sách câu hỏi tự luận
        List<ExamRoomResponse.EssayQuestionDetail> essayDetails = new ArrayList<>();
        List<ExamQuestionEssay> essaysToSave = new ArrayList<>();

        if (request.getEssayQuestions() != null) {
            int order = 1;
            for (CreateEssayQuestionRequest essayReq : request.getEssayQuestions()) {
                String essayId = "essay-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
                ExamQuestionEssay essay = ExamQuestionEssay.builder()
                        .id(essayId)
                        .room(savedRoom)
                        .orderIndex(essayReq.getOrder() > 0 ? essayReq.getOrder() : order)
                        .title(essayReq.getTitle() != null ? essayReq.getTitle() : "PHẦN I: TỰ LUẬN (30 điểm)")
                        .context(essayReq.getContext())
                        .prompt(essayReq.getPrompt() != null ? essayReq.getPrompt() : "")
                        .maxScore(essayReq.getMaxScore() != null ? essayReq.getMaxScore() : BigDecimal.valueOf(30.0))
                        .rubrics(essayReq.getRubric() != null ? essayReq.getRubric() : new ArrayList<>())
                        .build();

                essaysToSave.add(essay);

                essayDetails.add(ExamRoomResponse.EssayQuestionDetail.builder()
                        .id(essayId)
                        .order(essay.getOrderIndex())
                        .title(essay.getTitle())
                        .context(essay.getContext())
                        .prompt(essay.getPrompt())
                        .maxScore(essay.getMaxScore())
                        .rubric(essay.getRubrics())
                        .build());
                order++;
            }
        }

        if (!essaysToSave.isEmpty()) {
            examQuestionEssayRepository.saveAll(essaysToSave);
        }

        return ExamRoomResponse.builder()
                .id(savedRoom.getId())
                .code(savedRoom.getCode())
                .title(savedRoom.getTitle())
                .description(savedRoom.getDescription())
                .durationMinutes(savedRoom.getDurationMinutes())
                .totalAttempts(savedRoom.getTotalAttempts())
                .status(savedRoom.getStatus().name())
                .partsSummary(ExamRoomSummaryResponse.ExamRoomPartsSummary.builder()
                        .mcCount(mcDetails.size())
                        .essayCount(essayDetails.size())
                        .build())
                .createdAt(savedRoom.getCreatedAt())
                .multipleChoiceQuestions(mcDetails)
                .essayQuestions(essayDetails)
                .build();
    }

    @Override
    @Transactional
    public ExamRoomResponse updateRoom(String id, CreateExamRoomRequest request) {
        String lookupCode = (id != null) ? id.toUpperCase() : "";
        ExamRoom room = examRoomRepository.findById(id != null ? id : "")
                .or(() -> examRoomRepository.findByCode(lookupCode))
                .orElseThrow(() -> new ResourceNotFoundException(ExamConstant.MSG_ROOM_NOT_FOUND + id));

        String code = (request != null && request.getCode() != null) ? request.getCode().trim().toUpperCase() : room.getCode();
        if (!room.getCode().equalsIgnoreCase(code) && examRoomRepository.existsByCode(code)) {
            throw new BadRequestException(String.format(ExamConstant.MSG_ROOM_CODE_EXISTS, code));
        }

        room.setCode(code);
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            room.setTitle(request.getTitle().trim());
        }
        if (request.getDescription() != null) {
            room.setDescription(request.getDescription().trim());
        }
        if (request.getDurationMinutes() > 0) {
            room.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getStatus() != null) {
            room.setStatus(request.getStatus());
        }

        ExamRoom savedRoom = examRoomRepository.save(room);

        // Cập nhật danh sách câu hỏi trắc nghiệm nếu được gửi lên
        List<ExamRoomResponse.McQuestionDetail> mcDetails = new ArrayList<>();
        if (request.getMultipleChoiceQuestions() != null) {
            // Xóa các câu hỏi trắc nghiệm cũ của phòng thi
            examQuestionMcRepository.deleteByRoomId(savedRoom.getId());

            List<ExamQuestionMc> mcsToSave = new ArrayList<>();
            List<ExamQuestionMcOption> optsToSave = new ArrayList<>();
            int order = 1;
            for (CreateMcQuestionRequest mcReq : request.getMultipleChoiceQuestions()) {
                String mcId = "mc-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
                String rawAns = mcReq.getCorrectAnswer() != null ? mcReq.getCorrectAnswer().trim() : "";
                if (rawAns.length() == 1 && rawAns.matches("(?i)[A-D]")) {
                    rawAns = rawAns.toUpperCase();
                }

                ExamQuestionMc mc = ExamQuestionMc.builder()
                        .id(mcId)
                        .room(savedRoom)
                        .orderIndex(mcReq.getOrder() > 0 ? mcReq.getOrder() : order)
                        .questionText(mcReq.getQuestion())
                        .context(mcReq.getContext())
                        .correctAnswer(rawAns)
                        .explanation(mcReq.getExplanation())
                        .legalReference(mcReq.getLegalReference())
                        .build();

                mcsToSave.add(mc);

                List<ExamRoomResponse.McOptionDetail> optDetails = new ArrayList<>();
                if (mcReq.getOptions() != null) {
                    for (CreateMcOptionRequest optReq : mcReq.getOptions()) {
                        String optId = "opt-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
                        ExamQuestionMcOption opt = ExamQuestionMcOption.builder()
                                .id(optId)
                                .question(mc)
                                .label(optReq.getLabel() != null ? optReq.getLabel().toUpperCase() : "A")
                                .optionText(optReq.getText())
                                .build();
                        optsToSave.add(opt);

                        optDetails.add(ExamRoomResponse.McOptionDetail.builder()
                                .id(optId)
                                .label(opt.getLabel())
                                .text(opt.getOptionText())
                                .build());
                    }
                }

                mcDetails.add(ExamRoomResponse.McQuestionDetail.builder()
                        .id(mcId)
                        .order(mc.getOrderIndex())
                        .question(mc.getQuestionText())
                        .context(mc.getContext())
                        .options(optDetails)
                        .correctAnswer(mc.getCorrectAnswer())
                        .explanation(mc.getExplanation())
                        .legalReference(mc.getLegalReference())
                        .build());
                order++;
            }
            if (!mcsToSave.isEmpty()) {
                examQuestionMcRepository.saveAll(mcsToSave);
            }
            if (!optsToSave.isEmpty()) {
                examQuestionMcOptionRepository.saveAll(optsToSave);
            }
        } else {
            List<ExamQuestionMc> existingMcs = examQuestionMcRepository.findByRoomIdOrderByOrderIndexAsc(savedRoom.getId());
            mcDetails = existingMcs.stream().map(mc -> {
                List<ExamRoomResponse.McOptionDetail> optDetails = mc.getOptions().stream().map(opt ->
                        ExamRoomResponse.McOptionDetail.builder()
                                .id(opt.getId())
                                .label(opt.getLabel())
                                .text(opt.getOptionText())
                                .build()
                ).toList();

                return ExamRoomResponse.McQuestionDetail.builder()
                        .id(mc.getId())
                        .order(mc.getOrderIndex())
                        .question(mc.getQuestionText())
                        .context(mc.getContext())
                        .options(optDetails)
                        .correctAnswer(mc.getCorrectAnswer())
                        .explanation(mc.getExplanation())
                        .legalReference(mc.getLegalReference())
                        .build();
            }).toList();
        }

        // Cập nhật danh sách câu hỏi tự luận nếu được gửi lên
        List<ExamRoomResponse.EssayQuestionDetail> essayDetails = new ArrayList<>();
        if (request.getEssayQuestions() != null) {
            // Xóa các câu hỏi tự luận cũ của phòng thi
            examQuestionEssayRepository.deleteByRoomId(savedRoom.getId());

            List<ExamQuestionEssay> essaysToSave = new ArrayList<>();
            int order = 1;
            for (CreateEssayQuestionRequest essayReq : request.getEssayQuestions()) {
                String essayId = "essay-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
                ExamQuestionEssay essay = ExamQuestionEssay.builder()
                        .id(essayId)
                        .room(savedRoom)
                        .orderIndex(essayReq.getOrder() > 0 ? essayReq.getOrder() : order)
                        .title(essayReq.getTitle() != null ? essayReq.getTitle() : "PHẦN I: TỰ LUẬN (30 điểm)")
                        .context(essayReq.getContext())
                        .prompt(essayReq.getPrompt() != null ? essayReq.getPrompt() : "")
                        .maxScore(essayReq.getMaxScore() != null ? essayReq.getMaxScore() : BigDecimal.valueOf(30.0))
                        .rubrics(essayReq.getRubric() != null ? essayReq.getRubric() : new ArrayList<>())
                        .build();

                essaysToSave.add(essay);

                essayDetails.add(ExamRoomResponse.EssayQuestionDetail.builder()
                        .id(essayId)
                        .order(essay.getOrderIndex())
                        .title(essay.getTitle())
                        .context(essay.getContext())
                        .prompt(essay.getPrompt())
                        .maxScore(essay.getMaxScore())
                        .rubric(essay.getRubrics())
                        .build());
                order++;
            }
            if (!essaysToSave.isEmpty()) {
                examQuestionEssayRepository.saveAll(essaysToSave);
            }
        } else {
            List<ExamQuestionEssay> existingEssays = examQuestionEssayRepository.findByRoomIdOrderByOrderIndexAsc(savedRoom.getId());
            essayDetails = existingEssays.stream().map(essay ->
                    ExamRoomResponse.EssayQuestionDetail.builder()
                            .id(essay.getId())
                            .order(essay.getOrderIndex())
                            .title(essay.getTitle())
                            .context(essay.getContext())
                            .prompt(essay.getPrompt())
                            .maxScore(essay.getMaxScore())
                            .rubric(essay.getRubrics())
                            .build()
            ).toList();
        }

        ExamRoomResponse response = ExamRoomResponse.builder()
                .id(savedRoom.getId())
                .code(savedRoom.getCode())
                .title(savedRoom.getTitle())
                .description(savedRoom.getDescription())
                .durationMinutes(savedRoom.getDurationMinutes())
                .totalAttempts(savedRoom.getTotalAttempts())
                .status(savedRoom.getStatus().name())
                .partsSummary(ExamRoomSummaryResponse.ExamRoomPartsSummary.builder()
                        .mcCount(mcDetails.size())
                        .essayCount(essayDetails.size())
                        .build())
                .createdAt(savedRoom.getCreatedAt())
                .multipleChoiceQuestions(mcDetails)
                .essayQuestions(essayDetails)
                .build();

        // Xóa cache cũ để đồng bộ dữ liệu mới nhất
        redisCacheService.delete(EXAM_ROOM_CACHE_PREFIX + savedRoom.getId().toLowerCase());
        if (savedRoom.getCode() != null) {
            redisCacheService.delete(EXAM_ROOM_CACHE_PREFIX + savedRoom.getCode().toLowerCase());
        }
        redisCacheService.delete(EXAM_TAKING_CACHE_PREFIX + savedRoom.getId());

        return response;
    }

    @Override
    @Transactional
    public void deleteRoom(String id) {
        String lookupCode = (id != null) ? id.toUpperCase() : "";
        ExamRoom room = examRoomRepository.findById(id != null ? id : "")
                .or(() -> examRoomRepository.findByCode(lookupCode))
                .orElseThrow(() -> new ResourceNotFoundException(ExamConstant.MSG_ROOM_NOT_FOUND + id));

        String roomId = room.getId();

        // 1. Xóa các bài nộp
        examSubmissionRepository.deleteByRoomId(roomId);

        // 2. Xóa thông tin xác minh thí sinh
        candidateVerificationRepository.deleteByRoomId(roomId);

        // 3. Xóa câu hỏi trắc nghiệm & tự luận
        examQuestionMcRepository.deleteByRoomId(roomId);
        examQuestionEssayRepository.deleteByRoomId(roomId);

        // 4. Xóa phòng thi
        examRoomRepository.delete(room);

        // Xóa cache phòng thi và đề thi thí sinh
        redisCacheService.delete(EXAM_ROOM_CACHE_PREFIX + roomId.toLowerCase());
        if (room.getCode() != null) {
            redisCacheService.delete(EXAM_ROOM_CACHE_PREFIX + room.getCode().toLowerCase());
        }
        redisCacheService.delete(EXAM_TAKING_CACHE_PREFIX + roomId);

        log.info("Đã xóa hoàn toàn phòng thi '{}' (Mã: {})", room.getTitle(), room.getCode());
    }

    @Override
    @Transactional(readOnly = true)
    public ExamRoomListResponse getRooms(String search, ExamRoomStatus status, int page, int limit) {
        int pageIndex = Math.max(page - 1, 0);
        int pageSize = limit > 0 ? limit : 20;
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        String trimmedSearch = (search != null && !search.isBlank()) ? search.trim() : null;

        Page<ExamRoom> roomPage = examRoomRepository.searchRooms(trimmedSearch, status, pageable);

        List<ExamRoom> rooms = roomPage.getContent();
        List<String> roomIds = rooms.stream().map(ExamRoom::getId).toList();
        Map<String, Integer> mcCountMap = new HashMap<>();
        Map<String, Integer> essayCountMap = new HashMap<>();

        if (!roomIds.isEmpty()) {
            examQuestionMcRepository.countByRoomIdsGrouped(roomIds)
                    .forEach(row -> mcCountMap.put((String) row[0], ((Number) row[1]).intValue()));
            examQuestionEssayRepository.countByRoomIdsGrouped(roomIds)
                    .forEach(row -> essayCountMap.put((String) row[0], ((Number) row[1]).intValue()));
        }

        List<ExamRoomSummaryResponse> items = rooms.stream().map(room -> {
            int mcCount = mcCountMap.getOrDefault(room.getId(), 0);
            int essayCount = essayCountMap.getOrDefault(room.getId(), 0);

            return ExamRoomSummaryResponse.builder()
                    .id(room.getId())
                    .code(room.getCode())
                    .title(room.getTitle())
                    .description(room.getDescription())
                    .durationMinutes(room.getDurationMinutes())
                    .totalAttempts(room.getTotalAttempts())
                    .status(room.getStatus().name())
                    .partsSummary(ExamRoomSummaryResponse.ExamRoomPartsSummary.builder()
                            .mcCount(mcCount)
                            .essayCount(essayCount)
                            .build())
                    .createdAt(room.getCreatedAt())
                    .build();
        }).toList();

        ExamRoomListResponse.PaginationDto pagination = ExamRoomListResponse.PaginationDto.builder()
                .total(roomPage.getTotalElements())
                .page(page)
                .limit(pageSize)
                .totalPages(roomPage.getTotalPages())
                .build();

        return ExamRoomListResponse.builder()
                .items(items)
                .pagination(pagination)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ExamRoomResponse getRoomDetail(String id) {
        String cacheKey = EXAM_ROOM_CACHE_PREFIX + (id != null ? id.trim().toLowerCase() : "");
        ExamRoomResponse cached = redisCacheService.get(cacheKey, ExamRoomResponse.class);
        if (cached != null) {
            return cached;
        }

        String lookupCode = (id != null) ? id.toUpperCase() : "";
        ExamRoom room = examRoomRepository.findById(id != null ? id : "")
                .or(() -> examRoomRepository.findByCode(lookupCode))
                .orElseThrow(() -> new ResourceNotFoundException(ExamConstant.MSG_ROOM_NOT_FOUND + id));

        List<ExamQuestionMc> mcQuestions = examQuestionMcRepository.findByRoomIdOrderByOrderIndexAsc(room.getId());
        List<ExamQuestionEssay> essayQuestions = examQuestionEssayRepository.findByRoomIdOrderByOrderIndexAsc(room.getId());

        List<ExamRoomResponse.McQuestionDetail> mcDetails = mcQuestions.stream().map(mc -> {
            List<ExamQuestionMcOption> options = mc.getOptions() != null ? mc.getOptions() : java.util.Collections.emptyList();
            List<ExamRoomResponse.McOptionDetail> optDetails = options.stream().map(opt ->
                    ExamRoomResponse.McOptionDetail.builder()
                            .id(opt.getId())
                            .label(opt.getLabel())
                            .text(opt.getOptionText())
                            .build()
            ).toList();

            return ExamRoomResponse.McQuestionDetail.builder()
                    .id(mc.getId())
                    .order(mc.getOrderIndex())
                    .question(mc.getQuestionText())
                    .context(mc.getContext())
                    .options(optDetails)
                    .correctAnswer(mc.getCorrectAnswer())
                    .explanation(mc.getExplanation())
                    .legalReference(mc.getLegalReference())
                    .build();
        }).toList();

        List<ExamRoomResponse.EssayQuestionDetail> essayDetails = essayQuestions.stream().map(essay ->
                ExamRoomResponse.EssayQuestionDetail.builder()
                        .id(essay.getId())
                        .order(essay.getOrderIndex())
                        .title(essay.getTitle())
                        .context(essay.getContext())
                        .prompt(essay.getPrompt())
                        .maxScore(essay.getMaxScore())
                        .rubric(essay.getRubrics())
                        .build()
        ).toList();

        ExamRoomResponse response = ExamRoomResponse.builder()
                .id(room.getId())
                .code(room.getCode())
                .title(room.getTitle())
                .description(room.getDescription())
                .durationMinutes(room.getDurationMinutes())
                .totalAttempts(room.getTotalAttempts())
                .status(room.getStatus().name())
                .partsSummary(ExamRoomSummaryResponse.ExamRoomPartsSummary.builder()
                        .mcCount(mcDetails.size())
                        .essayCount(essayDetails.size())
                        .build())
                .createdAt(room.getCreatedAt())
                .multipleChoiceQuestions(mcDetails)
                .essayQuestions(essayDetails)
                .build();

        redisCacheService.set(cacheKey, response, EXAM_ROOM_TTL);
        if (room.getCode() != null && !room.getCode().equalsIgnoreCase(id)) {
            redisCacheService.set(EXAM_ROOM_CACHE_PREFIX + room.getCode().toLowerCase(), response, EXAM_ROOM_TTL);
        }

        return response;
    }
}
