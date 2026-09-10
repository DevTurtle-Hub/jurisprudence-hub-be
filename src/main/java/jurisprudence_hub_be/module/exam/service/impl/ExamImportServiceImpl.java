package jurisprudence_hub_be.module.exam.service.impl;

import jurisprudence_hub_be.common.exception.BadRequestException;
import jurisprudence_hub_be.common.exception.ResourceNotFoundException;
import jurisprudence_hub_be.module.exam.constant.ExamConstant;
import jurisprudence_hub_be.module.exam.dto.draft.AddDraftQuestionRequest;
import jurisprudence_hub_be.module.exam.dto.draft.ConfirmImportResponse;
import jurisprudence_hub_be.module.exam.dto.draft.DraftExamDto;
import jurisprudence_hub_be.module.exam.dto.draft.DraftOptionDto;
import jurisprudence_hub_be.module.exam.dto.draft.DraftQuestionDto;
import jurisprudence_hub_be.module.exam.dto.draft.DraftValidationResponse;
import jurisprudence_hub_be.module.exam.dto.draft.ExamImportPreviewResponse;
import jurisprudence_hub_be.module.exam.dto.draft.UpdateDraftQuestionRequest;
import jurisprudence_hub_be.module.exam.entity.ExamImportDraft;
import jurisprudence_hub_be.module.exam.entity.ExamQuestionEssay;
import jurisprudence_hub_be.module.exam.entity.ExamQuestionMc;
import jurisprudence_hub_be.module.exam.entity.ExamQuestionMcOption;
import jurisprudence_hub_be.module.exam.entity.ExamRoom;
import jurisprudence_hub_be.module.exam.enums.DraftQuestionType;
import jurisprudence_hub_be.module.exam.enums.DraftStatus;
import jurisprudence_hub_be.module.exam.enums.ExamRoomStatus;
import jurisprudence_hub_be.module.exam.enums.ImportMode;
import jurisprudence_hub_be.module.exam.enums.ParsingStatus;
import jurisprudence_hub_be.module.exam.parser.ExamPdfParser;
import jurisprudence_hub_be.module.exam.parser.PdfTextExtractor;
import jurisprudence_hub_be.module.exam.repository.ExamImportDraftRepository;
import jurisprudence_hub_be.module.exam.repository.ExamQuestionEssayRepository;
import jurisprudence_hub_be.module.exam.repository.ExamQuestionMcOptionRepository;
import jurisprudence_hub_be.module.exam.repository.ExamQuestionMcRepository;
import jurisprudence_hub_be.module.exam.repository.ExamRoomRepository;
import jurisprudence_hub_be.module.exam.service.ExamImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ExamImportServiceImpl implements ExamImportService {

    private static final Logger log = LoggerFactory.getLogger(ExamImportServiceImpl.class);

    private final PdfTextExtractor pdfTextExtractor;
    private final ExamPdfParser examPdfParser;
    private final ExamImportDraftRepository draftRepository;
    private final ExamRoomRepository examRoomRepository;
    private final ExamQuestionMcRepository examQuestionMcRepository;
    private final ExamQuestionMcOptionRepository examQuestionMcOptionRepository;
    private final ExamQuestionEssayRepository examQuestionEssayRepository;

    public ExamImportServiceImpl(PdfTextExtractor pdfTextExtractor,
                                 ExamPdfParser examPdfParser,
                                 ExamImportDraftRepository draftRepository,
                                 ExamRoomRepository examRoomRepository,
                                 ExamQuestionMcRepository examQuestionMcRepository,
                                 ExamQuestionMcOptionRepository examQuestionMcOptionRepository,
                                 ExamQuestionEssayRepository examQuestionEssayRepository) {
        this.pdfTextExtractor = pdfTextExtractor;
        this.examPdfParser = examPdfParser;
        this.draftRepository = draftRepository;
        this.examRoomRepository = examRoomRepository;
        this.examQuestionMcRepository = examQuestionMcRepository;
        this.examQuestionMcOptionRepository = examQuestionMcOptionRepository;
        this.examQuestionEssayRepository = examQuestionEssayRepository;
    }

    // Cache trong bộ nhớ phục vụ truy cập nhanh
    private final Map<String, DraftExamDto> memoryDraftCache = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public ExamImportPreviewResponse previewImport(MultipartFile file, ImportMode mode) {
        // 1. Trích xuất text từ PDFBox
        String extractedText = pdfTextExtractor.extractText(file);

        // 2. Parse cấu trúc đề thi thành DraftExamDto
        String originalFilename = (file != null && file.getOriginalFilename() != null) ? file.getOriginalFilename() : "Exam.pdf";
        DraftExamDto draft = examPdfParser.parseExamText(extractedText, originalFilename, mode);

        // 3. Lưu bản nháp vào Database (table exam_import_drafts)
        // TUYỆT ĐỐI KHÔNG INSERT VÀO BẢNG CHÍNH THỨC Ở ĐÂY
        ExamImportDraft draftEntity = ExamImportDraft.builder()
                .id(draft.getDraftId())
                .fileName(draft.getFileName())
                .title(draft.getTitle())
                .totalQuestions(draft.getTotalQuestions())
                .status(DraftStatus.DRAFT)
                .draftData(draft)
                .build();

        try {
            draftRepository.save(draftEntity);
        } catch (Exception e) {
            log.warn("Không thể lưu draft vào DB (có thể do kết nối), giữ draft trong memory cache: {}", e.getMessage());
        }
        memoryDraftCache.put(draft.getDraftId(), draft);

        // 4. Thống kê preview
        int successCount = 0;
        int warningCount = 0;
        int errorCount = 0;

        for (DraftQuestionDto q : draft.getQuestions()) {
            if (q.getParsingStatus() == ParsingStatus.SUCCESS) {
                successCount++;
            } else if (q.getParsingStatus() == ParsingStatus.WARNING) {
                warningCount++;
            } else {
                errorCount++;
            }
        }

        return ExamImportPreviewResponse.builder()
                .draftId(draft.getDraftId())
                .fileName(draft.getFileName())
                .title(draft.getTitle())
                .totalQuestions(draft.getTotalQuestions())
                .essayCount(draft.getEssayCount())
                .mcCount(draft.getMcCount())
                .shortAnswerCount(draft.getShortAnswerCount())
                .completedQuestions(draft.getCompletedQuestions())
                .incompleteQuestions(draft.getIncompleteQuestions())
                .successCount(successCount)
                .warningCount(warningCount)
                .errorCount(errorCount)
                .questions(draft.getQuestions())
                .warnings(draft.getWarnings())
                .errors(draft.getErrors())
                .build();
    }

    @Override
    @Transactional
    public ExamImportPreviewResponse createManualDraft(String title) {
        String draftId = UUID.randomUUID().toString();
        String draftTitle = title != null && !title.isBlank() ? title : "Đề thi nhập thủ công";

        DraftExamDto draft = DraftExamDto.builder()
                .draftId(draftId)
                .fileName("MANUAL_INPUT")
                .title(draftTitle)
                .totalQuestions(0)
                .essayCount(0)
                .mcCount(0)
                .shortAnswerCount(0)
                .completedQuestions(0)
                .incompleteQuestions(0)
                .questions(new ArrayList<>())
                .warnings(new ArrayList<>())
                .errors(new ArrayList<>())
                .build();

        // Lưu bản nháp vào Database
        ExamImportDraft draftEntity = ExamImportDraft.builder()
                .id(draftId)
                .fileName("MANUAL_INPUT")
                .title(draftTitle)
                .totalQuestions(0)
                .status(DraftStatus.DRAFT)
                .draftData(draft)
                .build();

        try {
            draftRepository.save(draftEntity);
        } catch (Exception e) {
            log.warn("Không thể lưu draft vào DB (có thể do kết nối), giữ draft trong memory cache: {}", e.getMessage());
        }
        memoryDraftCache.put(draftId, draft);

        return ExamImportPreviewResponse.builder()
                .draftId(draftId)
                .fileName("MANUAL_INPUT")
                .title(draftTitle)
                .totalQuestions(0)
                .essayCount(0)
                .mcCount(0)
                .shortAnswerCount(0)
                .completedQuestions(0)
                .incompleteQuestions(0)
                .successCount(0)
                .warningCount(0)
                .errorCount(0)
                .questions(new ArrayList<>())
                .warnings(new ArrayList<>())
                .errors(new ArrayList<>())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DraftExamDto getDraft(String draftId) {
        DraftExamDto cached = memoryDraftCache.get(draftId);
        if (cached != null) return cached;

        return draftRepository.findByIdAndStatus(draftId, DraftStatus.DRAFT)
                .map(ExamImportDraft::getDraftData)
                .orElseThrow(() -> new ResourceNotFoundException(ExamConstant.MSG_DRAFT_NOT_FOUND + draftId));
    }

    @Override
    @Transactional
    public DraftQuestionDto updateQuestion(String draftId, String questionId, UpdateDraftQuestionRequest request) {
        DraftExamDto draft = getDraft(draftId);

        List<DraftQuestionDto> questions = draft.getQuestions() != null ? draft.getQuestions() : new ArrayList<>();
        DraftQuestionDto targetQuestion = questions.stream()
                .filter(q -> q.getTemporaryId() != null && q.getTemporaryId().equalsIgnoreCase(questionId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ExamConstant.MSG_DRAFT_QUESTION_NOT_FOUND, questionId)));

        if (request != null) {
            if (request.getQuestionNumber() != null) {
                targetQuestion.setQuestionNumber(request.getQuestionNumber());
            }
            if (request.getType() != null) {
                targetQuestion.setType(request.getType());
            }
            if (request.getContent() != null) {
                targetQuestion.setContent(request.getContent().trim());
            }
            if (request.getContext() != null) {
                targetQuestion.setContext(request.getContext().trim());
            }
            if (request.getOptions() != null) {
                targetQuestion.setOptions(request.getOptions());
            }
            if (request.getAnswer() != null) {
                targetQuestion.setAnswer(request.getAnswer().trim());
                targetQuestion.setHasAnswer(!request.getAnswer().trim().isBlank());
            }

            // Cập nhật đáp án đúng của trắc nghiệm nếu admin chỉ định correctOptionKey
            if (request.getCorrectOptionKey() != null && targetQuestion.getType() == DraftQuestionType.MULTIPLE_CHOICE) {
                String key = request.getCorrectOptionKey().trim().toUpperCase();
                if (targetQuestion.getOptions() != null) {
                    for (DraftOptionDto opt : targetQuestion.getOptions()) {
                        if (opt != null && opt.getKey() != null) {
                            opt.setCorrect(opt.getKey().equalsIgnoreCase(key));
                        }
                    }
                }
                targetQuestion.setAnswer(key);
                targetQuestion.setHasAnswer(!key.isBlank());
            }
        }

        // Đánh giá lại trạng thái câu hỏi sau khi admin sửa
        recalculateQuestionStatus(targetQuestion);

        // Cập nhật thống kê hoàn thành
        draft.setCompletedQuestions((int) questions.stream().filter(q -> q.getParsingStatus() == ParsingStatus.SUCCESS).count());
        draft.setIncompleteQuestions(draft.getTotalQuestions() - draft.getCompletedQuestions());

        // Lưu bản nháp cập nhật
        saveDraft(draft);

        return targetQuestion;
    }

    @Override
    @Transactional
    public DraftExamDto deleteQuestion(String draftId, String questionId) {
        DraftExamDto draft = getDraft(draftId);

        boolean removed = draft.getQuestions() != null && draft.getQuestions().removeIf(q -> q.getTemporaryId() != null && q.getTemporaryId().equalsIgnoreCase(questionId));
        if (!removed) {
            throw new ResourceNotFoundException(String.format(ExamConstant.MSG_DRAFT_QUESTION_DELETE_NOT_FOUND, questionId));
        }

        // Cập nhật thống kê
        draft.setTotalQuestions(draft.getQuestions().size());
        draft.setEssayCount((int) draft.getQuestions().stream().filter(q -> q.getType() == DraftQuestionType.ESSAY).count());
        draft.setMcCount((int) draft.getQuestions().stream().filter(q -> q.getType() == DraftQuestionType.MULTIPLE_CHOICE).count());
        draft.setShortAnswerCount((int) draft.getQuestions().stream().filter(q -> q.getType() == DraftQuestionType.SHORT_ANSWER).count());
        draft.setCompletedQuestions((int) draft.getQuestions().stream().filter(q -> q.getParsingStatus() == ParsingStatus.SUCCESS).count());
        draft.setIncompleteQuestions(draft.getTotalQuestions() - draft.getCompletedQuestions());

        saveDraft(draft);
        return draft;
    }

    @Override
    @Transactional
    public DraftQuestionDto addQuestion(String draftId, AddDraftQuestionRequest request) {
        DraftExamDto draft = getDraft(draftId);

        int nextNumber = request.getQuestionNumber() != null
                ? request.getQuestionNumber()
                : draft.getQuestions().size() + 1;

        String temporaryId = "q_manual_" + UUID.randomUUID().toString().substring(0, 6);

        DraftQuestionDto newQuestion = DraftQuestionDto.builder()
                .temporaryId(temporaryId)
                .questionNumber(nextNumber)
                .type(request.getType())
                .section(request.getType() == DraftQuestionType.MULTIPLE_CHOICE ? "TRẮC NGHIỆM" :
                        request.getType() == DraftQuestionType.ESSAY ? "TỰ LUẬN" : "TRẮC NGHIỆM TRẢ LỜI NGẮN")
                .context(request.getContext())
                .content(request.getContent().trim())
                .options(request.getOptions() != null ? request.getOptions() : new ArrayList<>())
                .answer(request.getAnswer())
                .hasAnswer(request.getAnswer() != null && !request.getAnswer().isBlank())
                .build();

        if (request.getCorrectOptionKey() != null && request.getType() == DraftQuestionType.MULTIPLE_CHOICE) {
            String key = request.getCorrectOptionKey().trim().toUpperCase();
            for (DraftOptionDto opt : newQuestion.getOptions()) {
                opt.setCorrect(opt.getKey().equalsIgnoreCase(key));
            }
            newQuestion.setAnswer(key);
            newQuestion.setHasAnswer(!key.isBlank());
        }

        recalculateQuestionStatus(newQuestion);
        draft.getQuestions().add(newQuestion);

        draft.setTotalQuestions(draft.getQuestions().size());
        draft.setEssayCount((int) draft.getQuestions().stream().filter(q -> q.getType() == DraftQuestionType.ESSAY).count());
        draft.setMcCount((int) draft.getQuestions().stream().filter(q -> q.getType() == DraftQuestionType.MULTIPLE_CHOICE).count());
        draft.setShortAnswerCount((int) draft.getQuestions().stream().filter(q -> q.getType() == DraftQuestionType.SHORT_ANSWER).count());
        draft.setCompletedQuestions((int) draft.getQuestions().stream().filter(q -> q.getParsingStatus() == ParsingStatus.SUCCESS).count());
        draft.setIncompleteQuestions(draft.getTotalQuestions() - draft.getCompletedQuestions());

        saveDraft(draft);
        return newQuestion;
    }

    @Override
    @Transactional(readOnly = true)
    public DraftValidationResponse validateDraft(String draftId) {
        DraftExamDto draft = getDraft(draftId);

        List<String> examErrors = new ArrayList<>();
        List<String> examWarnings = new ArrayList<>();
        List<DraftValidationResponse.QuestionValidationDetail> details = new ArrayList<>();

        if (draft.getQuestions().isEmpty()) {
            examErrors.add("Đề thi không có câu hỏi nào.");
        }

        // Thêm thông tin về tình trạng hoàn thành đáp án
        if (draft.getIncompleteQuestions() > 0) {
            examErrors.add(String.format("Còn %d/%d câu hỏi chưa có đáp án đầy đủ. Admin phải hoàn thành tất cả đáp án trước khi xác nhận import.",
                    draft.getIncompleteQuestions(), draft.getTotalQuestions()));
        }

        Set<Integer> seenNumbers = new HashSet<>();
        for (DraftQuestionDto q : draft.getQuestions()) {
            List<String> qErrors = new ArrayList<>();
            List<String> qWarnings = new ArrayList<>();

            // 1. Kiểm tra trùng số thứ tự
            if (!seenNumbers.add(q.getQuestionNumber())) {
                qErrors.add("Số thứ tự câu hỏi '" + q.getQuestionNumber() + "' bị trùng lặp.");
            }

            // 2. Kiểm tra nội dung
            if (q.getContent() == null || q.getContent().isBlank()) {
                qErrors.add("Nội dung câu hỏi bị rỗng.");
            }

            // 3. Kiểm tra trắc nghiệm
            if (q.getType() == DraftQuestionType.MULTIPLE_CHOICE) {
                if (q.getOptions().isEmpty()) {
                    qErrors.add("Câu trắc nghiệm không có phương án lựa chọn.");
                } else {
                    if (q.getOptions().size() < 4) {
                        qWarnings.add("Câu trắc nghiệm chỉ có " + q.getOptions().size() + " phương án (tiêu chuẩn 4 phương án).");
                    }

                    long correctCount = q.getOptions().stream().filter(DraftOptionDto::isCorrect).count();
                    if (correctCount == 0) {
                        qErrors.add("BẮT BUỘC: Chưa chọn đáp án đúng cho câu trắc nghiệm. Admin phải chọn đáp án trước khi xác nhận import.");
                    } else if (correctCount > 1) {
                        qErrors.add("Câu trắc nghiệm có " + correctCount + " đáp án đúng (chỉ được có 1 đáp án đúng duy nhất).");
                    }

                    for (DraftOptionDto opt : q.getOptions()) {
                        if (opt.getContent() == null || opt.getContent().isBlank()) {
                            qErrors.add("Phương án " + opt.getKey() + " có nội dung rỗng.");
                        }
                    }
                }
            } else if (q.getType() == DraftQuestionType.ESSAY) {
                if (q.getAnswer() == null || q.getAnswer().isBlank()) {
                    qErrors.add("BẮT BUỘC: Câu tự luận chưa có đáp án mẫu. Admin phải nhập đáp án mẫu trước khi xác nhận import.");
                }
            } else if (q.getType() == DraftQuestionType.SHORT_ANSWER) {
                if (q.getAnswer() == null || q.getAnswer().isBlank()) {
                    qErrors.add("BẮT BUỘC: Câu trả lời ngắn chưa có đáp án mẫu. Admin phải nhập đáp án mẫu trước khi xác nhận import.");
                }
            }

            if (!qErrors.isEmpty() || !qWarnings.isEmpty()) {
                details.add(DraftValidationResponse.QuestionValidationDetail.builder()
                        .temporaryId(q.getTemporaryId())
                        .questionNumber(q.getQuestionNumber())
                        .errors(qErrors)
                        .warnings(qWarnings)
                        .build());
            }

            examErrors.addAll(qErrors);
            examWarnings.addAll(qWarnings);
        }

        boolean isValid = examErrors.isEmpty();

        return DraftValidationResponse.builder()
                .draftId(draftId)
                .valid(isValid)
                .totalQuestions(draft.getTotalQuestions())
                .completedQuestions(draft.getCompletedQuestions())
                .incompleteQuestions(draft.getIncompleteQuestions())
                .errors(examErrors)
                .warnings(examWarnings)
                .questionDetails(details)
                .build();
    }

    @Override
    @Transactional
    public ConfirmImportResponse confirmImport(String draftId) {
        // 1. Kiểm tra validation nghiêm ngặt trước khi Confirm
        DraftValidationResponse validation = validateDraft(draftId);
        if (!validation.isValid()) {
            throw new BadRequestException(ExamConstant.MSG_DRAFT_HAS_CRITICAL_ERRORS
                    + String.join("; ", validation.getErrors()));
        }

        DraftExamDto draft = getDraft(draftId);

        // 2. Khởi tạo ExamRoom chính thức
        String roomCode = "CAND-IMP-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        while (examRoomRepository.existsByCode(roomCode)) {
            roomCode = "CAND-IMP-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        }

        ExamRoom room = ExamRoom.builder()
                .code(roomCode)
                .title(draft.getTitle() != null ? draft.getTitle() : "Đề thi sát hạch Import từ PDF")
                .description("Đề thi được thẩm định và import từ tệp " + (draft.getFileName() != null ? draft.getFileName() : ""))
                .durationMinutes(60)
                .totalAttempts(0)
                .status(ExamRoomStatus.OPEN)
                .build();

        ExamRoom savedRoom = examRoomRepository.save(room);

        // 3. Lưu danh sách câu hỏi trắc nghiệm, tự luận và câu trả lời ngắn
        int essayCount = 0;
        int mcCount = 0;
        int shortAnswerCount = 0;

        List<ExamQuestionMc> mcsToSave = new ArrayList<>();
        List<ExamQuestionMcOption> optsToSave = new ArrayList<>();
        List<ExamQuestionEssay> essaysToSave = new ArrayList<>();

        List<DraftQuestionDto> questions = draft.getQuestions() != null ? draft.getQuestions() : Collections.emptyList();
        for (DraftQuestionDto q : questions) {
            if (q == null) continue;
            List<DraftOptionDto> options = q.getOptions() != null ? q.getOptions() : Collections.emptyList();
            if (q.getType() == DraftQuestionType.MULTIPLE_CHOICE) {
                // Xác định đáp án đúng
                String correctAnswer = options.stream()
                        .filter(DraftOptionDto::isCorrect)
                        .map(DraftOptionDto::getKey)
                        .findFirst()
                        .orElse("A");

                ExamQuestionMc mc = ExamQuestionMc.builder()
                        .room(savedRoom)
                        .orderIndex(q.getQuestionNumber())
                        .questionText(q.getContent())
                        .context(q.getContext())
                        .correctAnswer(correctAnswer)
                        .explanation(q.getContext())
                        .legalReference("Theo đề thi sát hạch chuẩn CAND")
                        .build();

                mcsToSave.add(mc);

                for (DraftOptionDto opt : options) {
                    if (opt == null) continue;
                    ExamQuestionMcOption mcOpt = ExamQuestionMcOption.builder()
                            .question(mc)
                            .label(opt.getKey() != null ? opt.getKey() : "")
                            .optionText(opt.getContent() != null ? opt.getContent() : "")
                            .build();
                    optsToSave.add(mcOpt);
                }
                mcCount++;
            } else if (q.getType() == DraftQuestionType.ESSAY) {
                // ESSAY ánh xạ vào bảng câu hỏi tự luận
                ExamQuestionEssay essay = ExamQuestionEssay.builder()
                        .room(savedRoom)
                        .orderIndex(q.getQuestionNumber())
                        .title("Câu " + q.getQuestionNumber() + ": Tự luận")
                        .context(q.getContext())
                        .prompt(q.getContent() != null ? q.getContent() : "")
                        .maxScore(BigDecimal.valueOf(10.0))
                        .rubrics(q.getAnswer() != null ? List.of("Đáp án mẫu: " + q.getAnswer()) : List.of())
                        .build();

                essaysToSave.add(essay);
                essayCount++;
            } else {
                // SHORT_ANSWER ánh xạ vào bảng câu hỏi trả lời ngắn
                ExamQuestionEssay essay = ExamQuestionEssay.builder()
                        .room(savedRoom)
                        .orderIndex(q.getQuestionNumber())
                        .title("Câu " + q.getQuestionNumber() + ": Trả lời ngắn")
                        .context(q.getContext())
                        .prompt(q.getContent() != null ? q.getContent() : "")
                        .maxScore(BigDecimal.valueOf(5.0))
                        .rubrics(q.getAnswer() != null ? List.of("Đáp án mẫu: " + q.getAnswer()) : List.of())
                        .build();

                essaysToSave.add(essay);
                shortAnswerCount++;
            }
        }

        if (!mcsToSave.isEmpty()) {
            examQuestionMcRepository.saveAll(mcsToSave);
        }
        if (!optsToSave.isEmpty()) {
            examQuestionMcOptionRepository.saveAll(optsToSave);
        }
        if (!essaysToSave.isEmpty()) {
            examQuestionEssayRepository.saveAll(essaysToSave);
        }

        // 4. Cập nhật trạng thái bản nháp thành CONFIRMED
        draftRepository.findById(draftId).ifPresent(d -> {
            d.setStatus(DraftStatus.CONFIRMED);
            draftRepository.save(d);
        });
        memoryDraftCache.remove(draftId);

        log.info("Xác nhận import thành công đề thi [{}] từ draft [{}] gồm {} câu hỏi",
                savedRoom.getCode(), draftId, draft.getTotalQuestions());

        return ConfirmImportResponse.builder()
                .examId(savedRoom.getId())
                .examCode(savedRoom.getCode())
                .title(savedRoom.getTitle())
                .totalQuestionsImported(draft.getTotalQuestions())
                .essayCount(essayCount)
                .mcCount(mcCount)
                .shortAnswerCount(shortAnswerCount)
                .status(savedRoom.getStatus().name())
                .confirmedAt(Instant.now())
                .build();
    }

    @Override
    @Transactional
    public void cancelDraft(String draftId) {
        draftRepository.findById(draftId).ifPresent(d -> {
            d.setStatus(DraftStatus.CANCELLED);
            draftRepository.save(d);
        });
        memoryDraftCache.remove(draftId);
        log.info("Đã hủy bản nháp đề thi: {}", draftId);
    }

    private void recalculateQuestionStatus(DraftQuestionDto q) {
        List<String> warnings = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        if (q.getContent() == null || q.getContent().isBlank()) {
            errors.add(ExamConstant.VAL_QUESTION_CONTENT_EMPTY);
        }

        List<DraftOptionDto> options = q.getOptions() != null ? q.getOptions() : Collections.emptyList();
        if (q.getType() == DraftQuestionType.MULTIPLE_CHOICE) {
            long correctCount = 0;
            if (options.isEmpty()) {
                errors.add(ExamConstant.VAL_NO_OPTIONS_FOUND);
            } else {
                correctCount = options.stream().filter(o -> o != null && o.isCorrect()).count();
                if (correctCount == 0) {
                    errors.add(ExamConstant.VAL_NO_CORRECT_ANSWER);
                } else if (correctCount > 1) {
                    errors.add(ExamConstant.VAL_MULTIPLE_CORRECT_ANSWERS);
                }
            }
            q.setHasAnswer(correctCount > 0);
        } else if (q.getType() == DraftQuestionType.ESSAY) {
            if (q.getAnswer() == null || q.getAnswer().isBlank()) {
                errors.add(ExamConstant.VAL_ESSAY_NO_SAMPLE_ANSWER);
            }
            q.setHasAnswer(q.getAnswer() != null && !q.getAnswer().isBlank());
        } else {
            if (q.getAnswer() == null || q.getAnswer().isBlank()) {
                errors.add(ExamConstant.VAL_SHORT_ANSWER_NO_SAMPLE);
            }
            q.setHasAnswer(q.getAnswer() != null && !q.getAnswer().isBlank());
        }

        q.setWarnings(warnings);
        q.setErrors(errors);

        if (!errors.isEmpty()) {
            q.setParsingStatus(ParsingStatus.ERROR);
        } else if (!warnings.isEmpty()) {
            q.setParsingStatus(ParsingStatus.WARNING);
        } else {
            q.setParsingStatus(ParsingStatus.SUCCESS);
        }
    }

    private void saveDraft(DraftExamDto draft) {
        memoryDraftCache.put(draft.getDraftId(), draft);
        draftRepository.findById(draft.getDraftId()).ifPresent(d -> {
            d.setTitle(draft.getTitle());
            d.setTotalQuestions(draft.getTotalQuestions());
            d.setDraftData(draft);
            draftRepository.save(d);
        });
    }
}
