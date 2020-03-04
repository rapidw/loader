package io.rapidw.loader.master.response;

import io.rapidw.loader.master.exception.AppStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
//import org.springframework.data.domain.Page;

import java.util.List;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class PagedResponse<T> extends BaseResponse {

    private PagedData<T> data;

    private PagedResponse(PagedData<T> data) {
        super(AppStatus.SUCCESS);
        this.data = data;
    }

//    public static <T> PagedResponse<T> of(Page<T> page) {
//        PagedData<T> data = new PagedData<>(page);
//        return new PagedResponse<>(data);
//    }

    @Data
    public static class PagedData<T> {

        private List<T> data;

        /**
         * 分页属性
         */
        private Integer pageNum;
        private Integer pageSize;
        private Integer pages;
        private Long total;

//        PagedData(Page<T> page) {
//            this.data = page.getContent();
//            this.pageNum = page.getNumber() + 1;
//            this.pageSize = page.getSize();
//            this.pages = page.getTotalPages();
//            this.total = page.getTotalElements();
//        }
    }
}
