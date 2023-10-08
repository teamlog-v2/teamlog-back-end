package com.test.teamlog.domain.projectinvitation.dto;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.file.info.entity.FileInfo;
import com.test.teamlog.domain.projectinvitation.entity.ProjectInvitation;
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
        final User invitee = projectInvitation.getInvitee();
        final User inviter = projectInvitation.getInviter();

        result.setInviteeIdentification(invitee.getIdentification());
        result.setInviteeName(invitee.getName());

        final FileInfo profileImage = invitee.getProfileImage();
        if (profileImage != null) result.setInviteeProfileImgPath(profileImage.getStoredFilePath());

        result.setInviterIdentification(inviter.getIdentification());
        result.setInviterName(inviter.getName());

        return result;
    }
}
