package jurisprudence_hub_be.module.document.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record ChapterUpdateRequest(
        @Size(max = 255, message = "Tiêu đề không được vượt quá 255 ký tự")
        String title,

        @Min(value = 1, message = "Thứ tự sắp xếp phải bắt đầu từ 1")
        Integer order,

        String description
) {
}
