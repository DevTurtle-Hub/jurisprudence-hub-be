package jurisprudence_hub_be.module.exam.service;

import jurisprudence_hub_be.module.exam.dto.request.CreateExamRoomRequest;
import jurisprudence_hub_be.module.exam.dto.response.ExamRoomListResponse;
import jurisprudence_hub_be.module.exam.dto.response.ExamRoomResponse;
import jurisprudence_hub_be.module.exam.enums.ExamRoomStatus;

public interface ExamRoomService {

    ExamRoomResponse createRoom(CreateExamRoomRequest request, String createdBy);

    ExamRoomResponse updateRoom(String id, CreateExamRoomRequest request);

    void deleteRoom(String id);

    ExamRoomListResponse getRooms(String search, ExamRoomStatus status, int page, int limit);

    ExamRoomResponse getRoomDetail(String id);
}
