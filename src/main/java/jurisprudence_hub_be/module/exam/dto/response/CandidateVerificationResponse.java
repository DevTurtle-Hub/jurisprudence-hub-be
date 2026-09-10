package jurisprudence_hub_be.module.exam.dto.response;

public class CandidateVerificationResponse {
    private String sessionToken;
    private CandidateDto candidate;

    public CandidateVerificationResponse() {
    }

    public CandidateVerificationResponse(String sessionToken, CandidateDto candidate) {
        this.sessionToken = sessionToken;
        this.candidate = candidate;
    }

    public static CandidateVerificationResponseBuilder builder() {
        return new CandidateVerificationResponseBuilder();
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public CandidateDto getCandidate() {
        return candidate;
    }

    public void setCandidate(CandidateDto candidate) {
        this.candidate = candidate;
    }

    public static class CandidateVerificationResponseBuilder {
        private String sessionToken;
        private CandidateDto candidate;

        public CandidateVerificationResponseBuilder sessionToken(String sessionToken) {
            this.sessionToken = sessionToken;
            return this;
        }

        public CandidateVerificationResponseBuilder candidate(CandidateDto candidate) {
            this.candidate = candidate;
            return this;
        }

        public CandidateVerificationResponse build() {
            return new CandidateVerificationResponse(sessionToken, candidate);
        }
    }
}
