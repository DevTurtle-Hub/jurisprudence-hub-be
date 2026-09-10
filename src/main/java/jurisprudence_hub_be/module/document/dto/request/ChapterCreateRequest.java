package jurisprudence_hub_be.module.document.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChapterCreateRequest(
        @NotBlank(message = "Tiêu đề chương không được để trống")
        @Size(max = 255, message = "Tiêu đề không được vượt quá 255 ký tự")
        String title,

        @NotNull(message = "Thứ tự sắp xếp không được để trống")
        @Min(value = 1, message = "Thứ tự sắp xếp phải bắt đầu từ 1")
        Integer order,

        String description
) {
}
