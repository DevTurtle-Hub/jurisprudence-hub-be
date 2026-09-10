package jurisprudence_hub_be.module.exam.service.impl;

import jurisprudence_hub_be.common.exception.BadRequestException;
import jurisprudence_hub_be.module.exam.constant.ExamConstant;
import jurisprudence_hub_be.module.exam.dto.response.ParsedDocumentResponse;
import jurisprudence_hub_be.module.exam.parser.ExamDocumentParsingEngine;
import jurisprudence_hub_be.module.exam.service.DocumentParserService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DocumentParserServiceImpl implements DocumentParserService {

    private static final Logger log = LoggerFactory.getLogger(DocumentParserServiceImpl.class);

    private final ExamDocumentParsingEngine examDocumentParsingEngine;

    public DocumentParserServiceImpl(ExamDocumentParsingEngine examDocumentParsingEngine) {
        this.examDocumentParsingEngine = examDocumentParsingEngine;
    }

    @Override
    public ParsedDocumentResponse parseDocument(MultipartFile file, String targetType) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException(ExamConstant.MSG_FILE_EMPTY);
        }

        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        String extractedText;

        try {
            if (fileName.endsWith(".pdf")) {
                extractedText = extractTextFromPdf(file);
            } else if (fileName.endsWith(".docx") || fileName.endsWith(".doc")) {
                extractedText = extractTextFromDocx(file);
            } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                extractedText = extractTextFromXlsx(file);
            } else {
                extractedText = new String(file.getBytes(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            log.error("Lỗi khi đọc file tài liệu: {}", e.getMessage(), e);
            throw new BadRequestException(ExamConstant.MSG_FILE_READ_ERROR + e.getMessage());
        }

        if (extractedText == null || extractedText.isBlank()) {
            throw new BadRequestException(ExamConstant.MSG_FILE_NO_TEXT);
        }

        // Xử lý 100% bằng Engine bóc tách thông minh chuyên sâu chuẩn Bộ Công An
        ParsedDocumentResponse localResult = examDocumentParsingEngine.parseToParsedDocumentResponse(extractedText, fileName, targetType);
        return filterByTargetType(sanitizeParsedResponse(localResult), targetType);
    }

    private String extractTextFromPdf(MultipartFile file) throws Exception {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(document);
        }
    }

    private String extractTextFromDocx(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream();
             XWPFDocument doc = new XWPFDocument(is);
             XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
            return extractor.getText();
        }
    }

    private String extractTextFromXlsx(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream();
             XSSFWorkbook wb = new XSSFWorkbook(is);
             XSSFExcelExtractor extractor = new XSSFExcelExtractor(wb)) {
            return extractor.getText();
        }
    }

    /**
     * Phương thức phân tích đề thi bằng regex chuyên dụng cho đề thi CAND.
     */
    public ParsedDocumentResponse parseWithRegex(String text, String fileName) {
        ParsedDocumentResponse res = examDocumentParsingEngine.parseToParsedDocumentResponse(text, fileName, "FULL");
        return sanitizeParsedResponse(res);
    }



    /**
     * Hậu xử lý kết quả bóc tách đảm bảo phân loại chuẩn xác và loại bỏ sạch sẽ footer rác.
     */
    public ParsedDocumentResponse sanitizeParsedResponse(ParsedDocumentResponse response) {
        if (response == null) return null;

        List<ParsedDocumentResponse.ParsedMcQuestion> mcqs = new ArrayList<>(
                response.getMultipleChoiceQuestions() != null ? response.getMultipleChoiceQuestions() : Collections.emptyList()
        );
        List<ParsedDocumentResponse.ParsedEssayQuestion> essays = new ArrayList<>(
                response.getEssayQuestions() != null ? response.getEssayQuestions() : Collections.emptyList()
        );

        List<ParsedDocumentResponse.ParsedEssayQuestion> genuineEssays = new ArrayList<>();

        for (ParsedDocumentResponse.ParsedEssayQuestion essay : essays) {
            String prompt = essay.getPrompt() != null ? essay.getPrompt() : "";
            String title = essay.getTitle() != null ? essay.getTitle() : "";
            String context = essay.getContext() != null ? essay.getContext() : "";

            // Kiểm tra xem câu này có phải là câu hỏi trắc nghiệm trả lời ngắn bị phân loại nhầm sang Tự luận không
            // Dấu hiệu: Có "Trả lời:____", hoặc prompt ngắn mà không có từ khóa nghị luận/bài viết
            boolean isShortAnswerQuestion = prompt.contains("Trả lời:") ||
                    prompt.contains("Trả lời :") ||
                    prompt.contains("Trả lời:___") ||
                    (essay.getOrder() >= 50 && prompt.length() < 400 && !prompt.toLowerCase().contains("nghị luận") && !prompt.toLowerCase().contains("bài viết"));

            // Nếu là câu trắc nghiệm trả lời ngắn (điền vào chỗ trống)
            if (isShortAnswerQuestion) {
                // Chuyển sang multipleChoiceQuestions với options rỗng
                int order = essay.getOrder();
                java.util.regex.Matcher numMatcher = java.util.regex.Pattern.compile("\\b(\\d+)\\b").matcher(title);
                if (numMatcher.find()) {
                    try {
                        order = Integer.parseInt(numMatcher.group(1));
                    } catch (Exception ignored) {}
                }

                String cleanQuestion = ExamDocumentParsingEngine.cleanGarbageFooters(prompt);

                mcqs.add(ParsedDocumentResponse.ParsedMcQuestion.builder()
                        .id("mc-ext-" + order)
                        .order(order)
                        .question(cleanQuestion)
                        .options(Collections.emptyList())
                        .correctAnswer("")
                        .explanation("Căn cứ quy định pháp luật.")
                        .legalReference("Quy định pháp luật")
                        .build());
            } else {
                // Là câu tự luận thực sự (bao gồm tình huống, bài viết nghị luận)
                essay.setTitle(ExamDocumentParsingEngine.cleanGarbageFooters(title));
                essay.setContext(ExamDocumentParsingEngine.cleanGarbageFooters(context));
                essay.setPrompt(ExamDocumentParsingEngine.cleanGarbageFooters(prompt));
                if (essay.getRubric() != null) {
                    essay.setRubric(essay.getRubric().stream().map(ExamDocumentParsingEngine::cleanGarbageFooters).toList());
                }
                genuineEssays.add(essay);
            }
        }

        // Làm sạch footer và xóa mặc định correctAnswer khỏi tất cả mcqs
        List<ParsedDocumentResponse.ParsedMcQuestion> cleanedMcqs = new ArrayList<>();
        for (ParsedDocumentResponse.ParsedMcQuestion mc : mcqs) {
            mc.setId("mc-" + mc.getOrder());
            mc.setCorrectAnswer(""); // Không mặc định đáp án đúng để FE người tạo đề tự chọn
            if (mc.getContext() != null) {
                mc.setContext(ExamDocumentParsingEngine.cleanGarbageFooters(mc.getContext()));
            }
            mc.setQuestion(ExamDocumentParsingEngine.cleanGarbageFooters(mc.getQuestion()));
            if (mc.getExplanation() != null) {
                mc.setExplanation(ExamDocumentParsingEngine.cleanGarbageFooters(mc.getExplanation()));
            }
            if (mc.getLegalReference() != null) {
                mc.setLegalReference(ExamDocumentParsingEngine.cleanGarbageFooters(mc.getLegalReference()));
            }
            if (mc.getOptions() != null) {
                for (ParsedDocumentResponse.ParsedOption opt : mc.getOptions()) {
                    if (opt != null && opt.getText() != null) {
                        opt.setText(ExamDocumentParsingEngine.cleanGarbageFooters(opt.getText()));
                    }
                }
            }
            cleanedMcqs.add(mc);
        }

        // Sắp xếp mcqs theo order
        cleanedMcqs.sort(java.util.Comparator.comparingInt(ParsedDocumentResponse.ParsedMcQuestion::getOrder));

        // Đánh lại số thứ tự cho genuineEssays
        int eOrder = 1;
        for (ParsedDocumentResponse.ParsedEssayQuestion eq : genuineEssays) {
            eq.setOrder(eOrder);
            eq.setId("essay-" + eOrder);
            eOrder++;
        }

        response.setMultipleChoiceQuestions(cleanedMcqs);
        response.setEssayQuestions(genuineEssays);
        if (response.getExtractedTitle() != null) {
            response.setExtractedTitle(ExamDocumentParsingEngine.cleanGarbageFooters(response.getExtractedTitle()));
        }

        return response;
    }

    private ParsedDocumentResponse filterByTargetType(ParsedDocumentResponse res, String targetType) {
        if (res == null) return null;
        if ("MC_ONLY".equalsIgnoreCase(targetType)) {
            res.setEssayQuestions(Collections.emptyList());
        } else if ("ESSAY_ONLY".equalsIgnoreCase(targetType)) {
            res.setMultipleChoiceQuestions(Collections.emptyList());
        }
        return res;
    }
}
