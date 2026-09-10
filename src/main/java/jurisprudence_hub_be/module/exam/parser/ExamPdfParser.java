package jurisprudence_hub_be.module.exam.parser;

import jurisprudence_hub_be.module.exam.dto.draft.DraftExamDto;
import jurisprudence_hub_be.module.exam.enums.ImportMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Bộ điều phối phân tích đề thi từ file PDF theo cấu trúc Phần (Section-based).
 * Tích hợp ExamDocumentParsingEngine xử lý thông minh mọi cấu trúc đề thi CAND.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ExamPdfParser {

    private final ExamDocumentParsingEngine examDocumentParsingEngine;

    public DraftExamDto parseExamText(String extractedText, String fileName, ImportMode importMode) {
        return examDocumentParsingEngine.parseToDraftExamDto(extractedText, fileName, importMode);
    }
}
