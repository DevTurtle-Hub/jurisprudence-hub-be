package jurisprudence_hub_be.module.exam.service;

import jurisprudence_hub_be.module.exam.dto.response.ExamTakingRoomResponse;

public interface ExamTakingService {

    ExamTakingRoomResponse getExamForTaking(String roomId, String sessionToken);
}
