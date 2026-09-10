package jurisprudence_hub_be.module.exam.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class VerifyCandidateRequest {

    @NotBlank(message = "Số CCCD không được để trống")
    @Pattern(regexp = "^[0-9]{12}$", message = "Số CCCD phải gồm đúng 12 chữ số")
    private String cccd;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải gồm 10 đến 11 chữ số")
    private String phone;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Địa chỉ / đơn vị công tác không được để trống")
    private String address;

    public VerifyCandidateRequest() {
    }

    public VerifyCandidateRequest(String cccd, String fullName, String phone, String email, String address) {
        this.cccd = cccd;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public static VerifyCandidateRequestBuilder builder() {
        return new VerifyCandidateRequestBuilder();
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

    public static class VerifyCandidateRequestBuilder {
        private String cccd;
        private String fullName;
        private String phone;
        private String email;
        private String address;

        public VerifyCandidateRequestBuilder cccd(String cccd) {
            this.cccd = cccd;
            return this;
        }

        public VerifyCandidateRequestBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public VerifyCandidateRequestBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public VerifyCandidateRequestBuilder email(String email) {
            this.email = email;
            return this;
        }

        public VerifyCandidateRequestBuilder address(String address) {
            this.address = address;
            return this;
        }

        public VerifyCandidateRequest build() {
            return new VerifyCandidateRequest(cccd, fullName, phone, email, address);
        }
    }
}
