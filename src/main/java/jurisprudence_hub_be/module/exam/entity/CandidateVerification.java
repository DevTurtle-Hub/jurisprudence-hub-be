package jurisprudence_hub_be.module.exam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "candidate_verifications")
@EntityListeners(AuditingEntityListener.class)
public class CandidateVerification {

    @Id
    @Column(length = 64)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ExamRoom room;

    @Column(length = 12, nullable = false)
    private String cccd;

    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;

    @Column(length = 15, nullable = false)
    private String phone;

    @Column(length = 100, nullable = false)
    private String email;

    @Column(length = 255, nullable = false)
    private String address;

    @Column(name = "candidate_id", length = 32, nullable = false)
    private String candidateId;

    @Column(name = "session_token", columnDefinition = "TEXT", nullable = false, unique = true)
    private String sessionToken;

    @CreatedDate
    @Column(name = "verified_at", nullable = false, updatable = false)
    private Instant verifiedAt;

    public CandidateVerification() {
    }

    public CandidateVerification(String id, ExamRoom room, String cccd, String fullName,
                                 String phone, String email, String address, String candidateId,
                                 String sessionToken, Instant verifiedAt) {
        this.id = id;
        this.room = room;
        this.cccd = cccd;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.candidateId = candidateId;
        this.sessionToken = sessionToken;
        this.verifiedAt = verifiedAt;
    }

    public static CandidateVerificationBuilder builder() {
        return new CandidateVerificationBuilder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ExamRoom getRoom() {
        return room;
    }

    public void setRoom(ExamRoom room) {
        this.room = room;
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

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public Instant getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(Instant verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = "cand-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        }
    }

    public static class CandidateVerificationBuilder {
        private String id;
        private ExamRoom room;
        private String cccd;
        private String fullName;
        private String phone;
        private String email;
        private String address;
        private String candidateId;
        private String sessionToken;
        private Instant verifiedAt;

        public CandidateVerificationBuilder id(String id) {
            this.id = id;
            return this;
        }

        public CandidateVerificationBuilder room(ExamRoom room) {
            this.room = room;
            return this;
        }

        public CandidateVerificationBuilder cccd(String cccd) {
            this.cccd = cccd;
            return this;
        }

        public CandidateVerificationBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public CandidateVerificationBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public CandidateVerificationBuilder email(String email) {
            this.email = email;
            return this;
        }

        public CandidateVerificationBuilder address(String address) {
            this.address = address;
            return this;
        }

        public CandidateVerificationBuilder candidateId(String candidateId) {
            this.candidateId = candidateId;
            return this;
        }

        public CandidateVerificationBuilder sessionToken(String sessionToken) {
            this.sessionToken = sessionToken;
            return this;
        }

        public CandidateVerificationBuilder verifiedAt(Instant verifiedAt) {
            this.verifiedAt = verifiedAt;
            return this;
        }

        public CandidateVerification build() {
            return new CandidateVerification(id, room, cccd, fullName, phone, email, address, candidateId, sessionToken, verifiedAt);
        }
    }
}
