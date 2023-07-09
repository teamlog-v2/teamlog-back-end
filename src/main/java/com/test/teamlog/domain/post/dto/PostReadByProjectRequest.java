package com.test.teamlog.domain.post.dto;

import lombok.Data;

import java.util.List;

@Data
public class PostReadByProjectRequest {
    private List<String> hashtagList;
    private String keyword;
    private Integer order;
    private Long cursor;
    private int size;

    public PostReadByProjectInput toInput() {
        PostReadByProjectInput input = new PostReadByProjectInput();
        input.setHashtagList(hashtagList);
        input.setKeyword(keyword);
        input.setOrder(order);
        input.setCursor(cursor);
        input.setSize(size);

        return input;
    }
}
