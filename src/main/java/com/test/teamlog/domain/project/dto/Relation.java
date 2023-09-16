package com.test.teamlog.domain.project.dto;

// TODO: project의 dto에서 사용 중이라서 우선 같은 패키지에 두었는데 위치 고민 필요
public enum Relation {
    MASTER,
    MEMBER,
    INVITED,
    APPLIED,
    NONE;
}
