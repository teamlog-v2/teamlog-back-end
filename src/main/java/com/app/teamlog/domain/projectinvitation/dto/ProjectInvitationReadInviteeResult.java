package com.app.teamlog.domain.projectinvitation.dto;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.file.info.entity.FileInfo;
import com.app.teamlog.domain.projectinvitation.entity.ProjectInvitation;
import lombok.Data;

@Data
public class ProjectInvitationReadInviteeResult {
    private String inviteeIdentification;
    private String inviteeName;
    private String inviteeProfileImgPath;

    private String inviterIdentification;
    private String inviterName;

    public static ProjectInvitationReadInviteeResult from(ProjectInvitation projectInvitation) {
        final ProjectInvitationReadInviteeResult result = new ProjectInvitationReadInviteeResult();
        final Account invitee = projectInvitation.getInvitee();
        final Account inviter = projectInvitation.getInviter();

        result.setInviteeIdentification(invitee.getIdentification());
        result.setInviteeName(invitee.getName());

        final FileInfo profileImage = invitee.getProfileImage();
        if (profileImage != null) result.setInviteeProfileImgPath(profileImage.getStoredFilePath());

        result.setInviterIdentification(inviter.getIdentification());
        result.setInviterName(inviter.getName());

        return result;
    }
}
