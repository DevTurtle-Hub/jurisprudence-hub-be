package jurisprudence_hub_be.module.exam.service.impl;

import jurisprudence_hub_be.common.exception.BadRequestException;
import jurisprudence_hub_be.common.exception.ResourceNotFoundException;
import jurisprudence_hub_be.common.exception.UnauthorizedException;
import jurisprudence_hub_be.module.exam.constant.ExamConstant;
import jurisprudence_hub_be.module.exam.dto.request.VerifyCandidateRequest;
import jurisprudence_hub_be.module.exam.dto.response.CandidateDto;
import jurisprudence_hub_be.module.exam.dto.response.CandidateVerificationResponse;
import jurisprudence_hub_be.module.exam.entity.CandidateVerification;
import jurisprudence_hub_be.module.exam.entity.ExamRoom;
import jurisprudence_hub_be.module.exam.enums.ExamRoomStatus;
import jurisprudence_hub_be.module.exam.repository.CandidateVerificationRepository;
import jurisprudence_hub_be.module.exam.repository.ExamRoomRepository;
import jurisprudence_hub_be.module.exam.service.CandidateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class CandidateServiceImpl implements CandidateService {

    private final ExamRoomRepository examRoomRepository;
    private final CandidateVerificationRepository candidateVerificationRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public CandidateServiceImpl(ExamRoomRepository examRoomRepository,
                                CandidateVerificationRepository candidateVerificationRepository) {
        this.examRoomRepository = examRoomRepository;
        this.candidateVerificationRepository = candidateVerificationRepository;
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public CandidateVerificationResponse verifyCandidate(String roomId, VerifyCandidateRequest request) {
        String lookupRoomId = roomId != null ? roomId.toUpperCase() : "";
        ExamRoom room = examRoomRepository.findById(roomId != null ? roomId : "")
                .or(() -> examRoomRepository.findByCode(lookupRoomId))
                .orElseThrow(() -> new ResourceNotFoundException(ExamConstant.MSG_ROOM_NOT_FOUND + roomId));

        // 1. Kiểm tra trạng thái phòng thi
        if (room.getStatus() != ExamRoomStatus.OPEN) {
            throw new BadRequestException(String.format(ExamConstant.MSG_ROOM_NOT_OPEN, room.getStatus()));
        }

        String cccd = (request != null && request.getCccd() != null) ? request.getCccd().trim() : "";
        String cccdLast4 = cccd.length() >= 4 ? cccd.substring(cccd.length() - 4) : cccd;

        // Mã phòng chuẩn hóa
        String rawCode = room.getCode() != null ? room.getCode() : "";
        String roomCodeTag = rawCode.replaceAll("[^A-Za-z0-9]", "");
        if (roomCodeTag.length() > 6) {
            roomCodeTag = roomCodeTag.substring(0, 6);
        }

        // 2. Tính số thứ tự thí sinh trong phòng để sinh index 3 chữ số
        long count = candidateVerificationRepository.countByRoomId(room.getId());
        String index3Digit = String.format("%03d", count + 1);

        // candidateId = SBD-[ROOM_CODE]-[CCCD_last4]-[INDEX_3digit]
        String candidateId = "SBD-" + roomCodeTag + "-" + cccdLast4 + "-" + index3Digit;

        // 3. Tạo sessionToken bảo mật (32 bytes hex)
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String sessionToken = HexFormat.of().formatHex(randomBytes);

        String id = "cand-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        String fullName = (request != null && request.getFullName() != null) ? request.getFullName().trim() : "";
        String phone = (request != null && request.getPhone() != null) ? request.getPhone().trim() : "";
        String email = (request != null && request.getEmail() != null) ? request.getEmail().trim() : "";
        String address = (request != null && request.getAddress() != null) ? request.getAddress().trim() : "";

        CandidateVerification verification = CandidateVerification.builder()
                .id(id)
                .room(room)
                .cccd(cccd)
                .fullName(fullName)
                .phone(phone)
                .email(email)
                .address(address)
                .candidateId(candidateId)
                .sessionToken(sessionToken)
                .build();

        @SuppressWarnings("null")
        CandidateVerification saved = candidateVerificationRepository.save(verification);

        CandidateDto candidateDto = CandidateDto.builder()
                .cccd(saved.getCccd())
                .fullName(saved.getFullName())
                .phone(saved.getPhone())
                .email(saved.getEmail())
                .address(saved.getAddress())
                .candidateId(saved.getCandidateId())
                .verifiedAt(saved.getVerifiedAt() != null ? saved.getVerifiedAt() : Instant.now())
                .isVerified(true)
                .build();

        return CandidateVerificationResponse.builder()
                .sessionToken(saved.getSessionToken())
                .candidate(candidateDto)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CandidateVerification getValidSession(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new UnauthorizedException(ExamConstant.MSG_SESSION_TOKEN_REQUIRED_SIMPLE);
        }

        String sessionToken = rawToken.startsWith("Bearer ") ? rawToken.substring(7).trim() : rawToken.trim();

        CandidateVerification verification = candidateVerificationRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new UnauthorizedException(ExamConstant.MSG_SESSION_TOKEN_INVALID_OR_EXPIRED));

        // Kiểm tra thời hạn session: verifiedAt + durationMinutes + 30 phút đệm
        Instant verifiedAt = verification.getVerifiedAt();
        if (verifiedAt != null) {
            int durationMinutes = verification.getRoom() != null ? verification.getRoom().getDurationMinutes() : 60;
            Instant expiry = verifiedAt.plus(Duration.ofMinutes(durationMinutes + 30));
            if (Instant.now().isAfter(expiry)) {
                throw new UnauthorizedException(ExamConstant.MSG_SESSION_EXPIRED);
            }
        }

        return verification;
    }
}
