package jurisprudence_hub_be.module.exam.parser;

import jurisprudence_hub_be.module.exam.dto.draft.DraftExamDto;
import jurisprudence_hub_be.module.exam.dto.draft.DraftOptionDto;
import jurisprudence_hub_be.module.exam.dto.draft.DraftQuestionDto;
import jurisprudence_hub_be.module.exam.dto.response.ParsedDocumentResponse;
import jurisprudence_hub_be.module.exam.enums.DraftQuestionType;
import jurisprudence_hub_be.module.exam.enums.ImportMode;
import jurisprudence_hub_be.module.exam.enums.ParsingStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Engine bóc tách thông minh và chuẩn hóa đề thi CAND chuyên sâu.
 * Xử lý hoàn hảo các trường hợp:
 * 1. Phương án trắc nghiệm nằm cùng 1 dòng (A. ... B. ... hoặc A. ... B. ... C. ... D. ...).
 * 2. Phương án trắc nghiệm nhiều dòng hoặc chứa các từ viết tắt pháp luật CAND (Bảo vệ, CAND, Bộ luật, Điều 15, v.v.).
 * 3. Tách bạch chính xác giữa Phần Tự luận (Tình huống, Bối cảnh, Yêu cầu, Thang điểm) và Phần Trắc nghiệm.
 * 4. Tự động tìm và ánh xạ Bảng đáp án ở cuối đề thi nếu có.
 */
@Component
@Slf4j
public class ExamDocumentParsingEngine {

    // Regex phát hiện tiêu đề phần
    private static final Pattern ESSAY_SECTION_HEADER = Pattern.compile(
            "(?imu)^\\s*(?:PHẦN\\s*(?:I|1|A|TỰ\\s*LUẬN)[:\\s.\\-]*TỰ\\s*LUẬN|PHẦN\\s*TỰ\\s*LUẬN|TỰ\\s*LUẬN\\b)[:\\s.\\-]*(.*)$"
    );
    private static final Pattern SITUATIONAL_SECTION_HEADER = Pattern.compile(
            "(?imu)^\\s*(?:BÀI\\s*TẬP\\s*TÌNH\\s*HUỐNG|TÌNH\\s*HUỐNG\\s*PHÁP\\s*LÝ|PHẦN\\s*TÌNH\\s*HUỐNG|TÌNH\\s*HUỐNG|ĐỌC\\s*VĂN\\s*BẢN\\s*SAU|ĐỌC\\s*ĐOẠN\\s*(?:TRÍCH|VĂN)\\s*SAU|THÔNG\\s*TIN\\s*SAU)(?:\\s+(?:SỐ\\s*)?\\d+)?\\b[:\\s.\\-]*(.*)$"
    );
    private static final Pattern SITUATIONAL_PROMPT_HEADER = Pattern.compile(
            "(?imu)^\\s*(?:TRẢ\\s*LỜI\\s*(?:(?:BÀI\\s*TẬP\\s*)?TÌNH\\s*HUỐNG|(?:CÁC\\s+)?CÂU\\s*HỎI(?:\\s*SAU)?)|ĐỌC\\s*(?:KỸ\\s*)?(?:TÌNH\\s*HUỐNG|VĂN\\s*BẢN|ĐOẠN\\s*(?:VĂN|TRÍCH)|THÔNG\\s*TIN)\\s*(?:SAU|DƯỚI\\s*ĐÂY)?\\s*VÀ\\s*TRẢ\\s*LỜI|DỰA\\s*VÀO\\s*(?:THÔNG\\s*TIN|TÌNH\\s*HUỐNG|NỘI\\s*DUNG|VĂN\\s*BẢN)\\s*(?:SAU|DƯỚI\\s*ĐÂY)?\\s*TRẢ\\s*LỜI|CÂU\\s*HỎI\\s*TÌNH\\s*HUỐNG).*$"
    );
    private static final Pattern SITUATIONAL_RANGE_PATTERN = Pattern.compile(
            "(?iu)\\(?\\s*(?:(?:(?:Từ|Áp\\s*dụng\\s*cho|Dùng\\s*cho)\\s+)?"
                    + "(?:(?:các\\s+)?câu(?:\\s+hỏi)?\\s*)?"
                    + "(?:Từ\\s+)?(?:câu(?:\\s+hỏi)?\\s*)?(?:số\\s*)?(\\d+)\\s*"
                    + "(?:đến|tới|[-–—])\\s*(?:câu(?:\\s+hỏi)?\\s*)?(?:số\\s*)?(\\d+))"
                    + "\\s*\\)?\\s*[:;.]?"
    );
    private static final Pattern LABELED_SITUATIONAL_CONTEXT_LINE = Pattern.compile(
            "(?iu)^\\s*(?:Tình\\s*huống(?:\\s*pháp\\s*lý)?|Bối\\s*cảnh)\\s*[:.\\-]\\s*\\S.*$"
    );
    private static final Pattern MCQ_SECTION_HEADER = Pattern.compile(
            "(?imu)^\\s*(?:PHẦN\\s*(?:II|2|B|TRẮC\\s*NGHIỆM)[:\\s.\\-]*TRẮC\\s*NGHIỆM|PHẦN\\s*TRẮC\\s*NGHIỆM|TRẮC\\s*NGHIỆM\\s*KHÁCH\\s*QUAN|TRẮC\\s*NGHIỆM\\b)[:\\s.\\-]*(.*)$"
    );
    private static final Pattern SHORT_ANSWER_SECTION_HEADER = Pattern.compile(
            "(?imu)^\\s*(?:PHẦN\\s*(?:III|3|C)[:\\s.\\-]*(?:TRẮC\\s*NGHIỆM\\s*)?TRẢ\\s*LỜI\\s*NGẮN|TRẮC\\s*NGHIỆM\\s*TRẢ\\s*LỜI\\s*NGẮN|CÂU\\s*HỎI\\s*TRẢ\\s*LỜI\\s*NGẮN|TRẢ\\s*LỜI\\s*NGẮN\\b)[:\\s.\\-]*(.*)$"
    );
    private static final Pattern ANSWER_KEY_SECTION_HEADER = Pattern.compile(
            "(?imu)^\\s*(?:ĐÁP\\s*ÁN\\s*(?:VÀ\\s*HƯỚNG\\s*DẪN\\s*CHẤM|ĐỀ\\s*THI|CHI\\s*TIẾT|TRẮC\\s*NGHIỆM)|BẢNG\\s*ĐÁP\\s*ÁN|HƯỚNG\\s*DẪN\\s*CHẤM|KEY\\s*ĐÁP\\s*ÁN)\\b.*$"
    );

    // Regex phát hiện đầu câu hỏi: Câu 1., Câu 1:, Câu 1 (15 điểm):, Tình huống 1:, Bài tập 1:, Question 1:, 1.
    private static final Pattern QUESTION_HEADER_PATTERN = Pattern.compile(
            "(?imu)^\\s*(?:(?:Câu|CÂU|câu)(?:\\s+(?:số|thứ))?\\s+(\\d+)|(?:Tình\\s+huống|TÌNH\\s+HUỐNG)(?:\\s+(?:số|thứ))?\\s+(\\d+)|(?:Bài\\s+tập|BÀI\\s+TẬP|Bài|BÀI)(?:\\s+(?:số|thứ))?\\s+(\\d+)|(?:Câu\\s+hỏi|CÂU\\s+HỎI)(?:\\s+(?:số|thứ))?\\s+(\\d+)|(?:Question|QUESTION|Q\\.?)\\s*(\\d+)|(\\d+)[.:\\)\\/-]\\s+(?=\\S.*))\\s*(?:\\([^)]*\\)|\\[[^\\]]*\\])?\\s*[.:\\-\\)/]?\\s*(.*)$"
    );

