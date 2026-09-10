package jurisprudence_hub_be.module.exam.parser;

import jurisprudence_hub_be.common.exception.BadRequestException;
import jurisprudence_hub_be.module.exam.constant.ExamConstant;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Thành phần trích xuất văn bản từ file PDF sử dụng Apache PDFBox.
 * Tích hợp các thuật toán hiện đại:
 * 1. Tự động giải mã PDF có thiết lập bảo vệ không mật khẩu người dùng.
 * 2. Phát hiện và cảnh báo chính xác tài liệu PDF scan / dạng ảnh không có text layer.
 * 3. Khử tiêu đề lặp trang (Recurring Headers / Footers) tránh làm hỏng câu hỏi vắt qua trang.
 * 4. Tự động nối các từ bị gãy dòng do dấu gạch nối (De-hyphenation).
 * 5. Chuẩn hóa Ligatures, ký tự khoanh tròn (Ⓐ, Ⓑ..), chữ cái toàn phần, khoảng trắng ẩn và Unicode NFC.
 */
@Component
public class PdfTextExtractor {

    private static final Logger log = LoggerFactory.getLogger(PdfTextExtractor.class);

    // Các pattern header / footer rác của đề thi
    private static final Pattern PAGE_NUMBER_PATTERN = Pattern.compile(
            "^\\s*(?:Trang\\s*\\d+\\s*(?:/|của|\\-|–)?\\s*\\d*|\\d+\\s*(?:/|\\-|–)\\s*\\d+|\\-\\s*\\d+\\s*\\-|\\d+)\\s*$",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern EXAM_CODE_PATTERN = Pattern.compile(
            "^\\s*Mã\\s*(?:đề|bài)(?:\\s*thi)?\\s*[:\\-]?\\s*[A-Za-z0-9_\\-]+\\s*$",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern CANDIDATE_INFO_HEADER = Pattern.compile(
            "^\\s*(?:Họ\\s*và\\s*tên|Họ\\s*tên|Số\\s*báo\\s*danh|Phòng\\s*thi)\\s*(?:thí\\s*sinh)?\\s*:.*$",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern COMMON_FOOTER_NOTICE = Pattern.compile(
            "^\\s*(?:Thí\\s*sinh\\s*không\\s*(?:được)?\\s*sử\\s*dụng\\s*tài\\s*liệu|Cán\\s*bộ\\s*(?:coi|chấm)\\s*thi\\s*không\\s*giải\\s*thích\\s*gì\\s*thêm|[\\-=_*~\\s]*HẾT[\\-=_*~\\s]*)\\s*$",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * Trích xuất văn bản thô từ file PDF và chuẩn hóa các dòng.
     */
    public String extractText(MultipartFile file) {
        validatePdfFile(file);

        try (InputStream inputStream = file.getInputStream();
             PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {

            handlePdfDecryption(document);

            int totalPages = document.getNumberOfPages();
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            // Thuật toán khử header/footer lặp trang nếu tài liệu có nhiều trang
            String rawText;
            if (totalPages > 1) {
                rawText = extractWithRecurringHeaderFooterSuppression(document, stripper, totalPages);
            } else {
                rawText = stripper.getText(document);
            }

            // Kiểm tra tài liệu scan / dạng ảnh không có text layer
            validateTextLayerDensity(rawText, totalPages);

            return cleanAndNormalizeText(rawText);

        } catch (BadRequestException e) {
            throw e;
        } catch (IOException e) {
            log.error("Lỗi khi đọc tệp PDF bằng PDFBox: {}", e.getMessage(), e);
            throw new BadRequestException(ExamConstant.MSG_PDF_READ_ERROR + e.getMessage());
        }
    }

    /**
     * Trích xuất trực tiếp từ mảng byte (hỗ trợ kiểm thử hoặc nguồn khác)
     */
    public String extractText(byte[] pdfBytes) {
        if (pdfBytes == null || pdfBytes.length == 0) {
            throw new BadRequestException(ExamConstant.MSG_FILE_EMPTY);
        }
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            handlePdfDecryption(document);

            int totalPages = document.getNumberOfPages();
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            String rawText;
            if (totalPages > 1) {
                rawText = extractWithRecurringHeaderFooterSuppression(document, stripper, totalPages);
            } else {
                rawText = stripper.getText(document);
            }

            validateTextLayerDensity(rawText, totalPages);
            return cleanAndNormalizeText(rawText);
        } catch (BadRequestException e) {
            throw e;
        } catch (IOException e) {
            throw new BadRequestException(ExamConstant.MSG_PDF_EXTRACT_ERROR + e.getMessage());
        }
    }

    /**
     * Xử lý giải mã PDF: Nếu chỉ đặt mã hóa bảo vệ (Owner Password) không cần mật khẩu người dùng thì tự động gỡ.
     */
    private void handlePdfDecryption(PDDocument document) {
        if (document.isEncrypted()) {
            try {
                document.setAllSecurityToBeRemoved(true);
            } catch (Exception e) {
                throw new BadRequestException(ExamConstant.MSG_PDF_PASSWORD_PROTECTED);
            }
        }
    }

    /**
     * Kiểm tra mật độ text layer để phát hiện sớm tài liệu dạng scan ảnh.
     */
    private void validateTextLayerDensity(String rawText, int totalPages) {
        if (rawText == null || rawText.trim().isEmpty()) {
            throw new BadRequestException(ExamConstant.MSG_PDF_NO_TEXT_LAYER);
        }

        // Nếu trung bình mỗi trang có ít hơn 15 ký tự thì chắc chắn là scan ảnh hoặc file rỗng
        if (totalPages > 0 && rawText.trim().length() < 15 * totalPages && rawText.trim().length() < 80) {
            throw new BadRequestException(ExamConstant.MSG_PDF_INSUFFICIENT_TEXT);
        }
    }

    /**
     * Trích xuất văn bản từng trang kết hợp thuật toán phát hiện và triệt tiêu header / footer lặp lại qua các trang.
     */
    private String extractWithRecurringHeaderFooterSuppression(PDDocument document, PDFTextStripper stripper, int totalPages) throws IOException {
        List<List<String>> pagesLines = new ArrayList<>();
        Map<String, Integer> topLinesCount = new HashMap<>();
        Map<String, Integer> bottomLinesCount = new HashMap<>();

        for (int p = 1; p <= totalPages; p++) {
            stripper.setStartPage(p);
            stripper.setEndPage(p);
            String pageText = stripper.getText(document);
            String[] rawLines = pageText.split("\r\n|\r|\n");
            List<String> pageLines = new ArrayList<>();

            for (String l : rawLines) {
                pageLines.add(l.trim());
            }
            pagesLines.add(pageLines);

            // Ghi nhận dòng đầu (header)
            int countTop = 0;
            for (String l : pageLines) {
                if (!l.isEmpty() && !PAGE_NUMBER_PATTERN.matcher(l).matches()) {
                    topLinesCount.put(l, topLinesCount.getOrDefault(l, 0) + 1);
                    countTop++;
                    if (countTop >= 2) break;
                }
            }

            // Ghi nhận dòng cuối (footer)
            int countBottom = 0;
            for (int i = pageLines.size() - 1; i >= 0; i--) {
                String l = pageLines.get(i);
                if (!l.isEmpty() && !PAGE_NUMBER_PATTERN.matcher(l).matches()) {
                    bottomLinesCount.put(l, bottomLinesCount.getOrDefault(l, 0) + 1);
                    countBottom++;
                    if (countBottom >= 2) break;
                }
            }
        }

        // Dòng được coi là header/footer lặp nếu xuất hiện trên >= 50% số trang (tối thiểu 2 trang)
        int threshold = Math.max(2, totalPages / 2);
        Set<String> recurringLines = new HashSet<>();

        for (Map.Entry<String, Integer> entry : topLinesCount.entrySet()) {
            if (entry.getValue() >= threshold && entry.getKey().length() > 5) {
                recurringLines.add(entry.getKey());
            }
        }
        for (Map.Entry<String, Integer> entry : bottomLinesCount.entrySet()) {
            if (entry.getValue() >= threshold && entry.getKey().length() > 5) {
                recurringLines.add(entry.getKey());
            }
        }

        StringBuilder fullText = new StringBuilder();
        for (int p = 0; p < pagesLines.size(); p++) {
            List<String> pageLines = pagesLines.get(p);
            // Trang đầu tiên (p = 0) giữ lại tiêu đề đề thi
            for (int i = 0; i < pageLines.size(); i++) {
                String line = pageLines.get(i);
                if (p > 0 && recurringLines.contains(line)) {
                    continue; // Triệt tiêu header lặp từ trang 2 trở đi
                }
                fullText.append(line).append("\n");
            }
        }

        return fullText.toString();
    }

    private void validatePdfFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException(ExamConstant.MSG_FILE_EMPTY);
        }

        String originalFilename = file.getOriginalFilename();
        String fileName = originalFilename != null ? originalFilename.toLowerCase() : "";
        if (!fileName.endsWith(".pdf") && !"application/pdf".equalsIgnoreCase(file.getContentType())) {
            throw new BadRequestException(ExamConstant.MSG_FILE_UNSUPPORTED);
        }

        // Giới hạn kích thước file PDF (20MB)
        long maxBytes = 20 * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new BadRequestException(ExamConstant.MSG_FILE_TOO_LARGE);
        }
    }

    /**
     * Làm sạch text, chuẩn hóa Unicode NFC, xử lý ligatures, nối từ gãy dòng và loại bỏ rác.
     */
    public String cleanAndNormalizeText(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return "";
        }

        // 1. Thay thế Ligatures phổ biến (tránh lỗi font PDF ghép chữ fi, fl, ffi...)
        String text = rawText
                .replace("ﬁ", "fi")
                .replace("ﬂ", "fl")
                .replace("ﬃ", "ffi")
                .replace("ﬀ", "ff")
                .replace("ﬆ", "st");

        // 2. Thay thế chữ cái khoanh tròn (Circled letters) Ⓐ..Ⓔ, ⓐ..ⓔ và số nhận định ①..⑥
        text = text
                .replace("Ⓐ.", "A.").replace("Ⓑ.", "B.").replace("Ⓒ.", "C.").replace("Ⓓ.", "D.").replace("Ⓔ.", "E.")
                .replace("Ⓐ", "A. ").replace("Ⓑ", "B. ").replace("Ⓒ", "C. ").replace("Ⓓ", "D. ").replace("Ⓔ", "E. ")
                .replace("ⓐ.", "A.").replace("ⓑ.", "B.").replace("ⓒ.", "C.").replace("ⓓ.", "D.").replace("ⓔ.", "E.")
                .replace("ⓐ", "A. ").replace("ⓑ", "B. ").replace("ⓒ", "C. ").replace("ⓓ", "D. ").replace("ⓔ", "E. ")
                .replace("A. .", "A.").replace("B. .", "B.").replace("C. .", "C.").replace("D. .", "D.").replace("E. .", "E.")
                .replace("①", "(1)").replace("②", "(2)").replace("③", "(3)").replace("④", "(4)").replace("⑤", "(5)").replace("⑥", "(6)");

        // 3. Thay thế chữ cái toàn phần (Fullwidth letters)
        text = text
                .replace("Ａ", "A").replace("Ｂ", "B").replace("Ｃ", "C").replace("Ｄ", "D").replace("Ｅ", "E")
                .replace("ａ", "a").replace("ｂ", "b").replace("ｃ", "c").replace("ｄ", "d").replace("ｅ", "e");

        // 4. Chuẩn hóa khoảng trắng & ký tự điều khiển ẩn
        text = text
                .replace("\u00A0", " ")  // Non-breaking space
                .replace("\u200B", "")   // Zero-width space
                .replace("\uFEFF", "")   // Zero-width no-break space (BOM)
                .replace("\u200C", "")   // Zero-width non-joiner
                .replace("\u200D", "")   // Zero-width joiner
                .replace("\u00AD", "")   // Soft hyphen
                .replace('\f', '\n');

        // 5. Chuẩn hóa dấu gạch nối dài
        text = text
                .replace("—", "-").replace("–", "-");

        // 6. Chuẩn hóa Unicode sang dạng dựng sẵn (NFC) tránh lỗi ký tự tiếng Việt tổ hợp
        text = Normalizer.normalize(text, Normalizer.Form.NFC);

        // 7. Chuẩn hóa xuống dòng
        text = text.replace("\r\n", "\n").replace("\r", "\n");

        // 8. Khử gãy từ do dấu gạch nối cuối dòng (De-hyphenation)
        // Ví dụ: "hoạt động bả-\n o vệ" -> "hoạt động bảo vệ"
        text = text.replaceAll("(?iu)(\\p{L}+)-\\s*\\n\\s*(\\p{Ll}+)\\b", "$1$2");

        // 9. Lọc bỏ các dòng rác (header, footer, số trang)
        String[] rawLines = text.split("\n");
        List<String> cleanedLines = new ArrayList<>();

        for (String line : rawLines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                cleanedLines.add("");
                continue;
            }

            // Bỏ qua dòng số trang, mã đề đơn lẻ, thông tin báo danh thừa hoặc thông báo coi thi rác
            if (PAGE_NUMBER_PATTERN.matcher(trimmed).matches() ||
                EXAM_CODE_PATTERN.matcher(trimmed).matches() ||
                CANDIDATE_INFO_HEADER.matcher(trimmed).matches() ||
                COMMON_FOOTER_NOTICE.matcher(trimmed).matches()) {
                continue;
            }

            cleanedLines.add(line);
        }

        return String.join("\n", cleanedLines);
    }
}
