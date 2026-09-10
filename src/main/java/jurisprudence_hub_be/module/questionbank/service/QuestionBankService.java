package jurisprudence_hub_be.module.questionbank.service;

import jurisprudence_hub_be.module.questionbank.dto.request.QuestionBankRequest;
import jurisprudence_hub_be.module.questionbank.dto.response.QuestionBankResponse;
import jurisprudence_hub_be.module.questionbank.entity.QuestionBank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionBankService {

    // Import và preview câu hỏi từ file (dùng lại logic import từ exam)
    List<QuestionBankResponse> parseAndPreviewQuestions(MultipartFile file, String targetType);

    // Lưu câu hỏi sau khi preview và chỉnh sửa
    QuestionBankResponse saveQuestion(QuestionBankRequest request, String createdBy);

    // Lưu nhiều câu hỏi cùng lúc
    List<QuestionBankResponse> saveMultipleQuestions(List<QuestionBankRequest> requests, String createdBy);

    // Lấy danh sách câu hỏi có phân trang
    Page<QuestionBankResponse> getQuestions(String category, String questionType,
                                           String keyword, boolean isDraft, Pageable pageable);

    // Lấy chi tiết câu hỏi
    QuestionBankResponse getQuestionById(String id);

    // Admin sửa câu hỏi đã lưu
    QuestionBankResponse updateQuestion(String id, QuestionBankRequest request, String editedBy, String editReason);

    // Xóa câu hỏi
    void deleteQuestion(String id, String deletedBy);

    // Publish draft câu hỏi
    QuestionBankResponse publishQuestion(String id, String verifiedBy);

    // Tạo câu hỏi tay (không import từ file)
    QuestionBankResponse createManualQuestion(QuestionBankRequest request, String createdBy);
}