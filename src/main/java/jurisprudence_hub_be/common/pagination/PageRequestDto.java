package jurisprudence_hub_be.common.pagination;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jurisprudence_hub_be.common.constant.AppConstants;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

@SuppressWarnings("null")
public record PageRequestDto(
        @Min(0)
        Integer page,

        @Min(1)
        @Max(AppConstants.MAX_PAGE_SIZE)
        Integer size,

        String sortBy,

        @Pattern(regexp = "ASC|DESC|asc|desc", message = "Sort direction must be ASC or DESC")
        String sortDirection
) {

    public PageRequestDto {
        if (page == null) {
            page = AppConstants.DEFAULT_PAGE;
        } else if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero");
        }

        if (size == null) {
            size = AppConstants.DEFAULT_PAGE_SIZE;
        } else if (size <= 0 || size > AppConstants.MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and " + AppConstants.MAX_PAGE_SIZE + ", but got " + size
            );
        }

        if (sortBy == null || sortBy.isBlank()) {
            sortBy = AppConstants.DEFAULT_SORT_BY;
        } else {
            String trimmedSort = sortBy.trim();
            if (!trimmedSort.matches("^[a-zA-Z0-9_.]+$")) {
                throw new IllegalArgumentException("Invalid sort field syntax: " + sortBy);
            }
            sortBy = trimmedSort;
        }

        if (sortDirection == null || sortDirection.isBlank()) {
            sortDirection = AppConstants.DEFAULT_SORT_DIRECTION;
        } else {
            String upper = sortDirection.trim().toUpperCase();
            if (!"ASC".equals(upper) && !"DESC".equals(upper)) {
                throw new IllegalArgumentException("Sort direction must be ASC or DESC, but got: " + sortDirection);
            }
            sortDirection = upper;
        }
    }

    public static PageRequestDto of(int page, int size) {
        return new PageRequestDto(page, size, null, null);
    }

    public static PageRequestDto of(int page, int size, String sortBy, String sortDirection) {
        return new PageRequestDto(page, size, sortBy, sortDirection);
    }

    public static PageRequestDto defaultPage() {
        return new PageRequestDto(AppConstants.DEFAULT_PAGE, AppConstants.DEFAULT_PAGE_SIZE, null, null);
    }

    public Sort toSort() {
        var direction = sortDirection;
        var field = sortBy;
        Sort result = Sort.by(Sort.Direction.fromString(direction), field);
        return result;
    }

    public Sort toSort(Set<String> allowedSortFields) {
        validateSortField(allowedSortFields);
        Sort result = toSort();
        return result;
    }

    public Pageable toPageable() {
        return org.springframework.data.domain.PageRequest.of(page, size, toSort());
    }

    public Pageable toPageable(Set<String> allowedSortFields) {
        validateSortField(allowedSortFields);
        return toPageable();
    }

    public Pageable toPageable(String... allowedSortFields) {
        if (allowedSortFields != null && allowedSortFields.length > 0) {
            validateSortField(Set.of(allowedSortFields));
        }
        return toPageable();
    }

    private void validateSortField(Set<String> allowedSortFields) {
        if (allowedSortFields != null && !allowedSortFields.isEmpty() && !allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException(
                    String.format("Sort field '%s' is not permitted. Allowed fields: %s", sortBy, allowedSortFields)
            );
        }
    }

    public PageRequestDto withPage(int newPage) {
        return new PageRequestDto(newPage, size, sortBy, sortDirection);
    }

    public PageRequestDto withSize(int newSize) {
        return new PageRequestDto(page, newSize, sortBy, sortDirection);
    }

    public PageRequestDto withSort(String newSortBy, String newSortDirection) {
        return new PageRequestDto(page, size, newSortBy, newSortDirection);
    }
}
