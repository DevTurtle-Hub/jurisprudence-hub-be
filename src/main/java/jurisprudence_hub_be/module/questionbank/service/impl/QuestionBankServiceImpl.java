package jurisprudence_hub_be.module.questionbank.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jurisprudence_hub_be.common.exception.BadRequestException;
import jurisprudence_hub_be.common.exception.ResourceNotFoundException;
import jurisprudence_hub_be.module.exam.dto.response.ParsedDocumentResponse;
import jurisprudence_hub_be.module.exam.service.DocumentParserService;
import jurisprudence_hub_be.module.questionbank.constant.QuestionBankConstant;
import jurisprudence_hub_be.module.questionbank.dto.request.QuestionBankMcOptionRequest;
import jurisprudence_hub_be.module.questionbank.dto.request.QuestionBankRequest;
import jurisprudence_hub_be.module.questionbank.dto.response.QuestionBankMcOptionResponse;
import jurisprudence_hub_be.module.questionbank.dto.response.QuestionBankResponse;
import jurisprudence_hub_be.module.questionbank.entity.QuestionBank;
import jurisprudence_hub_be.module.questionbank.entity.QuestionBankEditHistory;
import jurisprudence_hub_be.module.questionbank.entity.QuestionBankMcOption;
import jurisprudence_hub_be.module.questionbank.repository.QuestionBankEditHistoryRepository;
import jurisprudence_hub_be.module.questionbank.repository.QuestionBankMcOptionRepository;
import jurisprudence_hub_be.module.questionbank.repository.QuestionBankRepository;
import jurisprudence_hub_be.module.questionbank.service.QuestionBankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import jurisprudence_hub_be.common.service.RedisCacheService;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionBankServiceImpl implements QuestionBankService {

    private static final String QB_CACHE_PREFIX = "question_bank:";
    private static final Duration QB_TTL = Duration.ofHours(2);

    private final QuestionBankRepository questionBankRepository;
    private final QuestionBankMcOptionRepository mcOptionRepository;
    private final QuestionBankEditHistoryRepository editHistoryRepository;
    private final DocumentParserService documentParserService;
    private final ObjectMapper objectMapper;
    private final RedisCacheService redisCacheService;

    @Override
    public List<QuestionBankResponse> parseAndPreviewQuestions(MultipartFile file, String targetType) {
        try {
            // Dùng lại logic import từ exam module
            ParsedDocumentResponse parsedResponse = documentParserService.parseDocument(file, targetType);

            List<QuestionBankResponse> previewQuestions = new ArrayList<>();
            if (parsedResponse == null) {
                return previewQuestions;
            }

            // Convert MC questions sang QuestionBank format
            if (parsedResponse.getMultipleChoiceQuestions() != null) {
                for (var mcQuestion : parsedResponse.getMultipleChoiceQuestions()) {
                    if (mcQuestion == null) continue;
                    QuestionBankResponse qbResponse = convertMcToQuestionBank(mcQuestion);
                    previewQuestions.add(qbResponse);
                }
            }

            // Convert Essay questions sang QuestionBank format
            if (parsedResponse.getEssayQuestions() != null) {
                for (var essayQuestion : parsedResponse.getEssayQuestions()) {
                    if (essayQuestion == null) continue;
                    QuestionBankResponse qbResponse = convertEssayToQuestionBank(essayQuestion);
                    previewQuestions.add(qbResponse);
                }
            }

            return previewQuestions;
        } catch (Exception e) {
            log.error("Lỗi khi bóc tách file vào Question Bank: {}", e.getMessage(), e);
            throw new BadRequestException(QuestionBankConstant.MSG_PARSE_FILE_FAILED + e.getMessage());
        }
    }

    @Override
    @Transactional
    public QuestionBankResponse saveQuestion(QuestionBankRequest request, String createdBy) {
        if (request == null) {
            throw new BadRequestException(QuestionBankConstant.MSG_REQUEST_REQUIRED);
        }
        QuestionBank questionBank = convertRequestToEntity(request);
        questionBank.setCreatedBy(createdBy);
        questionBank.setDraft(true); // Mặc định là draft khi lưu lần đầu

        QuestionBank saved = questionBankRepository.save(questionBank);
        return convertEntityToResponse(saved);
    }

    @Override
    @Transactional
    public List<QuestionBankResponse> saveMultipleQuestions(List<QuestionBankRequest> requests, String createdBy) {
        if (requests == null || requests.isEmpty()) {
            return Collections.emptyList();
        }
        List<QuestionBank> entities = new ArrayList<>();
        for (QuestionBankRequest request : requests) {
            if (request == null) continue;
            QuestionBank qb = convertRequestToEntity(request);
            qb.setCreatedBy(createdBy);
            qb.setDraft(true);
            entities.add(qb);
        }
        if (entities.isEmpty()) {
            return Collections.emptyList();
        }
        List<QuestionBank> saved = questionBankRepository.saveAll(entities);
        return saved.stream().map(this::convertEntityToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionBankResponse> getQuestions(String category, String questionType,
                                                   String keyword, boolean isDraft, Pageable pageable) {
        Page<QuestionBank> questions = questionBankRepository.searchQuestions(
                category, questionType, keyword, isDraft, pageable);

        return questions.map(this::convertEntityToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionBankResponse getQuestionById(String id) {
        if (id == null || id.isBlank()) {
            throw new ResourceNotFoundException(QuestionBankConstant.MSG_QUESTION_NOT_FOUND + id);
        }

        String cacheKey = QB_CACHE_PREFIX + id.trim();
        QuestionBankResponse cached = redisCacheService.get(cacheKey, QuestionBankResponse.class);
        if (cached != null) {
            return cached;
        }

        QuestionBank question = questionBankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(QuestionBankConstant.MSG_QUESTION_NOT_FOUND + id));
        QuestionBankResponse response = convertEntityToResponse(question);
        redisCacheService.set(cacheKey, response, QB_TTL);
        return response;
    }

    @Override
    @Transactional
    public QuestionBankResponse updateQuestion(String id, QuestionBankRequest request, String editedBy, String editReason) {
        if (id == null || id.isBlank()) {
            throw new ResourceNotFoundException(QuestionBankConstant.MSG_QUESTION_NOT_FOUND + id);
        }
        if (request == null) {
            throw new BadRequestException(QuestionBankConstant.MSG_REQUEST_REQUIRED);
        }
        QuestionBank existingQuestion = questionBankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(QuestionBankConstant.MSG_QUESTION_NOT_FOUND + id));

        // Lưu lịch sử chỉnh sửa
        saveEditHistory(existingQuestion, editedBy, editReason);

        // Update question
        updateQuestionFromRequest(existingQuestion, request);
        existingQuestion.setUpdatedAt(Instant.now());

        // Update options nếu là MC question
        if ("MC".equalsIgnoreCase(request.getQuestionType())) {
            if (existingQuestion.getOptions() == null) {
                existingQuestion.setOptions(new ArrayList<>());
            } else {
                existingQuestion.getOptions().clear();
            }
            if (request.getOptions() != null) {
                for (QuestionBankMcOptionRequest optRequest : request.getOptions()) {
                    if (optRequest == null) continue;
                    QuestionBankMcOption option = QuestionBankMcOption.builder()
                            .question(existingQuestion)
                            .label(optRequest.getLabel() != null ? optRequest.getLabel() : "")
                            .optionText(optRequest.getOptionText() != null ? optRequest.getOptionText() : "")
                            .isCorrect(optRequest.isCorrect())
                            .build();
                    existingQuestion.getOptions().add(option);
                }
            }
        }

        QuestionBank updated = questionBankRepository.save(existingQuestion);
        redisCacheService.delete(QB_CACHE_PREFIX + id.trim());
        return convertEntityToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteQuestion(String id, String deletedBy) {
        if (id == null || id.isBlank()) {
            throw new ResourceNotFoundException(QuestionBankConstant.MSG_QUESTION_NOT_FOUND + id);
        }
        QuestionBank question = questionBankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(QuestionBankConstant.MSG_QUESTION_NOT_FOUND + id));

        // Lưu lịch sử xóa
        // Xóa lịch sử liên quan trước khi xóa câu hỏi
        editHistoryRepository.deleteByQuestionId(id);

        questionBankRepository.delete(question);
        redisCacheService.delete(QB_CACHE_PREFIX + id.trim());
    }

    @Override
    @Transactional
    public QuestionBankResponse publishQuestion(String id, String verifiedBy) {
        if (id == null || id.isBlank()) {
            throw new ResourceNotFoundException(QuestionBankConstant.MSG_QUESTION_NOT_FOUND + id);
        }
        QuestionBank question = questionBankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(QuestionBankConstant.MSG_QUESTION_NOT_FOUND + id));

        question.setDraft(false);
        question.setVerifiedBy(verifiedBy);
        question.setVerifiedAt(Instant.now());

        QuestionBank published = questionBankRepository.save(question);
        redisCacheService.delete(QB_CACHE_PREFIX + id.trim());
        return convertEntityToResponse(published);
    }

    @Override
    @Transactional
    public QuestionBankResponse createManualQuestion(QuestionBankRequest request, String createdBy) {
        return saveQuestion(request, createdBy);
    }

    private QuestionBankResponse convertMcToQuestionBank(ParsedDocumentResponse.ParsedMcQuestion mc) {
        if (mc == null) return null;
        List<QuestionBankMcOptionResponse> options = new ArrayList<>();
        if (mc.getOptions() != null) {
            options = mc.getOptions().stream()
                    .filter(Objects::nonNull)
                    .map(opt -> {
                        String label = opt.getLabel() != null ? opt.getLabel() : "";
                        boolean isCorrect = mc.getCorrectAnswer() != null && mc.getCorrectAnswer().equalsIgnoreCase(label);
                        return QuestionBankMcOptionResponse.builder()
                                .id(opt.getId())
                                .label(label)
                                .text(opt.getText())
                                .isCorrect(isCorrect)
                                .build();
                    })
                    .collect(Collectors.toList());
        }

        return QuestionBankResponse.builder()
                .id(mc.getId())
                .title("Câu " + mc.getOrder() + ": Trắc nghiệm")
                .description(mc.getContext())
                .questionType("MC")
                .questionText(mc.getQuestion())
                .correctAnswer(mc.getCorrectAnswer())
                .explanation(mc.getExplanation())
                .legalReference(mc.getLegalReference())
                .isDraft(true)
                .options(options)
                .build();
    }

    private QuestionBankResponse convertEssayToQuestionBank(ParsedDocumentResponse.ParsedEssayQuestion essay) {
        if (essay == null) return null;
        return QuestionBankResponse.builder()
                .id(essay.getId())
                .title(essay.getTitle() != null ? essay.getTitle() : ("Câu " + essay.getOrder() + ": Tự luận"))
                .description(essay.getContext())
                .questionType("ESSAY")
                .questionText(essay.getPrompt())
                .isDraft(true)
                .options(new ArrayList<>())
                .build();
    }

    private QuestionBank convertRequestToEntity(QuestionBankRequest request) {
        if (request == null) return null;
        QuestionBank question = QuestionBank.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .questionType(request.getQuestionType())
                .questionText(request.getQuestionText())
                .correctAnswer(request.getCorrectAnswer())
                .explanation(request.getExplanation())
                .legalReference(request.getLegalReference())
                .sampleEssay(request.getSampleEssay())
                .isDraft(request.isDraft())
                .build();

        if (request.getOptions() != null) {
            for (QuestionBankMcOptionRequest optRequest : request.getOptions()) {
                if (optRequest == null) continue;
                QuestionBankMcOption option = QuestionBankMcOption.builder()
                        .question(question)
                        .label(optRequest.getLabel() != null ? optRequest.getLabel() : "")
                        .optionText(optRequest.getOptionText() != null ? optRequest.getOptionText() : "")
                        .isCorrect(optRequest.isCorrect())
                        .build();
                question.getOptions().add(option);
            }
        }

        return question;
    }

    private QuestionBankResponse convertEntityToResponse(QuestionBank question) {
        if (question == null) return null;
        List<QuestionBankMcOptionResponse> options = new ArrayList<>();
        if (question.getOptions() != null) {
            options = question.getOptions().stream()
                    .filter(Objects::nonNull)
                    .map(opt -> QuestionBankMcOptionResponse.builder()
                            .id(opt.getId())
                            .label(opt.getLabel())
                            .text(opt.getOptionText())
                            .isCorrect(opt.isCorrect())
                            .build())
                    .collect(Collectors.toList());
        }

        return QuestionBankResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .description(question.getDescription())
                .category(question.getCategory())
                .questionType(question.getQuestionType())
                .questionText(question.getQuestionText())
                .correctAnswer(question.getCorrectAnswer())
                .explanation(question.getExplanation())
                .legalReference(question.getLegalReference())
                .sampleEssay(question.getSampleEssay())
                .createdBy(question.getCreatedBy())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .isDraft(question.isDraft())
                .verifiedBy(question.getVerifiedBy())
                .verifiedAt(question.getVerifiedAt())
                .options(options)
                .build();
    }

    private void saveEditHistory(QuestionBank question, String editedBy, String editReason) {
        if (question == null) return;
        try {
            String oldData = objectMapper.writeValueAsString(question);
            QuestionBankEditHistory history = QuestionBankEditHistory.builder()
                    .question(question)
                    .editedBy(editedBy)
                    .editReason(editReason)
                    .oldData(oldData)
                    .build();
            editHistoryRepository.save(history);
        } catch (Exception e) {
            log.error("Lỗi khi lưu lịch sử chỉnh sửa: {}", e.getMessage());
        }
    }

    private void updateQuestionFromRequest(QuestionBank question, QuestionBankRequest request) {
        if (question == null || request == null) return;
        question.setTitle(request.getTitle());
        question.setDescription(request.getDescription());
        question.setCategory(request.getCategory());
        question.setQuestionType(request.getQuestionType());
        question.setQuestionText(request.getQuestionText());
        question.setCorrectAnswer(request.getCorrectAnswer());
        question.setExplanation(request.getExplanation());
        question.setLegalReference(request.getLegalReference());
        question.setSampleEssay(request.getSampleEssay());
        question.setDraft(request.isDraft());
    }
}

