package com.test.teamlog.global.dto;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Data
public class CustomPageRequest {
    private int page = 0;
    private int size = 10;
    private Sort.Direction direction = Sort.Direction.DESC;
    private String sort = "id";

    public PageRequest toPageRequest() {
        return PageRequest.of(page, size, direction, sort);
    }
}
