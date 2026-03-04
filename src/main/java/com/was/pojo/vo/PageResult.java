package com.was.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页结果VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {

    private Long total;

    private Integer pageNum;

    private Integer pageSize;

    private Integer totalPages;

    private List<T> records;

    public static <T> PageResult<T> of(Long total, Integer pageNum, Integer pageSize, List<T> records) {
        int totalPages = (int) Math.ceil((double) total / pageSize);
        return PageResult.<T>builder()
                .total(total)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .records(records)
                .build();
    }
}
