package jurisprudence_hub_be.module.exam.service.impl;

import jurisprudence_hub_be.common.exception.BadRequestException;
import jurisprudence_hub_be.common.exception.ResourceNotFoundException;
import jurisprudence_hub_be.module.exam.constant.ExamConstant;
import jurisprudence_hub_be.module.exam.dto.request.GradeEssayRequest;
import jurisprudence_hub_be.module.exam.dto.request.SubmitExamRequest;
import jurisprudence_hub_be.module.exam.dto.response.CandidateDto;
import jurisprudence_hub_be.module.exam.dto.response.ExamSubmissionReceiptResponse;
import jurisprudence_hub_be.module.exam.dto.response.SubmissionDetailResponse;
import jurisprudence_hub_be.module.exam.entity.CandidateVerification;
import jurisprudence_hub_be.module.exam.entity.ExamQuestionEssay;
import jurisprudence_hub_be.module.exam.entity.ExamQuestionMc;
import jurisprudence_hub_be.module.exam.entity.ExamRoom;
import jurisprudence_hub_be.module.exam.entity.ExamSubmission;
import jurisprudence_hub_be.module.exam.entity.SubmissionAnswer;
import jurisprudence_hub_be.module.exam.enums.QuestionType;
import jurisprudence_hub_be.module.exam.enums.SubmissionStatus;
import jurisprudence_hub_be.module.exam.repository.CandidateVerificationRepository;
import jurisprudence_hub_be.module.exam.repository.ExamQuestionEssayRepository;
import jurisprudence_hub_be.module.exam.repository.ExamQuestionMcRepository;
import jurisprudence_hub_be.module.exam.repository.ExamRoomRepository;
import jurisprudence_hub_be.module.exam.repository.ExamSubmissionRepository;
import jurisprudence_hub_be.module.exam.repository.SubmissionAnswerRepository;
import jurisprudence_hub_be.module.exam.service.CandidateService;
import jurisprudence_hub_be.module.exam.service.ExamSubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExamSubmissionServiceImpl implements ExamSubmissionService {

    private static final Logger log = LoggerFactory.getLogger(ExamSubmissionServiceImpl.class);

    private final ExamRoomRepository examRoomRepository;
    private final ExamQuestionMcRepository examQuestionMcRepository;
    private final ExamQuestionEssayRepository examQuestionEssayRepository;
    private final ExamSubmissionRepository examSubmissionRepository;
    private final SubmissionAnswerRepository submissionAnswerRepository;
    private final CandidateService candidateService;

    @Value("${exam.hmac.secret:${security.jwt.secret:cand_exam_tamper_evident_hmac_secret_key_2026_jurisprudence}}")
    private String hmacSecret;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public ExamSubmissionReceiptResponse submitExam(String roomId, String sessionToken, SubmitExamRequest request) {
        // 1. Xác thực sessionToken
        CandidateVerification candidate = candidateService.getValidSession(sessionToken);

        String lookupRoomId = roomId != null ? roomId.toUpperCase() : "";
        ExamRoom room = examRoomRepository.findById(roomId != null ? roomId : "")
                .or(() -> examRoomRepository.findByCode(lookupRoomId))
                .orElseThrow(() -> new ResourceNotFoundException(ExamConstant.MSG_ROOM_NOT_FOUND_SIMPLE + roomId));

        if (candidate.getRoom() == null || !room.getId().equals(candidate.getRoom().getId())) {
            throw new BadRequestException(ExamConstant.MSG_CANDIDATE_NOT_IN_ROOM);
        }

        // 2. Chống nộp bài 2 lần
        if (examSubmissionRepository.existsByRoomIdAndCandidateVerificationId(room.getId(), candidate.getId())) {
            throw new BadRequestException(ExamConstant.MSG_EXAM_ALREADY_SUBMITTED);
        }

        // 3. Tự động chấm Trắc nghiệm (Thang 70 điểm / Chuẩn 60 câu)
        List<ExamQuestionMc> mcQuestions = examQuestionMcRepository.findByRoomIdOrderByOrderIndexAsc(room.getId());
        int mcTotalCount = mcQuestions.size();
        int mcCorrectCount = 0;
        int mcAnsweredCount = 0;

        Map<String, String> userMcAnswers = (request != null && request.getAnswers() != null && request.getAnswers().getMultipleChoice() != null)
                ? request.getAnswers().getMultipleChoice()
                : Map.of();

        List<SubmissionAnswer> submissionAnswers = new ArrayList<>();

        for (ExamQuestionMc mc : mcQuestions) {
            String chosen = userMcAnswers.get(mc.getId());
            boolean isAnswered = chosen != null && !chosen.isBlank();

            // So khớp đáp án chính xác (hỗ trợ cả trắc nghiệm A/B/C/D và câu trả lời ngắn chuẩn hóa khoảng trắng)
            boolean isCorrect = false;
            if (isAnswered && mc.getCorrectAnswer() != null && !mc.getCorrectAnswer().isBlank()) {
                String cleanChosen = chosen.trim().replaceAll("\\s+", " ");
                String cleanCorrect = mc.getCorrectAnswer().trim().replaceAll("\\s+", " ");
                isCorrect = cleanChosen.equalsIgnoreCase(cleanCorrect);
            }

            if (isAnswered) {
                mcAnsweredCount++;
            }
            if (isCorrect) {
                mcCorrectCount++;
            }

            SubmissionAnswer ans = SubmissionAnswer.builder()
                    .id("ans-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10))
                    .questionType(QuestionType.MC)
                    .questionId(mc.getId())
                    .chosenOption(isAnswered ? chosen.trim() : null)
                    .isCorrect(isCorrect)
                    .build();
            submissionAnswers.add(ans);
        }

        // Logic tính điểm Trắc Nghiệm: Thang 70.0 điểm (Chuẩn 60 câu: mỗi câu = 70/60 = ~1.167 điểm)
        // Công thức: mcScore = round((mcCorrectCount / mcTotalCount) * 70.0, 1)
        BigDecimal mcScore = BigDecimal.ZERO;
        if (mcTotalCount > 0) {
            double calculatedScore = ((double) mcCorrectCount / mcTotalCount) * 70.0;
            mcScore = BigDecimal.valueOf(calculatedScore).setScale(1, RoundingMode.HALF_UP);
        }

        // 4. Lưu câu trả lời Tự luận
        List<ExamQuestionEssay> essayQuestions = examQuestionEssayRepository.findByRoomIdOrderByOrderIndexAsc(room.getId());
        int essayTotalCount = essayQuestions.size();
        int essayAnsweredCount = 0;

        Map<String, String> userEssayAnswers = (request != null && request.getAnswers() != null && request.getAnswers().getEssay() != null)
                ? request.getAnswers().getEssay()
                : Map.of();

        for (ExamQuestionEssay essay : essayQuestions) {
            String content = userEssayAnswers.get(essay.getId());
            boolean isAnswered = content != null && !content.trim().isBlank();
            if (isAnswered) {
                essayAnsweredCount++;
            }

            SubmissionAnswer ans = SubmissionAnswer.builder()
                    .id("ans-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10))
                    .questionType(QuestionType.ESSAY)
                    .questionId(essay.getId())
                    .essayContent(content)
                    .essayScore(null) // Chờ giám khảo chấm
                    .build();
            submissionAnswers.add(ans);
        }

        // 5. Sinh mã biên bản REC-2026-XXXXXX
        byte[] receiptCodeBytes = new byte[3];
        secureRandom.nextBytes(receiptCodeBytes);
        String receiptCode = HexFormat.of().formatHex(receiptCodeBytes).toUpperCase();
        String receiptId = "REC-2026-" + receiptCode;

        Instant submittedAt = Instant.now();

        // 6. Tính mã băm HMAC-SHA256 toàn vẹn chống gian lận
        String dataToHash = receiptId + "|" + candidate.getCandidateId() + "|" + room.getId() + "|" + mcScore + "|" + submittedAt;
        String sha256Digest = "sha256:" + computeHmacSha256(dataToHash, hmacSecret);

        // 7. Lưu vào exam_submissions
        String submissionId = "sub-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        ExamSubmission submission = ExamSubmission.builder()
                .id(submissionId)
                .receiptId(receiptId)
                .room(room)
                .candidateVerification(candidate)
                .timeSpentSeconds(request.getTimeSpentSeconds())
                .mcAnsweredCount(mcAnsweredCount)
                .mcTotalCount(mcTotalCount)
                .mcCorrectCount(mcCorrectCount)
                .mcScore(mcScore)
                .essayAnsweredCount(essayAnsweredCount)
                .essayTotalCount(essayTotalCount)
                .essayScore(null)
                .totalScore(null)
                .status(SubmissionStatus.PENDING_ESSAY_GRADING)
                .sha256Digest(sha256Digest)
                .submittedAt(submittedAt)
                .build();

        ExamSubmission savedSubmission = examSubmissionRepository.save(submission);

        // Gắn submission vào từng answers và lưu theo batch
        for (SubmissionAnswer ans : submissionAnswers) {
            ans.setSubmission(savedSubmission);
        }
        if (!submissionAnswers.isEmpty()) {
            submissionAnswerRepository.saveAll(submissionAnswers);
        }

        // Tăng số lượt thi của phòng
        room.setTotalAttempts(room.getTotalAttempts() + 1);
        examRoomRepository.save(room);

        CandidateDto candidateDto = CandidateDto.builder()
                .cccd(candidate.getCccd())
                .fullName(candidate.getFullName())
                .phone(candidate.getPhone())
                .email(candidate.getEmail())
                .address(candidate.getAddress())
                .candidateId(candidate.getCandidateId())
                .verifiedAt(candidate.getVerifiedAt())
                .isVerified(true)
                .build();

        return ExamSubmissionReceiptResponse.builder()
                .receiptId(savedSubmission.getReceiptId())
                .roomId(room.getId())
                .roomCode(room.getCode())
                .roomTitle(room.getTitle())
                .candidate(candidateDto)
                .submittedAt(savedSubmission.getSubmittedAt())
                .timeSpentSeconds(savedSubmission.getTimeSpentSeconds())
                .mcAnsweredCount(savedSubmission.getMcAnsweredCount())
                .mcTotalCount(savedSubmission.getMcTotalCount())
                .mcCorrectCount(savedSubmission.getMcCorrectCount())
                .mcScore(savedSubmission.getMcScore())
                .essayAnsweredCount(savedSubmission.getEssayAnsweredCount())
                .essayTotalCount(savedSubmission.getEssayTotalCount())
                .sha256Digest(savedSubmission.getSha256Digest())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SubmissionDetailResponse getReceipt(String receiptId) {
        String lookupId = receiptId != null ? receiptId : "";
        ExamSubmission submission = examSubmissionRepository.findByReceiptId(lookupId)
                .or(() -> examSubmissionRepository.findById(lookupId))
                .orElseThrow(() -> new ResourceNotFoundException(ExamConstant.MSG_RECEIPT_NOT_FOUND + receiptId));

        return mapToDetailResponse(submission);
    }

    @Override
    @Transactional
    public SubmissionDetailResponse gradeEssay(String submissionId, GradeEssayRequest request) {
        String lookupId = submissionId != null ? submissionId : "";
        ExamSubmission submission = examSubmissionRepository.findById(lookupId)
                .or(() -> examSubmissionRepository.findByReceiptId(lookupId))
                .orElseThrow(() -> new ResourceNotFoundException(ExamConstant.MSG_SUBMISSION_NOT_FOUND + submissionId));

        List<SubmissionAnswer> answers = submissionAnswerRepository.findBySubmissionId(submission.getId());

        BigDecimal totalEssayScore = BigDecimal.ZERO;
        StringBuilder feedbackBuilder = new StringBuilder();

        List<SubmissionAnswer> updatedAnswers = new ArrayList<>();
        if (request != null && request.getEssayScores() != null) {
            for (GradeEssayRequest.EssayScoreItem item : request.getEssayScores()) {
                if (item == null || item.getQuestionId() == null) continue;
                for (SubmissionAnswer ans : answers) {
                    if (ans != null && ans.getQuestionType() == QuestionType.ESSAY && item.getQuestionId().equals(ans.getQuestionId())) {
                        BigDecimal score = item.getScore() != null ? item.getScore() : BigDecimal.ZERO;
                        ans.setEssayScore(score);
                        updatedAnswers.add(ans);
                        totalEssayScore = totalEssayScore.add(score);

                        if (item.getComment() != null && !item.getComment().isBlank()) {
                            if (!feedbackBuilder.isEmpty()) feedbackBuilder.append("\n");
                            feedbackBuilder.append("[").append(item.getQuestionId()).append("]: ").append(item.getComment());
                        }
                    }
                }
            }
        }
        if (!updatedAnswers.isEmpty()) {
            submissionAnswerRepository.saveAll(updatedAnswers);
        }

        // Giới hạn điểm tự luận tối đa 30.0
        if (totalEssayScore.compareTo(BigDecimal.valueOf(30.0)) > 0) {
            totalEssayScore = BigDecimal.valueOf(30.0);
        }

        submission.setEssayScore(totalEssayScore);
        submission.setEssayFeedback(feedbackBuilder.toString());

        // totalScore = mcScore + essayScore (thang 100)
        BigDecimal currentMcScore = submission.getMcScore() != null ? submission.getMcScore() : BigDecimal.ZERO;
        BigDecimal totalScore = currentMcScore.add(totalEssayScore);
        submission.setTotalScore(totalScore);
        submission.setStatus(SubmissionStatus.GRADED);

        ExamSubmission updated = examSubmissionRepository.save(submission);
        log.info("Đã chấm điểm tự luận thành công cho bài thi {} (Tổng điểm: {})", updated.getReceiptId(), totalScore);

        return mapToDetailResponse(updated);
    }

    private SubmissionDetailResponse mapToDetailResponse(ExamSubmission submission) {
        CandidateVerification candidate = submission.getCandidateVerification();
        ExamRoom room = submission.getRoom();
        List<SubmissionAnswer> answers = submissionAnswerRepository.findBySubmissionId(submission.getId());

        CandidateDto candidateDto = CandidateDto.builder()
                .cccd(candidate.getCccd())
                .fullName(candidate.getFullName())
                .phone(candidate.getPhone())
                .email(candidate.getEmail())
                .address(candidate.getAddress())
                .candidateId(candidate.getCandidateId())
                .verifiedAt(candidate.getVerifiedAt())
                .isVerified(true)
                .build();

        List<SubmissionDetailResponse.SubmissionAnswerDetailDto> answerDtos = answers.stream().map(ans ->
                SubmissionDetailResponse.SubmissionAnswerDetailDto.builder()
                        .id(ans.getId())
                        .questionType(ans.getQuestionType().name())
                        .questionId(ans.getQuestionId())
                        .chosenOption(ans.getChosenOption())
                        .isCorrect(ans.getIsCorrect())
                        .essayContent(ans.getEssayContent())
                        .essayScore(ans.getEssayScore())
                        .build()
        ).toList();

        return SubmissionDetailResponse.builder()
                .id(submission.getId())
                .receiptId(submission.getReceiptId())
                .roomId(room.getId())
                .roomCode(room.getCode())
                .roomTitle(room.getTitle())
                .candidate(candidateDto)
                .submittedAt(submission.getSubmittedAt())
                .timeSpentSeconds(submission.getTimeSpentSeconds())
                .mcAnsweredCount(submission.getMcAnsweredCount())
                .mcTotalCount(submission.getMcTotalCount())
                .mcCorrectCount(submission.getMcCorrectCount())
                .mcScore(submission.getMcScore())
                .essayAnsweredCount(submission.getEssayAnsweredCount())
                .essayTotalCount(submission.getEssayTotalCount())
                .essayScore(submission.getEssayScore())
                .totalScore(submission.getTotalScore())
                .essayFeedback(submission.getEssayFeedback())
                .status(submission.getStatus().name())
                .sha256Digest(submission.getSha256Digest())
                .answers(answerDtos)
                .build();
    }

    private String computeHmacSha256(String data, String secret) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] signedBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(signedBytes);
        } catch (Exception e) {
            log.error("Lỗi khi tính toán mã băm HMAC-SHA256: {}", e.getMessage(), e);
            return UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
        }
    }
}
