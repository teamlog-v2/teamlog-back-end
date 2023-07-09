package com.test.teamlog.domain.post.dto;

import lombok.Data;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
public class PostReadByProjectInput {
    private List<String> hashtagList;
    private String keyword;
    private Integer order;
    private Long cursor;
    private int size;

    private Sort.Direction sort = Sort.Direction.DESC;
    private String comparisonOperator = "<";

    public void convertPagingInfo() {
        if (order == -1) {
            sort = Sort.Direction.ASC;
            comparisonOperator = ">";
        }
    }
}
