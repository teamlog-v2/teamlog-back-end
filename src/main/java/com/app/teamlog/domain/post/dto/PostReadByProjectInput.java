package com.app.teamlog.domain.post.dto;

import com.app.teamlog.global.entity.AccessModifier;
import lombok.Data;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
public class PostReadByProjectInput {
    private Long projectId;
    private AccessModifier accessModifier;

    private List<String> hashtagList;
    private String keyword;
    private Integer order;
    private Long cursor;
    private int size;

    private Sort.Direction sort = Sort.Direction.DESC;
    private String comparisonOperator = "<";

    public void convertPagingInfo() {
        // FIXME: NPE 방지를 위한 임시 조치. 추후 개선 필요
        if (order == null) return;

        if (order == -1) {
            sort = Sort.Direction.ASC;
            comparisonOperator = ">";
        }
    }
}
