package jurisprudence_hub_be.module.exam.parser;

import jurisprudence_hub_be.module.exam.constant.ExamConstant;
import jurisprudence_hub_be.module.exam.dto.draft.DraftOptionDto;
import jurisprudence_hub_be.module.exam.dto.draft.DraftQuestionDto;
import jurisprudence_hub_be.module.exam.dto.response.ParsedDocumentResponse;
import jurisprudence_hub_be.module.exam.enums.DraftQuestionType;
import jurisprudence_hub_be.module.exam.enums.ParsingStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Parser chuyên sâu xử lý từng câu hỏi trong đề thi.
 * Tích hợp phương pháp bóc tách thông minh chống dính chùm phương án.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class QuestionParser {

    private final ExamDocumentParsingEngine examDocumentParsingEngine;

    public DraftQuestionDto parseQuestionBlock(int questionNumber,
                                               List<String> blockLines,
                                               DraftQuestionType defaultType,
                                               String section,
                                               String context) {

        List<String> lines = blockLines != null ? blockLines : Collections.emptyList();
        String combinedText = String.join("\n", lines);
        ExamDocumentParsingEngine.ParsedOptionsResult optionsResult =
                examDocumentParsingEngine.parseOptionsFromText(combinedText, questionNumber);

        List<ParsedDocumentResponse.ParsedOption> parsedOptions =
                (optionsResult != null && optionsResult.options != null) ? optionsResult.options : Collections.emptyList();

        boolean isEssay = (defaultType == DraftQuestionType.ESSAY);
        boolean isShortAnswer = (defaultType == DraftQuestionType.SHORT_ANSWER);
        boolean hasOptions = parsedOptions.size() >= 2;

        DraftQuestionType finalType;
        if (isEssay) {
            finalType = DraftQuestionType.ESSAY;
        } else if (isShortAnswer || !hasOptions) {
            finalType = DraftQuestionType.SHORT_ANSWER;
        } else {
            finalType = DraftQuestionType.MULTIPLE_CHOICE;
        }

        List<DraftOptionDto> draftOptions = new ArrayList<>();
        String detectedAnswer = optionsResult != null ? optionsResult.correctLabelFromMarker : null;

        for (ParsedDocumentResponse.ParsedOption opt : parsedOptions) {
            if (opt == null) continue;
            String label = opt.getLabel() != null ? opt.getLabel() : "";
            boolean isCorrect = (detectedAnswer != null && label.equalsIgnoreCase(detectedAnswer));
            draftOptions.add(DraftOptionDto.builder()
                    .key(label)
                    .content(opt.getText() != null ? opt.getText() : "")
                    .isCorrect(isCorrect)
                    .build());
        }

        String content = (hasOptions && optionsResult != null && optionsResult.questionText != null)
                ? optionsResult.questionText : combinedText;

        List<String> warnings = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        if (content == null || content.isBlank()) {
            errors.add(ExamConstant.VAL_QUESTION_CONTENT_EMPTY);
        }

        if (finalType == DraftQuestionType.MULTIPLE_CHOICE) {
            if (draftOptions.isEmpty()) {
                errors.add(ExamConstant.VAL_NO_MC_OPTIONS);
            } else if (draftOptions.size() < 4) {
                boolean isTrueFalse = draftOptions.size() == 2 && draftOptions.stream().anyMatch(o -> {
                    if (o == null || o.getContent() == null) return false;
                    String c = o.getContent().toLowerCase();
                    return c.contains("đúng") || c.contains("sai") || c.contains("true") || c.contains("false");
                });
                if (!isTrueFalse) {
                    warnings.add(String.format(ExamConstant.VAL_FEWER_OPTIONS_WARNING, draftOptions.size()));
                }
            }
        }

        ParsingStatus status = !errors.isEmpty() ? ParsingStatus.ERROR :
                (!warnings.isEmpty() ? ParsingStatus.WARNING : ParsingStatus.SUCCESS);

        return DraftQuestionDto.builder()
                .temporaryId("q" + questionNumber)
                .questionNumber(questionNumber)
                .type(finalType)
                .section(section)
                .context(context)
                .content(content.trim())
                .options(draftOptions)
                .answer(detectedAnswer)
                .hasAnswer(detectedAnswer != null && !detectedAnswer.isBlank())
                .parsingStatus(status)
                .warnings(warnings)
                .errors(errors)
                .build();
    }
}
