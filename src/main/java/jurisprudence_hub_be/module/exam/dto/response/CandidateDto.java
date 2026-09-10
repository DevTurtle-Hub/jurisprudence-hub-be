package jurisprudence_hub_be.module.exam.dto.response;

import java.time.Instant;

public class CandidateDto {
    private String cccd;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private String candidateId;
    private Instant verifiedAt;
    private boolean isVerified = true;

    public CandidateDto() {
    }

    public CandidateDto(String cccd, String fullName, String phone, String email,
                        String address, String candidateId, Instant verifiedAt, boolean isVerified) {
        this.cccd = cccd;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.candidateId = candidateId;
        this.verifiedAt = verifiedAt;
        this.isVerified = isVerified;
    }

    public static CandidateDtoBuilder builder() {
        return new CandidateDtoBuilder();
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public Instant getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(Instant verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public static class CandidateDtoBuilder {
        private String cccd;
        private String fullName;
        private String phone;
        private String email;
        private String address;
        private String candidateId;
        private Instant verifiedAt;
        private boolean isVerified = true;

        public CandidateDtoBuilder cccd(String cccd) {
            this.cccd = cccd;
            return this;
        }

        public CandidateDtoBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public CandidateDtoBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public CandidateDtoBuilder email(String email) {
            this.email = email;
            return this;
        }

        public CandidateDtoBuilder address(String address) {
            this.address = address;
            return this;
        }

        public CandidateDtoBuilder candidateId(String candidateId) {
            this.candidateId = candidateId;
            return this;
        }

        public CandidateDtoBuilder verifiedAt(Instant verifiedAt) {
            this.verifiedAt = verifiedAt;
            return this;
        }

        public CandidateDtoBuilder isVerified(boolean isVerified) {
            this.isVerified = isVerified;
            return this;
        }

        public CandidateDto build() {
            return new CandidateDto(cccd, fullName, phone, email, address, candidateId, verifiedAt, isVerified);
        }
    }
}
