package io.rapidw.loader.master.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

//import static org.springframework.data.domain.PageRequest.of;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {

    @NotNull(message = "should set page num")
    @Min(value = 1, message = "page num should greater than 1")
    @Max(value = 100, message = "page num should smaller than 100")
    private Integer pageNum = 1;

    @NotNull(message = "should set page size")
    @Min(value = 1, message = "page size should greater than 1")
    @Max(value = 100, message = "page size should smaller than 100")
    private int pageSize = 20;

//    public Pageable getPageable() {
//        return of(pageNum - 1, pageSize);
//    }

//    public Pageable getPageable(Sort sort) {
//        return of(pageNum - 1, pageSize, sort);
//    }
}
