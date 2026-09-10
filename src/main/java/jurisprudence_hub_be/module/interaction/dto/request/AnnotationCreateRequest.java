package jurisprudence_hub_be.module.interaction.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnnotationCreateRequest(
        @NotBlank(message = "Đoạn văn bản chọn không được để trống")
        String selectedText,

        @NotBlank(message = "Loại đánh dấu không được để trống")
        @Size(max = 50, message = "Loại đánh dấu không được vượt quá 50 ký tự")
        String kind,

        @NotBlank(message = "Màu sắc đánh dấu không được để trống")
        @Size(max = 50, message = "Màu sắc không được vượt quá 50 ký tự")
        String color,

        String note
) {
}
