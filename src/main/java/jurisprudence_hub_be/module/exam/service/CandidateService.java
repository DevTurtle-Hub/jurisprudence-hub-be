package jurisprudence_hub_be.module.exam.service;

import jurisprudence_hub_be.module.exam.dto.request.VerifyCandidateRequest;
import jurisprudence_hub_be.module.exam.dto.response.CandidateVerificationResponse;
import jurisprudence_hub_be.module.exam.entity.CandidateVerification;

public interface CandidateService {

    CandidateVerificationResponse verifyCandidate(String roomId, VerifyCandidateRequest request);

    CandidateVerification getValidSession(String sessionToken);
}