    // Regex phát hiện đáp án, giải thích, căn cứ trong từng câu (Hỗ trợ A-F)
    private static final Pattern INLINE_ANSWER_PATTERN = Pattern.compile(
            "(?iu)(?:^|[\\r\\n;\\s])\\s*(?:\\*?\\s*(?:Đáp\\s*án(?:\\s*đúng)?|Chọn(?:\\s*đáp\\s*án)?|ĐA|Đ\\/a|Key)[:\\s]*([A-F])\\b)",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern INLINE_EXPLANATION_PATTERN = Pattern.compile(
            "(?imu)^\\s*(?:Giải\\s*thích|Hướng\\s*dẫn\\s*giải|Lý\\s*do)[:\\s]*(.*)$"
    );
    private static final Pattern INLINE_REFERENCE_PATTERN = Pattern.compile(
            "(?imu)^\\s*(?:Căn\\s*cứ(?:\\s*pháp\\s*lý)?|Điều\\s*luật|Theo\\s*quy\\s*định)[:\\s]*(.*)$"
    );

    // Regex nhận diện marker phương án trắc nghiệm: A. / A) / A: / A - / (A) / [A] / *A. / A*. / a.
    // Hỗ trợ từ A đến F, nhận diện dấu sao * ở trước hoặc sau chữ cái, phân cách bằng tab, space hoặc chấm phẩy/pipe
    private static final Pattern OPTION_TOKEN_PATTERN = Pattern.compile(
            "(?i)(?:^|[\\r\\n]|\\s{2,}|\\t|(?<=[.;?!|]\\s*))"
            + "(?:\\[?\\(?(\\*?)\\s*([A-F])\\s*(\\*?)[.:\\-\\)]\\)?(\\*?)"
            + "|\\[([A-F])\\]\\s*(\\*?)"
            + "|\\(([A-F])\\)\\s*(\\*?))\\s*"
    );

    // Regex nhận diện điểm số trong câu hỏi: (15 điểm), (30.0 điểm), [10 điểm], 15 điểm, (1.5đ)...
    private static final Pattern ESSAY_SCORE_PATTERN = Pattern.compile(
            "(?iu)(?:\\((\\d+(?:[.,]\\d+)?)\\s*(?:điểm|đ)\\)|\\[(\\d+(?:[.,]\\d+)?)\\s*(?:điểm|đ)\\]|(\\d+(?:[.,]\\d+)?)\\s*(?:điểm|đ)\\b)"
    );

    // Regex nhận diện phần yêu cầu / câu hỏi trong bài tự luận
    private static final Pattern ESSAY_PROMPT_START_PATTERN = Pattern.compile(
            "(?imu)^\\s*(?:Anh\\s*\\/\\s*chị\\s*hãy|Thí\\s*sinh\\s*hãy|Hãy\\s+viết|Hãy|Yêu\\s*cầu|Nhiệm\\s*vụ|Câu\\s*hỏi|Nội\\s*dung\\s*cần\\s*giải\\s*quyết)[:\\s]*(.*)$"
    );

    // Regex phát hiện câu hỏi trắc nghiệm với nội dung dài (để xử lý đặc biệt)
    private static final Pattern LONG_MC_QUESTION_PATTERN = Pattern.compile(
            "(?imu)^\\s*(?:(?:Câu|CÂU|câu)\\s+(\\d+)|Tình\\s+huống\\s+(\\d+)|Bài\\s+tập\\s+(\\d+)|Câu\\s+hỏi\\s+(\\d+))\\s*(?:\\([^)]*\\)|\\[[^\\]]*\\])?\\s*[.:\\-\\)]?\\s*([\\s\\S]*?)(?=\\s*[A-F][.:\\-\\)]|$)"
    );

    // Pre-compiled regex patterns để tối đa hóa throughput và loại bỏ áp lực GC khi parse
    private static final Pattern NUMERIC_VALUE_LINE_PATTERN = Pattern.compile("^(?:\\(?\\d+(?:[.,]\\d+)?\\)?|\\d{1,4})$");
    private static final Pattern OPTION_LINE_START_PATTERN = Pattern.compile("(?i)^\\*?\\s*(?:\\[[A-F]\\]|\\(?[A-F][.:\\-\\)])\\s*.*");
    private static final Pattern PAGE_NUMBER_ALONE_PATTERN = Pattern.compile("^\\d{1,3}$");
    private static final Pattern FOOTER_END_MARKER_PATTERN = Pattern.compile("(?iu)^[\\-=_*~\\s]*HẾT[\\-=_*~\\s]*$");
    private static final Pattern FOOTER_PROCTOR_NOTICE_PATTERN = Pattern.compile("(?iu)^.*cán\\s*bộ\\s*(?:coi|chấm)\\s*thi\\s*không\\s*giải\\s*thích\\s*gì\\s*thêm.*$");
    private static final Pattern FOOTER_MATERIAL_NOTICE_PATTERN = Pattern.compile("(?iu)^.*thí\\s*sinh\\s*không\\s*(?:được)?\\s*sử\\s*dụng\\s*tài\\s*liệu.*$");
    private static final Pattern FOOTER_PAGE_NUM_PATTERN = Pattern.compile("(?iu)^.*Trang\\s*\\d+(?:\\s*\\/\\s*\\d+)?.*$");
    private static final Pattern FOOTER_EXAM_CODE_PATTERN = Pattern.compile("(?iu)^.*Mã\\s*(?:bài|đề)\\s*thi\\s*[:\\s]*[A-Z0-9_\\-]+.*$");
    private static final Pattern FOOTER_PROCTOR_SIGN_PATTERN = Pattern.compile("(?iu)^.*(?:Giám\\s*thị|Chữ\\s*ký\\s*giám\\s*thị).*$");
    private static final Pattern SITUATIONAL_LEAD_IN_PATTERN = Pattern.compile("(?iu)^\\(?\\s*(?:Từ|Áp\\s*dụng|Dùng|Trả\\s*lời|Đọc).*\\)?[:;.]?$");
    private static final Pattern NUMBERED_ASSERTION_START_PATTERN = Pattern.compile("^(?:\\(?\\d+[).:]|\\([a-zA-Z]\\)|(?:I|II|III|IV|V|VI|VII|VIII|IX|X)[).:])\\s*.*");
    private static final Pattern BULLET_POINT_PATTERN = Pattern.compile("^[•\\-\\*+]\\s+.*");
    private static final Pattern LEADING_QUESTION_PROMPT_PATTERN = Pattern.compile("(?iu)^(?:Có\\s+bao\\s+nhiêu|Hỏi[:\\s]|Nhận\\s+định\\s+nào|Khẳng\\s+định\\s+nào|Phát\\s+biểu\\s+nào|Trong\\s+các|Theo\\s+đó|Như\\s+vậy|Chọn\\s+câu|Hãy\\s+cho\\s+biết)\\b.*");
    private static final Pattern OPTION_PREFIX_PATTERN = Pattern.compile("(?i)^[A-F][.:\\-\\)]\\s.*");
    private static final Pattern TRAILING_PAGE_NUMBER_PATTERN = Pattern.compile("(?<=[?.!:;”\"'\\)])\\s+\\d{1,3}\\s*$");
    private static final Pattern ASSERTION_PLACEHOLDER_PATTERN = Pattern.compile("\\((\\d+)\\)");
    private static final Pattern ASSERTION_RESTORE_PATTERN = Pattern.compile("___ASSERTION_(\\d+)___");

    /**
     * Làm sạch các dòng rác cuối trang / cuối đề thi.
     */
    public static String cleanGarbageFooters(String text) {
        if (text == null || text.isBlank()) return "";

        String trimmedText = text.trim();
        // Nếu chuỗi đầu vào chỉ là một dòng duy nhất và là số hoặc nhận định ngắn (ví dụ: "1", "2", "3", "4", "15", "(1)", "100"),
        // thì đây là nội dung giá trị hợp lệ (option text, điểm số, số lượng...), KHÔNG PHẢI là số trang rác cuối trang!
        if (!trimmedText.contains("\n") && NUMERIC_VALUE_LINE_PATTERN.matcher(trimmedText).matches()) {
            return trimmedText;
        }

        // Bảo vệ các số nhận định trong ngoặc đơn như (1), (2), (3), (4), (5), (6)
        String protectedText = protectNumberedAssertions(text);

        String[] lines = protectedText.split("\n");
        List<String> validLines = new ArrayList<>();
        for (String line : lines) {
            String trimmed = line.trim();
            if (isFooterOrHeaderGarbage(trimmed)) {
                continue;
            }
            // Chỉ loại bỏ số trang rác dính ở cuối dòng sau dấu kết thúc câu nếu dòng KHÔNG phải là phương án trắc nghiệm
            // Ví dụ "...phù hợp nhất? 24" -> "...phù hợp nhất?", nhưng "A. 3" hoặc "B. 4" phải giữ nguyên số "3", "4"
            String cleanedLine = line;
            if (!OPTION_LINE_START_PATTERN.matcher(trimmed).matches()) {
                cleanedLine = TRAILING_PAGE_NUMBER_PATTERN.matcher(line).replaceAll("");
            }
            validLines.add(cleanedLine);
        }

        // Khôi phục lại các số nhận định đã bảo vệ
        String result = String.join("\n", validLines).trim();
        return restoreNumberedAssertions(result);
    }
    
    /**
     * Bảo vệ các số nhận định trong ngoặc đơn như (1), (2), (3), (4), (5), (6)
     * bằng cách thay thế chúng bằng placeholder
     */
    private static String protectNumberedAssertions(String text) {
        return ASSERTION_PLACEHOLDER_PATTERN.matcher(text).replaceAll("___ASSERTION_$1___");
    }
    
    /**
     * Khôi phục lại các số nhận định từ placeholder
     */
    private static String restoreNumberedAssertions(String text) {
        return ASSERTION_RESTORE_PATTERN.matcher(text).replaceAll("($1)");
    }

    /**
     * Kiểm tra một dòng có phải là footer/header rác không.
     */
    public static boolean isFooterOrHeaderGarbage(String line) {
        if (line == null || line.isBlank()) return false;
        String trimmed = line.trim();
        // Số trang đứng một mình (ví dụ: 1, 2, 23, 24, 29...)
        if (PAGE_NUMBER_ALONE_PATTERN.matcher(trimmed).matches()) return true;
        // HẾT (ví dụ: --------------- HẾT ---------------)
        if (FOOTER_END_MARKER_PATTERN.matcher(trimmed).matches()) return true;
        // Cán bộ coi thi không giải thích gì thêm
        if (FOOTER_PROCTOR_NOTICE_PATTERN.matcher(trimmed).matches()) return true;
        // Thí sinh không được sử dụng tài liệu
        if (FOOTER_MATERIAL_NOTICE_PATTERN.matcher(trimmed).matches()) return true;
        // Trang X/Y hoặc Trang X (ví dụ: Trang 8/8 - Mã bài thi CA4)
        if (FOOTER_PAGE_NUM_PATTERN.matcher(trimmed).matches()) return true;
        // Mã bài thi CA4 / Mã đề thi ...
        if (FOOTER_EXAM_CODE_PATTERN.matcher(trimmed).matches()) return true;
        // Giám thị ...
        if (FOOTER_PROCTOR_SIGN_PATTERN.matcher(trimmed).matches()) return true;
        return false;
    }

    /**
     * Parse văn bản và trả về ParsedDocumentResponse cho API /api/v1/exams/parse-document.
     */
    public ParsedDocumentResponse parseToParsedDocumentResponse(String rawText, String fileName, String targetType) {
        String normalizedText = cleanAndNormalize(rawText);

        String title = extractTitle(normalizedText, fileName);
        int durationMinutes = extractDurationMinutes(normalizedText);

        // 1. Tìm bảng đáp án ở cuối tài liệu nếu có
        Map<Integer, String> answerKeyMap = extractAnswerKeyMap(normalizedText);

        // 2. Phân đoạn văn bản thành các khối câu hỏi theo Section
        List<ExtractedQuestion> extractedQuestions = extractAllQuestions(normalizedText, answerKeyMap);

        List<ParsedDocumentResponse.ParsedMcQuestion> mcQuestions = new ArrayList<>();
        List<ParsedDocumentResponse.ParsedEssayQuestion> essayQuestions = new ArrayList<>();

        int mcOrder = 1;
        int essayOrder = 1;

        for (ExtractedQuestion eq : extractedQuestions) {
            if (eq.isEssay()) {
                int orderToUse = eq.getOrder() > 0 ? eq.getOrder() : essayOrder++;
                ParsedDocumentResponse.ParsedEssayQuestion essay = buildParsedEssayQuestion(eq, orderToUse);
                essayQuestions.add(essay);
            } else {
                int orderToUse = eq.getOrder() > 0 ? eq.getOrder() : mcOrder++;
                ParsedDocumentResponse.ParsedMcQuestion mc = buildParsedMcQuestion(eq, orderToUse);
                mcQuestions.add(mc);
            }
        }

        ParsedDocumentResponse response = ParsedDocumentResponse.builder()
                .extractedTitle(title)
                .suggestedDurationMinutes(durationMinutes)
                .multipleChoiceQuestions(mcQuestions)
                .essayQuestions(essayQuestions)
                .build();

        return filterResponseByTargetType(response, targetType);
    }

    /**
     * Parse văn bản và trả về DraftExamDto cho API /api/admin/exams/import/preview.
     */
    public DraftExamDto parseToDraftExamDto(String rawText, String fileName, ImportMode importMode) {
        String draftId = UUID.randomUUID().toString();
        String normalizedText = cleanAndNormalize(rawText);

        if (normalizedText.isBlank()) {
            return DraftExamDto.builder()
                    .draftId(draftId)
                    .fileName(fileName)
                    .title("Đề thi chưa có nội dung")
                    .totalQuestions(0)
                    .errors(List.of("File không chứa nội dung văn bản có thể trích xuất."))
                    .build();
        }

        String title = extractTitle(normalizedText, fileName);
        Map<Integer, String> answerKeyMap = extractAnswerKeyMap(normalizedText);
        List<ExtractedQuestion> extractedQuestions = extractAllQuestions(normalizedText, answerKeyMap);

        List<DraftQuestionDto> draftQuestions = new ArrayList<>();
        int qNumber = 1;

        for (ExtractedQuestion eq : extractedQuestions) {
            int orderToUse = eq.getOrder() > 0 ? eq.getOrder() : qNumber++;
            DraftQuestionDto draftQ = buildDraftQuestionDto(eq, orderToUse);
            draftQuestions.add(draftQ);
        }

        // Lọc theo ImportMode
        List<DraftQuestionDto> filtered = filterDraftQuestionsByMode(draftQuestions, importMode);

        int essayCount = (int) filtered.stream().filter(q -> q.getType() == DraftQuestionType.ESSAY).count();
        int mcCount = (int) filtered.stream().filter(q -> q.getType() == DraftQuestionType.MULTIPLE_CHOICE).count();
        int shortAnswerCount = (int) filtered.stream().filter(q -> q.getType() == DraftQuestionType.SHORT_ANSWER).count();
        int completedCount = (int) filtered.stream().filter(q -> q.getParsingStatus() == ParsingStatus.SUCCESS).count();
        int incompleteCount = filtered.size() - completedCount;

        List<String> warnings = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        if (filtered.isEmpty()) {
            errors.add("Không tìm thấy câu hỏi nào hợp lệ trong tệp tải lên.");
        } else {
            validateExamIntegrity(filtered, answerKeyMap, warnings);
        }

        return DraftExamDto.builder()
                .draftId(draftId)
                .fileName(fileName)
                .title(title)
                .totalQuestions(filtered.size())
                .essayCount(essayCount)
                .mcCount(mcCount)
                .shortAnswerCount(shortAnswerCount)
                .completedQuestions(completedCount)
                .incompleteQuestions(incompleteCount)
                .questions(filtered)
                .warnings(warnings)
                .errors(errors)
                .build();
    }

    /**
     * Chuẩn hóa Unicode NFC và làm sạch ký tự rác.
     */
    public String cleanAndNormalize(String rawText) {
        if (rawText == null || rawText.isBlank()) return "";

        String text = rawText
                .replace("ﬁ", "fi").replace("ﬂ", "fl").replace("ﬃ", "ffi").replace("ﬀ", "ff")
                .replace("Ⓐ.", "A.").replace("Ⓑ.", "B.").replace("Ⓒ.", "C.").replace("Ⓓ.", "D.").replace("Ⓔ.", "E.")
                .replace("Ⓐ", "A. ").replace("Ⓑ", "B. ").replace("Ⓒ", "C. ").replace("Ⓓ", "D. ").replace("Ⓔ", "E. ")
                .replace("ⓐ.", "A.").replace("ⓑ.", "B.").replace("ⓒ.", "C.").replace("ⓓ.", "D.").replace("ⓔ.", "E.")
                .replace("ⓐ", "A. ").replace("ⓑ", "B. ").replace("ⓒ", "C. ").replace("ⓓ", "D. ").replace("ⓔ", "E. ")
                .replace("A. .", "A.").replace("B. .", "B.").replace("C. .", "C.").replace("D. .", "D.").replace("E. .", "E.")
                .replace("①", "(1)").replace("②", "(2)").replace("③", "(3)").replace("④", "(4)").replace("⑤", "(5)").replace("⑥", "(6)")
                .replace("Ａ", "A").replace("Ｂ", "B").replace("Ｃ", "C").replace("Ｄ", "D").replace("Ｅ", "E")
                .replace("\u00A0", " ")
                .replace("\u200B", "")
                .replace("\uFEFF", "")
                .replace("\u00AD", "")
                .replace('\f', '\n')
                .replace("—", "-").replace("–", "-");

        text = Normalizer.normalize(text, Normalizer.Form.NFC);
        text = text.replace("\r\n", "\n").replace("\r", "\n");
        // Khử gãy từ do gạch nối cuối dòng (De-hyphenation)
        text = text.replaceAll("(?iu)(\\p{L}+)-\\s*\\n\\s*(\\p{Ll}+)\\b", "$1$2");
        return text;
    }

    /**
     * Trích xuất Tiêu đề đề thi thông minh.
     */
    public String extractTitle(String text, String fileName) {
        if (text == null || text.isBlank()) {
            if (fileName != null && !fileName.isBlank()) {
                String clean = fileName.replaceAll("(?i)\\.(pdf|docx|doc|xlsx|xls|txt)$", "");
                return clean.replace("-", " ").replace("_", " ").trim();
            }
            return "BÀI THI ĐÁNH GIÁ NĂNG LỰC CAND";
        }

        Pattern titlePattern = Pattern.compile(
                "(?imu)^(?:BỘ\\s+CÔNG\\s+AN[^\n]*\n)?(?:ĐỀ\\s+THI|ĐỀ\\s+SÁT\\s+HẠCH|BÀI\\s+THI|KIỂM\\s+TRA|KỲ\\s+THI|ĐỀ\\s+KIỂM\\s+TRA)[^\n]+"
        );
        Matcher matcher = titlePattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(0).trim().replaceAll("^[\\-\\s]+", "");
        }

        // Lấy dòng có ý nghĩa đầu tiên không phải header hành chính
        String[] lines = text.split("\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.length() > 8 &&
                !trimmed.startsWith("---") &&
                !trimmed.toLowerCase().contains("bộ công an") &&
                !trimmed.toLowerCase().contains("cộng hòa xã hội") &&
                !trimmed.toLowerCase().contains("độc lập - tự do") &&
                !trimmed.toLowerCase().contains("thời gian") &&
                !trimmed.toLowerCase().startsWith("phần")) {
                return trimmed;
            }
        }

