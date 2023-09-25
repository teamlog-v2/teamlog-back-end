package com.test.teamlog.domain.projectinvitation.dto;

import lombok.Data;

@Data
public class ProjectInvitationReadPendingResult {
    private Long idx;
    private Long projectIdx;
    private String projectName;
    private String thumbnail;
}
