package ftms.svc.transactions.api.application.dto;

import java.util.List;

public class TransactionPageResponse {

    private List<TransactionResponse> content;
    private PageMetadata page;

    public List<TransactionResponse> getContent() { return content; }
    public void setContent(List<TransactionResponse> content) { this.content = content; }

    public PageMetadata getPage() { return page; }
    public void setPage(PageMetadata page) { this.page = page; }

    public static class PageMetadata {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;

        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }

        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }

        public long getTotalElements() { return totalElements; }
        public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    }
}