        if (fileName != null && !fileName.isBlank()) {
            String clean = fileName.replaceAll("(?i)\\.(pdf|docx|doc|xlsx|xls|txt)$", "");
            return clean.replace("-", " ").replace("_", " ").trim();
        }
        return "BÀI THI ĐÁNH GIÁ NĂNG LỰC CAND";
    }

    /**
     * Trích xuất Thời gian làm bài (phút).
     */
    public int extractDurationMinutes(String text) {
        if (text == null || text.isBlank()) {
            return 60;
        }

        Pattern durationPattern = Pattern.compile(
                "(?iu)(?:Thời\\s*gian(?:\\s*làm\\s*bài)?|Thời\\s*lượng)[:\\s]*(\\d+)\\s*(?:phút|'|p)",
                Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = durationPattern.matcher(text);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException ignored) {}
        }
        return 60;
    }

    /**
     * Trích xuất Bảng đáp án ở cuối văn bản (nếu có): ví dụ 1. A  2. B  3. C ...
     * Hỗ trợ:
     * 1. Bảng ma trận 2 dòng (Dòng 1: Số câu 1 2 3..., Dòng 2: Đáp án A B C...).
     * 2. Bảng viết nén: 1A 2B 3C hoặc 1.A, 2.B, 3.C hoặc 1-A, 2-B.
     * 3. Hỗ trợ phương án từ A đến F.
     */
    public Map<Integer, String> extractAnswerKeyMap(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyMap();
        }

        Map<Integer, String> map = new HashMap<>();

        Matcher sectionMatcher = ANSWER_KEY_SECTION_HEADER.matcher(text);
        if (sectionMatcher.find()) {
            int answerSectionIndex = sectionMatcher.start();
            String answerSectionText = text.substring(answerSectionIndex);

            // 1. Thử bóc tách dạng bảng ma trận 2 dòng (Dòng số câu / Dòng chữ cái đáp án)
            Map<Integer, String> matrixMap = parseTwoRowMatrixAnswerTable(answerSectionText);
            map.putAll(matrixMap);

            // 2. Bóc tách dạng thông thường: 1.A, 1-A, Câu 1: A, 1:A hoặc viết liền 1A 2B 3C
            Pattern itemPattern = Pattern.compile(
                    "(?:(?:Câu\\s*)?(\\d+)[.:\\-_\\/\\s]*([A-F])\\b)|(?:(\\d+)\\s*-\\s*([A-F])\\b)|(?:\\b(\\d+)([A-F])\\b)",
                    Pattern.CASE_INSENSITIVE
            );
            Matcher im = itemPattern.matcher(answerSectionText);
            while (im.find()) {
                String numStr = im.group(1) != null ? im.group(1) : (im.group(3) != null ? im.group(3) : im.group(5));
                String ans = im.group(2) != null ? im.group(2) : (im.group(4) != null ? im.group(4) : im.group(6));
                if (numStr != null && ans != null) {
                    try {
                        int qNum = Integer.parseInt(numStr);
                        if (!map.containsKey(qNum)) {
                            map.put(qNum, ans.toUpperCase());
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        return map;
    }

    /**
     * Bóc tách bảng đáp án ma trận 2 dòng:
     * Câu  1  2  3  4  5
     * Đ/A  A  B  C  D  A
     */
    private Map<Integer, String> parseTwoRowMatrixAnswerTable(String text) {
        Map<Integer, String> result = new HashMap<>();
        String[] lines = text.split("\n");

        for (int i = 0; i < lines.length - 1; i++) {
            String line1 = lines[i].trim();
            String line2 = lines[i + 1].trim();

            List<Integer> numbers = extractNumbersFromLine(line1);
            List<String> letters = extractAnswerLettersFromLine(line2);

            if (numbers.size() >= 3 && letters.size() >= 3 && Math.abs(numbers.size() - letters.size()) <= 1) {
                int count = Math.min(numbers.size(), letters.size());
                for (int k = 0; k < count; k++) {
                    result.put(numbers.get(k), letters.get(k));
                }
                i++; // Đã xử lý cặp dòng này
            }
        }
        return result;
    }

    private List<Integer> extractNumbersFromLine(String line) {
        List<Integer> list = new ArrayList<>();
        Pattern p = Pattern.compile("\\b(\\d{1,3})\\b");
        Matcher m = p.matcher(line);
        while (m.find()) {
            try {
                int num = Integer.parseInt(m.group(1));
                list.add(num);
            } catch (NumberFormatException ignored) {}
        }
        return list;
    }

    private List<String> extractAnswerLettersFromLine(String line) {
        List<String> list = new ArrayList<>();
        String clean = line.replaceAll("(?iu)^\\s*(?:Đ\\/?A|Đáp\\s*án|Key|Chọn|Phương\\s*án)[:\\s|]*", "");
        Pattern p = Pattern.compile("\\b([A-F])\\b", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(clean);
        while (m.find()) {
            list.add(m.group(1).toUpperCase());
        }
        return list;
    }

    /**
     * Bóc tách toàn bộ câu hỏi và tình huống trong văn bản đề thi.
     */
    private List<ExtractedQuestion> extractAllQuestions(String text, Map<Integer, String> answerKeyMap) {
        // Cắt bỏ phần Bảng đáp án ở cuối để không bị parse nhầm thành câu hỏi
        String mainContent = text;
        Matcher answerSecMatcher = ANSWER_KEY_SECTION_HEADER.matcher(text);
        if (answerSecMatcher.find()) {
            mainContent = text.substring(0, answerSecMatcher.start()).trim();
        }

        String[] lines = mainContent.split("\n");

        List<ExtractedQuestion> result = new ArrayList<>();
        DraftQuestionType currentSectionType = DraftQuestionType.MULTIPLE_CHOICE;
        String currentSectionTitle = "TRẮC NGHIỆM";
        BigDecimal currentSectionMaxScore = null;
        boolean inSituationalSection = false;

        int currentOrder = -1;
        List<String> currentBlockLines = new ArrayList<>();

        // Các biến cục bộ quản lý bối cảnh tình huống (Thread-safe)
        String pendingContext = null;
        String activeSituationalContext = null;
        int situationalStartOrder = -1;
        int situationalEndOrder = -1;

        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty()) continue;
            if (isFooterOrHeaderGarbage(line)) continue;

            // Kiểm tra dải câu dạng chỉ-dòng-dải (ví dụ: "Câu 43 đến câu 47", "(Từ câu 43 đến câu 47)", "Áp dụng cho các câu từ 43 đến 47")
            int[] standaloneRange = extractSituationalRange(line);
            if (isRangeOnlyLine(line, standaloneRange)) {
                if (currentOrder != -1 && !currentBlockLines.isEmpty()) {
                    String ctxToPass = getSituationalContextForOrder(activeSituationalContext, situationalStartOrder, situationalEndOrder, currentOrder);
                    result.add(parseRawBlock(currentOrder, currentBlockLines, currentSectionType, currentSectionTitle, ctxToPass, currentSectionMaxScore, answerKeyMap));
                    currentBlockLines.clear();
                }
                currentOrder = -1;
                inSituationalSection = true;

                int newStart = standaloneRange[0];
                int newEnd = standaloneRange[1];
                boolean isSameRange = (newStart == situationalStartOrder && newEnd == situationalEndOrder);

                // Nếu có hai dải khác nhau xuất hiện liên tiếp trước câu hỏi đầu tiên (Dạng test 25):
                // Reset pendingContext cũ để nhận context mới
                if (situationalStartOrder != -1 && !isSameRange && currentBlockLines.isEmpty() && result.isEmpty()) {
                    pendingContext = null;
                    activeSituationalContext = null;
                }

                situationalStartOrder = newStart;
                situationalEndOrder = newEnd;
                if (!isSameRange) {
                    activeSituationalContext = null;
                }

                if (pendingContext != null && !pendingContext.isBlank()) {
                    activeSituationalContext = pendingContext;
                    pendingContext = null;
                }
                continue;
            }

            // 1. Kiểm tra Section Header (Bài tập tình huống)
            boolean isQuestionHeader = QUESTION_HEADER_PATTERN.matcher(line).matches();
            boolean isEssayContextLabel = currentSectionType == DraftQuestionType.ESSAY && LABELED_SITUATIONAL_CONTEXT_LINE.matcher(line).matches();
            boolean isSituationalHeader = !isQuestionHeader && !isEssayContextLabel && SITUATIONAL_SECTION_HEADER.matcher(line).matches();

            if (isSituationalHeader) {
                if (currentOrder != -1 && !currentBlockLines.isEmpty()) {
                    String ctxToPass = getSituationalContextForOrder(activeSituationalContext, situationalStartOrder, situationalEndOrder, currentOrder);
                    result.add(parseRawBlock(currentOrder, currentBlockLines, currentSectionType, currentSectionTitle, ctxToPass, currentSectionMaxScore, answerKeyMap));
                    currentBlockLines.clear();
                }
                currentSectionType = DraftQuestionType.MULTIPLE_CHOICE;
                currentSectionTitle = line;
                currentOrder = -1;
                inSituationalSection = true;

                int[] inlineRange = extractSituationalRange(line);
                boolean isRepeatedHeaderForSameRange = inlineRange != null && inlineRange[0] == situationalStartOrder && inlineRange[1] == situationalEndOrder;

                if (!isRepeatedHeaderForSameRange && (situationalEndOrder == -1 || (currentOrder != -1 && currentOrder >= situationalEndOrder))) {
                    pendingContext = null;
                    activeSituationalContext = null;
                    situationalStartOrder = -1;
                    situationalEndOrder = -1;
                } else if (inlineRange == null && activeSituationalContext != null && situationalEndOrder != -1) {
                    pendingContext = null;
                } else {
                    pendingContext = null;
                    activeSituationalContext = null;
                    situationalStartOrder = -1;
                    situationalEndOrder = -1;
                }

                if (inlineRange != null) {
                    situationalStartOrder = inlineRange[0];
                    situationalEndOrder = inlineRange[1];
                }
                currentSectionMaxScore = null;
                continue;
            }

            // 2. Kiểm tra dòng chứa thông tin dải câu hỏi tình huống: "(Từ câu 43 đến câu 47)",...
            Matcher rangeMatcher = SITUATIONAL_RANGE_PATTERN.matcher(line);
            if (rangeMatcher.find()) {
                int[] range = extractSituationalRange(line);
                if (range != null) {
                    if (currentOrder != -1 && !currentBlockLines.isEmpty()) {
                        String ctxToPass = getSituationalContextForOrder(activeSituationalContext, situationalStartOrder, situationalEndOrder, currentOrder);
                        result.add(parseRawBlock(currentOrder, currentBlockLines, currentSectionType, currentSectionTitle, ctxToPass, currentSectionMaxScore, answerKeyMap));
                        currentBlockLines.clear();
                    }
                    currentOrder = -1;
                    inSituationalSection = true;

                    int newStart = range[0];
                    int newEnd = range[1];
                    boolean isSameRange = (newStart == situationalStartOrder && newEnd == situationalEndOrder);

                    situationalStartOrder = newStart;
                    situationalEndOrder = newEnd;
                    if (!isSameRange) {
                        activeSituationalContext = null;
                    }

                    if (pendingContext != null && !pendingContext.isBlank()) {
                        activeSituationalContext = pendingContext;
                        pendingContext = null;
                    }

                    if (SITUATIONAL_LEAD_IN_PATTERN.matcher(line).matches()) {
                        continue;
                    }
                }
            }

            // 3. Kiểm tra dòng chỉ dẫn tình huống
            if (SITUATIONAL_PROMPT_HEADER.matcher(line).matches()) {
                if (currentOrder != -1 && !currentBlockLines.isEmpty()) {
                    String ctxToPass = getSituationalContextForOrder(activeSituationalContext, situationalStartOrder, situationalEndOrder, currentOrder);
                    result.add(parseRawBlock(currentOrder, currentBlockLines, currentSectionType, currentSectionTitle, ctxToPass, currentSectionMaxScore, answerKeyMap));
                    currentBlockLines.clear();
                }
                currentOrder = -1;
                inSituationalSection = true;

                int[] pRange = extractSituationalRange(line);
                if (pRange != null) {
                    boolean isSameRange = (pRange[0] == situationalStartOrder && pRange[1] == situationalEndOrder);
                    situationalStartOrder = pRange[0];
                    situationalEndOrder = pRange[1];
                    if (!isSameRange) {
                        activeSituationalContext = null;
                    }
                    if (pendingContext != null && !pendingContext.isBlank()) {
                        activeSituationalContext = pendingContext;
                        pendingContext = null;
                    }
                }
                continue;
            }

            // 4. Kiểm tra các Section Header khác để đóng câu hỏi cũ an toàn
            if (ESSAY_SECTION_HEADER.matcher(line).matches()) {
                if (currentOrder != -1 && !currentBlockLines.isEmpty()) {
                    String ctxToPass = getSituationalContextForOrder(activeSituationalContext, situationalStartOrder, situationalEndOrder, currentOrder);
                    result.add(parseRawBlock(currentOrder, currentBlockLines, currentSectionType, currentSectionTitle, ctxToPass, currentSectionMaxScore, answerKeyMap));
                    currentBlockLines.clear();
                }
                currentSectionType = DraftQuestionType.ESSAY;
                currentSectionTitle = line;
                currentOrder = -1;
                inSituationalSection = false;
                pendingContext = null;
                activeSituationalContext = null;
                situationalStartOrder = -1;
                situationalEndOrder = -1;
                currentSectionMaxScore = extractScoreFromText(line, BigDecimal.valueOf(30.0));
                continue;
            }

            if (SHORT_ANSWER_SECTION_HEADER.matcher(line).matches()) {
                if (currentOrder != -1 && !currentBlockLines.isEmpty()) {
                    String ctxToPass = getSituationalContextForOrder(activeSituationalContext, situationalStartOrder, situationalEndOrder, currentOrder);
                    result.add(parseRawBlock(currentOrder, currentBlockLines, currentSectionType, currentSectionTitle, ctxToPass, currentSectionMaxScore, answerKeyMap));
                    currentBlockLines.clear();
                }
                currentSectionType = DraftQuestionType.SHORT_ANSWER;
                currentSectionTitle = line;
                currentOrder = -1;
                inSituationalSection = false;
                pendingContext = null;
                activeSituationalContext = null;
                situationalStartOrder = -1;
                situationalEndOrder = -1;
                currentSectionMaxScore = null;
                continue;
            }

            if (MCQ_SECTION_HEADER.matcher(line).matches() && !line.toLowerCase().contains("trả lời ngắn")) {
                if (currentOrder != -1 && !currentBlockLines.isEmpty()) {
                    String ctxToPass = getSituationalContextForOrder(activeSituationalContext, situationalStartOrder, situationalEndOrder, currentOrder);
                    result.add(parseRawBlock(currentOrder, currentBlockLines, currentSectionType, currentSectionTitle, ctxToPass, currentSectionMaxScore, answerKeyMap));
                    currentBlockLines.clear();
                }
                currentSectionType = DraftQuestionType.MULTIPLE_CHOICE;
                currentSectionTitle = line;
                currentOrder = -1;
                if (situationalEndOrder == -1) {
                    inSituationalSection = false;
                    pendingContext = null;
                    activeSituationalContext = null;
                    situationalStartOrder = -1;
                }
                currentSectionMaxScore = null;
                continue;
            }

            // 5. Kiểm tra dòng bắt đầu câu hỏi mới ("Câu X.", "1.", "Bài tập 1:")
            Matcher qMatcher = QUESTION_HEADER_PATTERN.matcher(line);
            if (qMatcher.matches()) {
                String numStr = null;
                for (int i = 1; i <= 6; i++) {
                    if (qMatcher.group(i) != null) {
                        numStr = qMatcher.group(i);
                        break;
                    }
                }

                int parsedOrder;
                try {
                    parsedOrder = numStr != null ? Integer.parseInt(numStr) : (result.size() + 1);
                } catch (NumberFormatException e) {
                    parsedOrder = result.size() + 1;
                }

                // Nếu là số trần không có chữ "Câu", chỉ chấp nhận trong phần trắc nghiệm ngoài bài đọc tình huống
                boolean isBareNumber = (qMatcher.group(6) != null);
                if (isBareNumber) {
                    boolean validBareQuestion = (currentSectionType == DraftQuestionType.MULTIPLE_CHOICE)
                            && !inSituationalSection
                            && ((parsedOrder == 1 && result.isEmpty()) || (currentOrder != -1 && parsedOrder == currentOrder + 1) || (parsedOrder == result.size() + 1));
                    if (!validBareQuestion) {
                        if (currentOrder != -1) {
                            currentBlockLines.add(line);
                        } else if (inSituationalSection || situationalStartOrder != -1) {
                            pendingContext = (pendingContext == null || pendingContext.isBlank()) ? line : (pendingContext + "\n" + line);
                        }
                        continue;
                    }
                }

                if (currentOrder != -1 && !currentBlockLines.isEmpty()) {
                    String ctxToPass = getSituationalContextForOrder(activeSituationalContext, situationalStartOrder, situationalEndOrder, currentOrder);
                    result.add(parseRawBlock(currentOrder, currentBlockLines, currentSectionType, currentSectionTitle, ctxToPass, currentSectionMaxScore, answerKeyMap));
                    currentBlockLines.clear();
                }

                if (currentOrder == -1) {
                    // Chỉ kích hoạt pendingContext khi đang trong phần tình huống và chưa có context cho câu này
                    if ((inSituationalSection || situationalStartOrder != -1) && pendingContext != null && !pendingContext.isBlank()) {
                        boolean isWithinExistingScope = (activeSituationalContext != null && situationalStartOrder != -1 && parsedOrder >= situationalStartOrder && parsedOrder <= situationalEndOrder);
                        if (!isWithinExistingScope) {
                            activeSituationalContext = pendingContext;
                        }
                        pendingContext = null;
                    }
                }

                currentOrder = parsedOrder;

                // Nếu số câu hỏi vượt quá phạm vi tình huống đã định nghĩa, tự động giải phóng bối cảnh
                if (situationalEndOrder != -1 && currentOrder > situationalEndOrder) {
                    activeSituationalContext = null;
                    situationalStartOrder = -1;
                    situationalEndOrder = -1;
                    inSituationalSection = false;
                }

                currentBlockLines.add(line);
                continue;
            }

            // 6. Xử lý phần tự luận không có tiền tố "Câu X"
            if (currentSectionType == DraftQuestionType.ESSAY && currentOrder == -1) {
                currentOrder = result.size() + 1;
                currentBlockLines.add(line);
                continue;
            }

            // 7. Thu thập phần bối cảnh tình huống trước khi câu hỏi đầu tiên xuất hiện
            if (currentOrder == -1) {
                if (inSituationalSection || situationalStartOrder != -1) {
                    pendingContext = (pendingContext == null || pendingContext.isBlank()) ? line : (pendingContext + "\n" + line);
                }
                continue;
            }

            // 8. Nếu đang trong một câu hỏi -> gom dòng câu hỏi
            if (currentOrder != -1) {
                currentBlockLines.add(line);
            }
        }

        // Đóng câu hỏi cuối cùng của tài liệu
        if (currentOrder != -1 && !currentBlockLines.isEmpty()) {
            String ctxToPass = getSituationalContextForOrder(activeSituationalContext, situationalStartOrder, situationalEndOrder, currentOrder);
            result.add(parseRawBlock(currentOrder, currentBlockLines, currentSectionType, currentSectionTitle, ctxToPass, currentSectionMaxScore, answerKeyMap));
        }

        return result;
    }

    private boolean isRangeOnlyLine(String line, int[] range) {
        if (range == null || line == null) return false;
        String stripped = line.replaceAll("(?iu)^\\s*[\\(\\[]?\\s*(?:(?:Áp\\s*dụng\\s*cho|Dùng\\s*cho|Trả\\s*lời|Đọc|Dựa\\s*vào)\\s+)?(?:(?:các\\s+)?câu(?:\\s+hỏi)?\\s*)?(?:Từ\\s+)?(?:câu(?:\\s+hỏi)?\\s*)?(?:số\\s*)?\\d+\\s*(?:đến|tới|[-–—])\\s*(?:câu(?:\\s+hỏi)?\\s*)?(?:số\\s*)?\\d+\\s*[\\]\\)]?\\s*[:;.]?\\s*$", "").trim();
        return stripped.isEmpty();
    }

    private int[] extractSituationalRange(String line) {
        Matcher matcher = SITUATIONAL_RANGE_PATTERN.matcher(line);
        if (!matcher.find()) return null;

        try {
            int startOrder = Integer.parseInt(matcher.group(1));
            int endOrder = Integer.parseInt(matcher.group(2));
            return startOrder > 0 && startOrder <= endOrder
                    ? new int[]{startOrder, endOrder}
                    : null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public static String normalizeParagraphFlow(String text) {
        if (text == null || text.isBlank()) return "";
        String normalized = text.replace("\r\n", "\n").replace("\r", "\n");
        // Nối các dòng bẻ ngắt giữa chừng do lề trang PDF thành một dòng chảy liền mạch
        normalized = normalized.replaceAll("(?m)([^\\n.?!:;])\\n(?!\\n|[•\\-\\*]|\\d+\\.)", "$1 ");
        // Chuẩn hóa khoảng trắng dư thừa
        normalized = normalized.replaceAll("[ \\t]+", " ").trim();
        return normalized;
    }

    private String appendContext(String currentContext, String passage) {
        if (passage == null || passage.isBlank()) return currentContext;
        return currentContext == null || currentContext.isBlank()
                ? passage
                : currentContext + "\n" + passage;
    }

    private String getSituationalContextForOrder(String activeContext,
                                                  int startOrder,
                                                  int endOrder,
                                                  int questionOrder) {
        if (activeContext == null || activeContext.isBlank()) return null;
        String normalized = normalizeParagraphFlow(activeContext);
        if (startOrder > 0 && endOrder >= startOrder) {
            return (questionOrder >= startOrder && questionOrder <= endOrder) ? normalized : null;
        }
        return normalized;
    }

    private BigDecimal extractScoreFromText(String text, BigDecimal defaultScore) {
        if (text == null) return defaultScore;
        Matcher matcher = ESSAY_SCORE_PATTERN.matcher(text);
        if (matcher.find()) {
            for (int i = 1; i <= 3; i++) {
                if (matcher.group(i) != null) {
                    try {
                        return new BigDecimal(matcher.group(i).replace(",", "."));
                    } catch (Exception ignored) {}
                }
            }
        }
        return defaultScore;
    }

    /**
     * Bóc tách thông minh một khối câu hỏi (Multiple Choice, Short Answer hoặc Essay).
     */
    private ExtractedQuestion parseRawBlock(int order,
                                            List<String> rawLines,
                                            DraftQuestionType sectionType,
                                            String sectionTitle,
                                            String sectionContext,
                                            BigDecimal sectionMaxScore,
                                            Map<Integer, String> answerKeyMap) {

        // Xử lý đặc biệt cho câu hỏi dài nhiều dòng
        String combinedText = handleLongQuestionLines(rawLines);

        // 1. Tách các metadata: Đáp án, Giải thích, Căn cứ pháp lý
        String detectedAnswer = null;
        String detectedExplanation = null;
        String detectedLegalRef = null;

        Matcher ansMatcher = INLINE_ANSWER_PATTERN.matcher(combinedText);
        if (ansMatcher.find()) {
            detectedAnswer = ansMatcher.group(1).toUpperCase();
        }

        Matcher expMatcher = INLINE_EXPLANATION_PATTERN.matcher(combinedText);
        if (expMatcher.find()) {
            detectedExplanation = expMatcher.group(1).trim();
        }

        Matcher refMatcher = INLINE_REFERENCE_PATTERN.matcher(combinedText);
        if (refMatcher.find()) {
            detectedLegalRef = refMatcher.group(1).trim();
        }

        // Làm sạch các dòng metadata khỏi thân câu hỏi
        List<String> cleanedLines = new ArrayList<>();
        for (String line : rawLines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;
            if (isFooterOrHeaderGarbage(trimmed)) continue;
            if (INLINE_ANSWER_PATTERN.matcher(trimmed).find() ||
                INLINE_EXPLANATION_PATTERN.matcher(trimmed).matches() ||
                INLINE_REFERENCE_PATTERN.matcher(trimmed).matches()) {
                continue;
            }
            cleanedLines.add(trimmed);
        }

        String bodyText = cleanGarbageFooters(String.join("\n", cleanedLines).trim());

        // 2. Nếu là PHẦN TRẮC NGHIỆM TRẢ LỜI NGẮN (SHORT_ANSWER):
        // Theo chuẩn đề thi CAND, phần này là trắc nghiệm khách quan dạng trả lời ngắn,
        // BẮT BUỘC đưa vào multipleChoiceQuestions với options = []
        if (sectionType == DraftQuestionType.SHORT_ANSWER) {
            String finalAnswer = detectedAnswer != null ? detectedAnswer : answerKeyMap.get(order);
            return ExtractedQuestion.builder()
                    .order(order)
                    .isEssay(false)
                    .isShortAnswer(true)
                    .sectionTitle(sectionTitle)
                    .context(sectionContext)
                    .questionText(bodyText)
                    .options(Collections.emptyList())
                    .correctAnswer(finalAnswer != null ? finalAnswer : "")
                    .explanation(detectedExplanation != null ? detectedExplanation : (detectedLegalRef != null ? "Căn cứ " + detectedLegalRef : "Căn cứ quy định pháp luật."))
                    .legalReference(detectedLegalRef != null ? detectedLegalRef : "Quy định pháp luật")
                    .build();
        }

        // 3. Nếu là PHẦN TỰ LUẬN (ESSAY):
        if (sectionType == DraftQuestionType.ESSAY) {
            ParsedEssayDetails essayDetails = parseEssayDetails(bodyText, order, sectionContext, sectionMaxScore);

            return ExtractedQuestion.builder()
                    .order(order)
                    .isEssay(true)
                    .isShortAnswer(false)
                    .sectionTitle(sectionTitle)
                    .context(sectionContext)
                    .essayTitle(essayDetails.title)
                    .essayContext(essayDetails.context)
                    .essayPrompt(essayDetails.prompt)
                    .essayMaxScore(essayDetails.maxScore)
                    .essayRubrics(essayDetails.rubrics)
                    .correctAnswer(detectedAnswer)
                    .explanation(detectedExplanation)
                    .legalReference(detectedLegalRef)
                    .build();
        }

        // 4. Nếu là TRẮC NGHIỆM (MULTIPLE CHOICE):
        // Xử lý đặc biệt cho câu hỏi dài nhiều dòng
        String enhancedBodyText = enhanceLongQuestionText(bodyText);
        ParsedOptionsResult optionsResult = parseOptionsFromText(enhancedBodyText, order);

        // Cải thiện: Nếu parse được options, đảm bảo phân loại là MC ngay cả khi có từ "tình huống"
        String questionText = optionsResult.questionText.isBlank() ? bodyText : optionsResult.questionText;
        questionText = cleanGarbageFooters(questionText);

        // Xác định đáp án đúng
        String finalAnswer = detectedAnswer;
        if (finalAnswer == null && optionsResult.correctLabelFromMarker != null) {
            finalAnswer = optionsResult.correctLabelFromMarker;
        }
        if (finalAnswer == null && answerKeyMap.containsKey(order)) {
            finalAnswer = answerKeyMap.get(order);
        }
        if (finalAnswer == null) {
            finalAnswer = "A"; // Mặc định A nếu không tìm thấy
        }

        return ExtractedQuestion.builder()
                .order(order)
                .isEssay(false)
                .isShortAnswer(false)
                .sectionTitle(sectionTitle)
                .context(sectionContext)
                .questionText(questionText)
                .options(optionsResult.options)
                .correctAnswer(finalAnswer)
                .explanation(detectedExplanation != null ? detectedExplanation : (detectedLegalRef != null ? "Căn cứ " + detectedLegalRef : "Căn cứ quy định pháp luật CAND."))
                .legalReference(detectedLegalRef != null ? detectedLegalRef : "Quy định nghiệp vụ CAND")
                .build();
    }

    /**
     * Bóc tách các phương án A, B, C, D thông minh.
     * Giải quyết triệt để lỗi "dính chùm" khi nhiều options nằm trên 1 dòng hoặc nối tiếp nhiều dòng.
     */
    public ParsedOptionsResult parseOptionsFromText(String text, int questionOrder) {
        List<ParsedDocumentResponse.ParsedOption> options = new ArrayList<>();
        String correctLabelFromMarker = null;

        // 1. Tìm tất cả các vị trí xuất hiện của marker phương án: A., B., C., D., E., F. hoặc A), B), ...
        List<OptionTokenMatch> matches = new ArrayList<>();
        Matcher matcher = OPTION_TOKEN_PATTERN.matcher(text);

        while (matcher.find()) {
            String label = null;
            boolean isStarred = false;

            if (matcher.group(2) != null) {
                label = matcher.group(2);
                isStarred = (matcher.group(1) != null && !matcher.group(1).isEmpty())
                        || (matcher.group(3) != null && !matcher.group(3).isEmpty())
                        || (matcher.group(4) != null && !matcher.group(4).isEmpty());
            } else if (matcher.group(5) != null) {
                label = matcher.group(5);
                isStarred = (matcher.group(6) != null && !matcher.group(6).isEmpty());
            } else if (matcher.group(7) != null) {
                label = matcher.group(7);
                isStarred = (matcher.group(8) != null && !matcher.group(8).isEmpty());
            }

            if (label != null) {
                matches.add(new OptionTokenMatch(
                        matcher.start(),
                        matcher.end(),
                        label.toUpperCase(),
                        isStarred
                ));
            }
        }

        // 2. Lọc chuỗi token theo đúng thứ tự logic tăng dần (A -> B -> C -> D)
        // Tránh bị nhầm bởi các từ viết tắt như "GS. TS. Nguyễn Văn A. Người này..."
        List<OptionTokenMatch> validSequence = filterAscendingOptionSequence(matches);

        if (validSequence != null && validSequence.size() >= 2) {
            // Cắt phần nội dung câu hỏi (nằm trước option đầu tiên)
            OptionTokenMatch firstOption = validSequence.get(0);
            int firstOptionStart = firstOption != null ? firstOption.startIndex : 0;
            String rawQuestionText = text.substring(0, firstOptionStart).trim();
            String questionText = rawQuestionText.replaceAll("(?imu)^\\s*(?:Câu\\s+\\d+|Tình\\s+huống\\s+\\d+|Bài\\s+tập\\s+\\d+|Câu\\s+hỏi\\s+\\d+)\\s*(?:\\((?!\\d+\\))[^)]*\\)|\\[[^\\]]*\\])?\\s*[.:\\-\\)]?\\s*", "").trim();
            if (questionText.isBlank()) {
                questionText = rawQuestionText;
            }

            for (int i = 0; i < validSequence.size(); i++) {
                OptionTokenMatch current = validSequence.get(i);
                int contentStart = current.endIndex;
                int contentEnd = (i + 1 < validSequence.size()) ? validSequence.get(i + 1).startIndex : text.length();

                String rawOptionContent = text.substring(contentStart, contentEnd).trim();

                // Kiểm tra xem phương án có đánh dấu đúng không: (Đúng), [x], hoặc có dấu *
                boolean isMarkedCorrect = current.isStarred ||
                        rawOptionContent.toLowerCase().endsWith("(đúng)") ||
                        rawOptionContent.toLowerCase().endsWith("(đáp án đúng)") ||
                        rawOptionContent.startsWith("[x]");

                if (isMarkedCorrect && correctLabelFromMarker == null) {
                    correctLabelFromMarker = current.label;
                }

                // Làm sạch nội dung phương án
                String cleanContent = cleanOptionText(rawOptionContent);

                options.add(ParsedDocumentResponse.ParsedOption.builder()
                        .id("opt-" + questionOrder + "-" + current.label.toLowerCase())
                        .label(current.label)
                        .text(cleanContent)
                        .build());
            }

            return new ParsedOptionsResult(questionText, options, correctLabelFromMarker);
        }

        // Fallback: Nếu không match được bằng sequence regex, thử split từng dòng nếu có dòng bắt đầu bằng A. / B. / C. / D.
        return fallbackLineBasedOptionParser(text, questionOrder);
    }

    /**
     * Lọc danh sách token sao cho các phương án tuân theo đúng thứ tự A -> B -> C -> D.
     */
    private List<OptionTokenMatch> filterAscendingOptionSequence(List<OptionTokenMatch> tokens) {
        if (tokens.isEmpty()) return Collections.emptyList();

        List<OptionTokenMatch> bestSequence = new ArrayList<>();

        for (int startIndex = 0; startIndex < tokens.size(); startIndex++) {
            OptionTokenMatch first = tokens.get(startIndex);
            if (!"A".equalsIgnoreCase(first.label)) {
                continue; // Phương án đầu tiên phải là A
            }

            List<OptionTokenMatch> currentSeq = new ArrayList<>();
            currentSeq.add(first);
            char expectedChar = 'B';

            for (int j = startIndex + 1; j < tokens.size(); j++) {
                OptionTokenMatch candidate = tokens.get(j);
                char candidateChar = candidate.label.charAt(0);

                if (candidateChar == expectedChar) {
                    currentSeq.add(candidate);
                    expectedChar++;
                    if (expectedChar > 'F') {
                        break; // Đã tìm tối đa A, B, C, D, E, F
                    }
                }
            }

            if (currentSeq.size() > bestSequence.size()) {
                bestSequence = currentSeq;
                if (bestSequence.size() >= 4) break;
            }
        }

        return bestSequence;
    }

    /**
     * Parser dự phòng theo từng dòng kết hợp tách inline options.
     */
    private ParsedOptionsResult fallbackLineBasedOptionParser(String text, int questionOrder) {
        List<ParsedDocumentResponse.ParsedOption> options = new ArrayList<>();
        String[] lines = text.split("\n");
        StringBuilder questionBuilder = new StringBuilder();
        String correctLabel = null;

        Pattern lineOptPattern = Pattern.compile("^\\s*([A-F])[.:\\-\\)]\\s*(.*)$");

        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty()) continue;

            Matcher m = lineOptPattern.matcher(line);
            if (m.matches()) {
                String firstKey = m.group(1).toUpperCase();
                String rest = m.group(2).trim();

                // Kiểm tra xem dòng này có chứa thêm B., C., D., E., F. inline không
                List<InlinePiece> pieces = splitInlinePieces(firstKey, rest);
                for (InlinePiece piece : pieces) {
                    options.add(ParsedDocumentResponse.ParsedOption.builder()
                            .id("opt-" + questionOrder + "-" + piece.label.toLowerCase())
                            .label(piece.label)
                            .text(cleanOptionText(piece.text))
                            .build());
                }
            } else {
                if (options.isEmpty()) {
                    if (!questionBuilder.isEmpty()) questionBuilder.append(" ");
                    questionBuilder.append(line);
                } else {
                    // Nối vào option cuối cùng
                    ParsedDocumentResponse.ParsedOption last = options.get(options.size() - 1);
                    last.setText(last.getText() + " " + line);
                }
            }
        }

        return new ParsedOptionsResult(questionBuilder.toString().trim(), options, correctLabel);
    }

    private List<InlinePiece> splitInlinePieces(String firstKey, String text) {
        List<InlinePiece> pieces = new ArrayList<>();

        // Tìm các marker B., C., D., E., F. phía sau firstKey
        Pattern subMarkerPattern = Pattern.compile("(?:\\s{2,}|\\t|(?<=[.;]\\s))([B-F])[.:\\-\\)]\\s*");
        Matcher sm = subMarkerPattern.matcher(text);

        int lastIndex = 0;
        String currentLabel = firstKey;

        while (sm.find()) {
            String pieceText = text.substring(lastIndex, sm.start()).trim();
            pieces.add(new InlinePiece(currentLabel, pieceText));
            currentLabel = sm.group(1).toUpperCase();
            lastIndex = sm.end();
        }

        String remainingText = text.substring(lastIndex).trim();
        pieces.add(new InlinePiece(currentLabel, remainingText));

        return pieces;
    }

    private String cleanOptionText(String raw) {
        if (raw == null) return "";
        String cleaned = raw.replaceAll("(?iu)\\s*\\((?:đúng|đáp\\s*án\\s*đúng)\\)\\s*$", "")
                .replaceAll("(?iu)\\s*(?:\\*?\\s*(?:Đáp\\s*án(?:\\s*đúng)?|Chọn(?:\\s*đáp\\s*án)?|ĐA|Đ\\/a|Key)[:\\s]*[A-F]\\b.*)$", "")
                .replaceAll("^\\[x\\]\\s*", "")
                .replaceAll("\\s+", " ")
                .trim();
        // Nếu nội dung phương án chỉ là một con số hợp lệ (ví dụ: 1, 2, 3, 4, 15, 100 hoặc (1)), trả về ngay
        if (NUMERIC_VALUE_LINE_PATTERN.matcher(cleaned).matches()) {
            return cleaned;
        }
        return cleanGarbageFooters(cleaned);
    }

    private static boolean shouldStartOnNewLine(String trimmed) {
        if (trimmed == null || trimmed.isEmpty()) return false;
        // Nhận định đánh số: (1), (2), 1., 1), 1:
        if (NUMBERED_ASSERTION_START_PATTERN.matcher(trimmed).matches()) {
            return true;
        }
        // Gạch đầu dòng
        if (BULLET_POINT_PATTERN.matcher(trimmed).matches()) {
            return true;
        }
        // Câu hỏi kết luận hoặc dẫn đề
        if (LEADING_QUESTION_PROMPT_PATTERN.matcher(trimmed).matches()) {
            return true;
        }
        return false;
    }

    /**
     * Cải thiện xử lý câu hỏi dài nhiều dòng
     * Gom nhóm các dòng thuộc về câu hỏi và tách biệt với các phương án
     */
    private String enhanceLongQuestionText(String text) {
        if (text == null || text.isBlank()) return text;

        String[] lines = text.split("\n");
        StringBuilder result = new StringBuilder();
        boolean inOptions = false;
        boolean lastLineEndedWithColon = false;

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;
            if (isFooterOrHeaderGarbage(trimmed)) continue;

            // Kiểm tra xem dòng này có phải bắt đầu một phương án không
            if (OPTION_PREFIX_PATTERN.matcher(trimmed).matches()) {
                inOptions = true;
            }

            if (inOptions) {
                if (result.length() > 0) {
                    result.append("\n");
                }
                result.append(trimmed);
            } else {
                if (result.length() == 0) {
                    result.append(trimmed);
                } else if (lastLineEndedWithColon || shouldStartOnNewLine(trimmed)) {
                    result.append("\n").append(trimmed);
                } else {
                    result.append(" ").append(trimmed);
                }
                lastLineEndedWithColon = trimmed.endsWith(":");
            }
        }

        return result.toString();
    }

    /**
     * Xử lý đặc biệt cho câu hỏi dài nhiều dòng trong rawLines
     * Gom nhóm các dòng thuộc câu hỏi trước khi parse
     */
    private String handleLongQuestionLines(List<String> rawLines) {
        if (rawLines == null || rawLines.isEmpty()) return "";

        StringBuilder result = new StringBuilder();
        boolean inOptions = false;
        boolean lastLineEndedWithColon = false;

        for (String line : rawLines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;
            if (isFooterOrHeaderGarbage(trimmed)) continue;

            // Kiểm tra xem dòng này có phải bắt đầu một phương án không
            if (OPTION_PREFIX_PATTERN.matcher(trimmed).matches()) {
                inOptions = true;
            }

            // Gom nhóm các dòng, nhưng khi vào phần options thì vẫn giữ nguyên format
            if (inOptions) {
                if (result.length() > 0) {
                    result.append("\n");
                }
                result.append(trimmed);
            } else {
                if (result.length() == 0) {
                    result.append(trimmed);
                } else if (lastLineEndedWithColon || shouldStartOnNewLine(trimmed)) {
                    result.append("\n").append(trimmed);
                } else {
                    result.append(" ").append(trimmed);
                }
                lastLineEndedWithColon = trimmed.endsWith(":");
            }
        }

        return result.toString();
    }

    /**
     * Bóc tách chi tiết câu hỏi Tự luận (Tình huống, Bối cảnh, Yêu cầu, Thang điểm, Rubric).
     */
    private ParsedEssayDetails parseEssayDetails(String rawText, int order, String sectionContext, BigDecimal sectionMaxScore) {
        String text = cleanGarbageFooters(rawText);
        BigDecimal maxScore = sectionMaxScore != null ? sectionMaxScore : BigDecimal.valueOf(30.0);

        // 1. Trích xuất thang điểm từ tiêu đề / nội dung nếu có
        Matcher scoreMatcher = ESSAY_SCORE_PATTERN.matcher(text);
        if (scoreMatcher.find()) {
            try {
                String scoreStr = null;
                for (int i = 1; i <= 3; i++) {
                    if (scoreMatcher.group(i) != null) {
                        scoreStr = scoreMatcher.group(i);
                        break;
                    }
                }
                if (scoreStr != null) {
                    maxScore = new BigDecimal(scoreStr.replace(",", "."));
                }
            } catch (Exception ignored) {}
        }

        // 2. Làm sạch tiền tố câu hỏi hoặc "Nội dung câu hỏi:"
        String cleanText = text.replaceAll("(?imu)^\\s*(?:Nội\\s*dung\\s*(?:câu\\s*hỏi|đề\\s*bài)|Câu\\s*hỏi|Đề\\s*bài)[:\\s]*", "").trim();

        // 3. Tách Context (Bối cảnh/Đoạn trích) và Prompt (Yêu cầu/Hỏi/Viết bài)
        String context = "";
        String prompt = "";

        Matcher promptMatcher = ESSAY_PROMPT_START_PATTERN.matcher(cleanText);
        if (promptMatcher.find() && promptMatcher.start() > 0) {
            int promptIndex = promptMatcher.start();
            context = cleanText.substring(0, promptIndex).trim();
            prompt = cleanText.substring(promptIndex).trim();
        } else {
            // Thử tách theo 2 đoạn văn \n\n
            String[] paragraphs = cleanText.split("\n\n");
            if (paragraphs.length >= 2) {
                context = paragraphs[0].trim();
                StringBuilder rest = new StringBuilder();
                for (int i = 1; i < paragraphs.length; i++) {
                    if (!rest.isEmpty()) rest.append("\n\n");
                    rest.append(paragraphs[i].trim());
                }
                prompt = rest.toString();
            } else {
                prompt = cleanText;
                context = (sectionContext != null && !sectionContext.isBlank())
                        ? sectionContext
                        : "Chủ đề / Tình huống nghiệp vụ phát sinh trong thực tiễn.";
            }
        }

        context = context.replaceAll("(?imu)^\\s*(?:Câu\\s+\\d+|Tình\\s+huống\\s+\\d+|Bài\\s+tập\\s+\\d+|Câu\\s+hỏi\\s+\\d+)\\s*(?:\\((?!\\d+\\))[^)]*\\|\\[[^\\]]*\\])?\\s*[.:\\-\\)]?\\s*", "").trim();
        if (context.isBlank()) {
            context = (sectionContext != null && !sectionContext.isBlank())
                    ? sectionContext
                    : "Chủ đề / Tình huống nghiệp vụ phát sinh trong thực tiễn.";
        }

        prompt = prompt.replaceAll("(?imu)^\\s*(?:Yêu\\s*cầu|Hỏi|Nhiệm\\s*vụ|Câu\\s*hỏi)[:\\-\\.]?\\s*", "").trim();
        if (prompt.isBlank()) {
            prompt = cleanText;
        }

        String title;
        if (order == 1) {
            String scoreDisplay = maxScore.stripTrailingZeros().toPlainString();
            title = "Phần I: Tự luận (" + scoreDisplay + " điểm)";
        } else {
            title = "Tình huống " + (order < 10 ? "0" + order : String.valueOf(order)) + ": Xử lý nghiệp vụ CAND";
        }

        // Tạo tiêu chí chấm (Rubric) phù hợp theo nội dung tự luận
        List<String> rubrics;
        if (prompt.toLowerCase().contains("nghị luận") || prompt.toLowerCase().contains("chữ") || prompt.toLowerCase().contains("bài viết") || cleanText.toLowerCase().contains("hồ chí minh")) {
            rubrics = List.of(
                    "Giải thích và phân tích nội dung tư tưởng / chủ đề (" + maxScore.multiply(BigDecimal.valueOf(0.3)).setScale(1, RoundingMode.HALF_UP) + " điểm)",
                    "Liên hệ thực tiễn và vai trò, trách nhiệm hiện nay (" + maxScore.multiply(BigDecimal.valueOf(0.5)).setScale(1, RoundingMode.HALF_UP) + " điểm)",
                    "Kỹ năng lập luận, bố cục bài viết và diễn đạt (" + maxScore.multiply(BigDecimal.valueOf(0.2)).setScale(1, RoundingMode.HALF_UP) + " điểm)"
            );
        } else {
            rubrics = List.of(
                    "Xác định đúng thẩm quyền và căn cứ pháp lý (" + maxScore.multiply(BigDecimal.valueOf(0.3)).setScale(1, RoundingMode.HALF_UP) + " điểm)",
                    "Trình tự thủ tục tố tụng và lập biên bản nghiệp vụ (" + maxScore.multiply(BigDecimal.valueOf(0.5)).setScale(1, RoundingMode.HALF_UP) + " điểm)",
                    "Biện pháp bảo quản tang vật và kỹ năng xử lý tình huống (" + maxScore.multiply(BigDecimal.valueOf(0.2)).setScale(1, RoundingMode.HALF_UP) + " điểm)"
            );
        }

        return new ParsedEssayDetails(title, context, prompt, maxScore, rubrics);
    }

    private ParsedDocumentResponse.ParsedMcQuestion buildParsedMcQuestion(ExtractedQuestion eq, int order) {
        String idPrefix = eq.isShortAnswer() ? "mc-ext-" : "mc-";
        return ParsedDocumentResponse.ParsedMcQuestion.builder()
                .id(idPrefix + order)
                .order(order)
                .context(eq.context != null && !eq.context.isBlank() ? normalizeParagraphFlow(cleanGarbageFooters(eq.context)) : null)
                .question(cleanGarbageFooters(eq.questionText))
                .options(eq.options != null ? eq.options : Collections.emptyList())
                .correctAnswer(eq.correctAnswer != null ? eq.correctAnswer : "")
                .explanation(eq.explanation != null ? cleanGarbageFooters(eq.explanation) : "Căn cứ quy định pháp luật.")
                .legalReference(eq.legalReference != null ? cleanGarbageFooters(eq.legalReference) : "Quy định pháp luật")
                .build();
    }

    private ParsedDocumentResponse.ParsedEssayQuestion buildParsedEssayQuestion(ExtractedQuestion eq, int order) {
        return ParsedDocumentResponse.ParsedEssayQuestion.builder()
                .id("essay-" + order)
                .order(order)
                .title(eq.essayTitle != null ? eq.essayTitle : "Phần I: Tự luận")
                .context(eq.essayContext != null && !eq.essayContext.isBlank() ? normalizeParagraphFlow(cleanGarbageFooters(eq.essayContext)) : "Tình huống nghiệp vụ CAND phát sinh trong thực tế.")
                .prompt(eq.essayPrompt != null && !eq.essayPrompt.isBlank() ? cleanGarbageFooters(eq.essayPrompt) : "Hãy phân tích tình huống trên và đề xuất biện pháp xử lý theo quy định.")
                .maxScore(eq.essayMaxScore != null ? eq.essayMaxScore : BigDecimal.valueOf(30.0))
                .rubric(eq.essayRubrics != null ? eq.essayRubrics : List.of())
                .build();
    }

    private DraftQuestionDto buildDraftQuestionDto(ExtractedQuestion eq, int order) {
        DraftQuestionType type = eq.isEssay() ? DraftQuestionType.ESSAY :
                (eq.isShortAnswer() ? DraftQuestionType.SHORT_ANSWER : DraftQuestionType.MULTIPLE_CHOICE);

        List<DraftOptionDto> draftOpts = new ArrayList<>();
        if (!eq.isEssay() && eq.options != null) {
            for (ParsedDocumentResponse.ParsedOption opt : eq.options) {
                boolean isCorrect = opt.getLabel().equalsIgnoreCase(eq.correctAnswer);
                draftOpts.add(DraftOptionDto.builder()
                        .key(opt.getLabel())
                        .content(opt.getText())
                        .isCorrect(isCorrect)
                        .build());
            }
        }

        List<String> warnings = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        if (type == DraftQuestionType.MULTIPLE_CHOICE) {
            if (draftOpts.isEmpty()) {
                errors.add("Không tìm thấy bất kỳ phương án lựa chọn A/B/C/D nào.");
            } else if (draftOpts.size() < 4) {
                boolean isTrueFalse = draftOpts.size() == 2 && draftOpts.stream().anyMatch(o -> {
                    String c = o.getContent().toLowerCase();
                    return c.contains("đúng") || c.contains("sai") || c.contains("true") || c.contains("false");
                });
                if (!isTrueFalse) {
                    warnings.add("Câu trắc nghiệm chỉ có " + draftOpts.size() + " phương án (tiêu chuẩn là 4 phương án A, B, C, D).");
                }
            }
        }

        ParsingStatus status = !errors.isEmpty() ? ParsingStatus.ERROR :
                (!warnings.isEmpty() ? ParsingStatus.WARNING : ParsingStatus.SUCCESS);

        String content = eq.isEssay() ? eq.essayPrompt : eq.questionText;
        String context = eq.isEssay() ? (eq.essayContext != null ? normalizeParagraphFlow(eq.essayContext) : null)
                : (eq.context != null && !eq.context.isBlank() ? normalizeParagraphFlow(eq.context) : null);

        return DraftQuestionDto.builder()
                .temporaryId("q" + order)
                .questionNumber(order)
                .type(type)
                .section(eq.sectionTitle != null ? eq.sectionTitle : (eq.isEssay() ? "TỰ LUẬN" : "TRẮC NGHIỆM"))
                .context(context)
                .content(content != null ? content : "")
                .options(draftOpts)
                .answer(eq.correctAnswer)
                .hasAnswer(eq.correctAnswer != null && !eq.correctAnswer.isBlank())
                .parsingStatus(status)
                .warnings(warnings)
                .errors(errors)
                .build();
    }

    private ParsedDocumentResponse filterResponseByTargetType(ParsedDocumentResponse res, String targetType) {
        if ("MC_ONLY".equalsIgnoreCase(targetType)) {
            res.setEssayQuestions(Collections.emptyList());
        } else if ("ESSAY_ONLY".equalsIgnoreCase(targetType)) {
            res.setMultipleChoiceQuestions(Collections.emptyList());
        }
        return res;
    }

    private List<DraftQuestionDto> filterDraftQuestionsByMode(List<DraftQuestionDto> list, ImportMode mode) {
        if (mode == ImportMode.MCQ_ONLY) {
            return list.stream().filter(q -> q.getType() == DraftQuestionType.MULTIPLE_CHOICE).toList();
        } else if (mode == ImportMode.ESSAY_ONLY) {
            return list.stream().filter(q -> q.getType() == DraftQuestionType.ESSAY).toList();
        }
        return list;
    }

    /**
     * Kiểm tra tính toàn vẹn của đề thi: phát hiện trùng lặp số thứ tự câu hỏi,
     * phát hiện bước nhảy số câu hỏi (sequence gap) và kiểm tra số lượng đáp án trong bảng đáp án.
     */
    private void validateExamIntegrity(List<DraftQuestionDto> questions,
                                       Map<Integer, String> answerKeyMap,
                                       List<String> warnings) {
        if (questions == null || questions.isEmpty()) return;

        Set<Integer> seenNumbers = new HashSet<>();
        List<Integer> duplicateNumbers = new ArrayList<>();
        int prevNumber = -1;
        List<String> missingRanges = new ArrayList<>();

        for (DraftQuestionDto q : questions) {
            int num = q.getQuestionNumber();
            if (num > 0) {
                if (!seenNumbers.add(num)) {
                    duplicateNumbers.add(num);
                }
                if (prevNumber > 0 && num > prevNumber + 1) {
                    if (num == prevNumber + 2) {
                        missingRanges.add("Câu " + (prevNumber + 1));
                    } else {
                        missingRanges.add("Câu " + (prevNumber + 1) + " đến Câu " + (num - 1));
                    }
                }
                prevNumber = num;
            }
        }

        if (!duplicateNumbers.isEmpty()) {
            warnings.add("Phát hiện các câu hỏi trùng số thứ tự: " + duplicateNumbers);
        }
        if (!missingRanges.isEmpty()) {
            warnings.add("Phát hiện nhảy số thứ tự câu hỏi, có thể bị thiếu: " + String.join(", ", missingRanges));
        }

        // Kiểm tra khớp số lượng đáp án trong bảng đáp án nếu có
        if (answerKeyMap != null && !answerKeyMap.isEmpty()) {
            int totalMcq = (int) questions.stream().filter(q -> q.getType() == DraftQuestionType.MULTIPLE_CHOICE).count();
            if (totalMcq > 0 && answerKeyMap.size() != totalMcq) {
                warnings.add(String.format("Bảng đáp án có %d đáp án, trong khi tài liệu bóc tách được %d câu trắc nghiệm.",
                        answerKeyMap.size(), totalMcq));
            }
        }
    }

    // Helper classes
    private static class OptionTokenMatch {
        final int startIndex;
        final int endIndex;
        final String label;
        final boolean isStarred;

        OptionTokenMatch(int startIndex, int endIndex, String label, boolean isStarred) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.label = label;
            this.isStarred = isStarred;
        }
    }

    public static class ParsedOptionsResult {
        public final String questionText;
        public final List<ParsedDocumentResponse.ParsedOption> options;
        public final String correctLabelFromMarker;

        public ParsedOptionsResult(String questionText, List<ParsedDocumentResponse.ParsedOption> options, String correctLabelFromMarker) {
            this.questionText = questionText;
            this.options = options;
            this.correctLabelFromMarker = correctLabelFromMarker;
        }
    }

    private static class InlinePiece {
        final String label;
        final String text;

        InlinePiece(String label, String text) {
            this.label = label;
            this.text = text;
        }
    }

    private static class ParsedEssayDetails {
        final String title;
        final String context;
        final String prompt;
        final BigDecimal maxScore;
        final List<String> rubrics;

        ParsedEssayDetails(String title, String context, String prompt, BigDecimal maxScore, List<String> rubrics) {
            this.title = title;
            this.context = context;
            this.prompt = prompt;
            this.maxScore = maxScore;
            this.rubrics = rubrics;
        }
    }

    private static class ExtractedQuestion {
        int order;
        boolean isEssay;
        boolean isShortAnswer;
        String sectionTitle;
        String context;
        String questionText;
        List<ParsedDocumentResponse.ParsedOption> options;
        String correctAnswer;
        String explanation;
        String legalReference;
        String essayTitle;
        String essayContext;
        String essayPrompt;
        BigDecimal essayMaxScore;
        List<String> essayRubrics;

        ExtractedQuestion() {}

        ExtractedQuestion(int order, boolean isEssay, boolean isShortAnswer, String sectionTitle,
                          String context, String questionText, List<ParsedDocumentResponse.ParsedOption> options,
                          String correctAnswer, String explanation, String legalReference,
                          String essayTitle, String essayContext, String essayPrompt,
                          BigDecimal essayMaxScore, List<String> essayRubrics) {
            this.order = order;
            this.isEssay = isEssay;
            this.isShortAnswer = isShortAnswer;
            this.sectionTitle = sectionTitle;
            this.context = context;
            this.questionText = questionText;
            this.options = options;
            this.correctAnswer = correctAnswer;
            this.explanation = explanation;
            this.legalReference = legalReference;
            this.essayTitle = essayTitle;
            this.essayContext = essayContext;
            this.essayPrompt = essayPrompt;
            this.essayMaxScore = essayMaxScore;
            this.essayRubrics = essayRubrics;
        }

        public boolean isEssay() { return isEssay; }
        public boolean isShortAnswer() { return isShortAnswer; }
        public int getOrder() { return order; }
        public String getSectionTitle() { return sectionTitle; }
        public String getContext() { return context; }
        public String getQuestionText() { return questionText; }
        public List<ParsedDocumentResponse.ParsedOption> getOptions() { return options; }
        public String getCorrectAnswer() { return correctAnswer; }
        public String getExplanation() { return explanation; }
        public String getLegalReference() { return legalReference; }
        public String getEssayTitle() { return essayTitle; }
        public String getEssayContext() { return essayContext; }
        public String getEssayPrompt() { return essayPrompt; }
        public BigDecimal getEssayMaxScore() { return essayMaxScore; }
        public List<String> getEssayRubrics() { return essayRubrics; }

        public static ExtractedQuestionBuilder builder() {
            return new ExtractedQuestionBuilder();
        }

        public static class ExtractedQuestionBuilder {
            int order;
            boolean isEssay;
            boolean isShortAnswer;
            String sectionTitle;
            String context;
            String questionText;
            List<ParsedDocumentResponse.ParsedOption> options;
            String correctAnswer;
            String explanation;
            String legalReference;
            String essayTitle;
            String essayContext;
            String essayPrompt;
            BigDecimal essayMaxScore;
            List<String> essayRubrics;

            public ExtractedQuestionBuilder order(int order) { this.order = order; return this; }
            public ExtractedQuestionBuilder isEssay(boolean isEssay) { this.isEssay = isEssay; return this; }
            public ExtractedQuestionBuilder isShortAnswer(boolean isShortAnswer) { this.isShortAnswer = isShortAnswer; return this; }
            public ExtractedQuestionBuilder sectionTitle(String sectionTitle) { this.sectionTitle = sectionTitle; return this; }
            public ExtractedQuestionBuilder context(String context) { this.context = context; return this; }
            public ExtractedQuestionBuilder questionText(String questionText) { this.questionText = questionText; return this; }
            public ExtractedQuestionBuilder options(List<ParsedDocumentResponse.ParsedOption> options) { this.options = options; return this; }
            public ExtractedQuestionBuilder correctAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; return this; }
            public ExtractedQuestionBuilder explanation(String explanation) { this.explanation = explanation; return this; }
            public ExtractedQuestionBuilder legalReference(String legalReference) { this.legalReference = legalReference; return this; }
            public ExtractedQuestionBuilder essayTitle(String essayTitle) { this.essayTitle = essayTitle; return this; }
            public ExtractedQuestionBuilder essayContext(String essayContext) { this.essayContext = essayContext; return this; }
            public ExtractedQuestionBuilder essayPrompt(String essayPrompt) { this.essayPrompt = essayPrompt; return this; }
            public ExtractedQuestionBuilder essayMaxScore(BigDecimal essayMaxScore) { this.essayMaxScore = essayMaxScore; return this; }
            public ExtractedQuestionBuilder essayRubrics(List<String> essayRubrics) { this.essayRubrics = essayRubrics; return this; }

            public ExtractedQuestion build() {
                return new ExtractedQuestion(order, isEssay, isShortAnswer, sectionTitle, context, questionText,
                        options, correctAnswer, explanation, legalReference, essayTitle, essayContext,
                        essayPrompt, essayMaxScore, essayRubrics);
            }
        }
    }
}
