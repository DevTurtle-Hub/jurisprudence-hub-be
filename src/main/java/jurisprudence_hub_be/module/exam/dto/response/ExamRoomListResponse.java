package jurisprudence_hub_be.module.exam.dto.response;

import java.util.ArrayList;
import java.util.List;

public class ExamRoomListResponse {

    private List<ExamRoomSummaryResponse> items = new ArrayList<>();
    private PaginationDto pagination;

    public ExamRoomListResponse() {
    }

    public ExamRoomListResponse(List<ExamRoomSummaryResponse> items, PaginationDto pagination) {
        this.items = (items != null) ? items : new ArrayList<>();
        this.pagination = pagination;
    }

    public static ExamRoomListResponseBuilder builder() {
        return new ExamRoomListResponseBuilder();
    }

    public List<ExamRoomSummaryResponse> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    public void setItems(List<ExamRoomSummaryResponse> items) {
        this.items = items;
    }

    public PaginationDto getPagination() {
        return pagination;
    }

    public void setPagination(PaginationDto pagination) {
        this.pagination = pagination;
    }

    public static class PaginationDto {
        private long total;
        private int page;
        private int limit;
        private int totalPages;

        public PaginationDto() {
        }

        public PaginationDto(long total, int page, int limit, int totalPages) {
            this.total = total;
            this.page = page;
            this.limit = limit;
            this.totalPages = totalPages;
        }

        public static PaginationDtoBuilder builder() {
            return new PaginationDtoBuilder();
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public static class PaginationDtoBuilder {
            private long total;
            private int page;
            private int limit;
            private int totalPages;

            public PaginationDtoBuilder total(long total) {
                this.total = total;
                return this;
            }

            public PaginationDtoBuilder page(int page) {
                this.page = page;
                return this;
            }

            public PaginationDtoBuilder limit(int limit) {
                this.limit = limit;
                return this;
            }

            public PaginationDtoBuilder totalPages(int totalPages) {
                this.totalPages = totalPages;
                return this;
            }

            public PaginationDto build() {
                return new PaginationDto(total, page, limit, totalPages);
            }
        }
    }

    public static class ExamRoomListResponseBuilder {
        private List<ExamRoomSummaryResponse> items = new ArrayList<>();
        private PaginationDto pagination;

        public ExamRoomListResponseBuilder items(List<ExamRoomSummaryResponse> items) {
            this.items = (items != null) ? items : new ArrayList<>();
            return this;
        }

        public ExamRoomListResponseBuilder pagination(PaginationDto pagination) {
            this.pagination = pagination;
            return this;
        }

        public ExamRoomListResponse build() {
            return new ExamRoomListResponse(items, pagination);
        }
    }
}
