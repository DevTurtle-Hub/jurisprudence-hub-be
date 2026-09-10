package jurisprudence_hub_be.common.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean empty,
        PageMetadata metadata
) {

    // Backward compatible constructor
    public PageResponse(List<T> content, int page, int size, long totalElements, int totalPages, boolean first, boolean last) {
        this(content, page, size, totalElements, totalPages, first, last, content.isEmpty(), null);
    }

    // Original method for backward compatibility
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    // Enhanced method with additional metadata
    public static <T> PageResponse<T> fromWithMetadata(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.isEmpty(),
                new PageMetadata(
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages()
                )
        );
    }

    public record PageMetadata(
            int currentPage,
            int pageSize,
            long totalElements,
            int totalPages
    ) {
        public long getTotalPages() {
            return totalPages;
        }

        public boolean hasNext() {
            return currentPage < totalPages - 1;
        }

        public boolean hasPrevious() {
            return currentPage > 0;
        }
    }
}
