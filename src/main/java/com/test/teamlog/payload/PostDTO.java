package com.test.teamlog.payload;

import com.test.teamlog.entity.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.geo.Point;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostDTO {
    @Getter
    public static class PostRequest {
        private String contents;
        private AccessModifier accessModifier;
        private AccessModifier commentModifier;
        private Point location;
        private String writerId;
        private Long projectId;
        private List<String> hashtags;
    }
//
//    id: '796799',
//    content: '도쿄 스카이 트리는 일본 도쿄도 스미다구에 세워진 전파탑이다. 본래 높이 610.58m로 계획되었으나 2009년 10월에 높이 634 m로 설계가 변경되어, 캐나다의 CN 타워와 중국의 광저우타워를 제치고 세계에서 가장 높은 자립식 전파탑이 되었다.',
//    hashtags: ['스토리보드', '일본', '여행', '도쿄', '스카이트리'],
//    likeCount: 40,
//    commentCount: 3,
//    writeTime: '2020-09-13',

    @Data
    @Builder
    public static class PostResponse {
        private Long id;
        private String content;
        private List<String> hashtags;
        private int likeCount;
        private int commentCount;
        private LocalDateTime writeTime;
    }
}
