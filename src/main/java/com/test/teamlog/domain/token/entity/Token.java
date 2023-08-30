package com.test.teamlog.domain.token.entity;

import jakarta.persistence.*;
import lombok.*;



@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false)
    private String identification;

    private String refreshToken;
}
