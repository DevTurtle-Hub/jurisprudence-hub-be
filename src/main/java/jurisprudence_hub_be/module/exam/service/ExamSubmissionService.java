package jurisprudence_hub_be.module.exam.service;

import jurisprudence_hub_be.module.exam.dto.request.GradeEssayRequest;
import jurisprudence_hub_be.module.exam.dto.request.SubmitExamRequest;
import jurisprudence_hub_be.module.exam.dto.response.ExamSubmissionReceiptResponse;
import jurisprudence_hub_be.module.exam.dto.response.SubmissionDetailResponse;

public interface ExamSubmissionService {

    ExamSubmissionReceiptResponse submitExam(String roomId, String sessionToken, SubmitExamRequest request);

    SubmissionDetailResponse getReceipt(String receiptId);

    SubmissionDetailResponse gradeEssay(String submissionId, GradeEssayRequest request);
}
