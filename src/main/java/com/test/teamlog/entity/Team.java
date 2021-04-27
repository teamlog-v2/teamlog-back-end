package com.test.teamlog.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @Setter
@Entity
public class Team extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String introduction;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_modifier",nullable = false)
    private AccessModifier accessModifier;

    @ManyToOne
    @JoinColumn(name = "master_user_id", nullable = false) // master_user_id 때문에 nullable 문제 생기는 거 아니가?
    private User master;
}
