package com.test.teamlog.domain.projectjoin.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProjectJoinInviteInput {
    private Long projectId;
    private List<String> accountIdentificationList;
}
