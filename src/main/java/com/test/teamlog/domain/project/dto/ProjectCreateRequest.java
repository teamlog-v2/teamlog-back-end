package com.test.teamlog.domain.project.dto;

import com.test.teamlog.entity.AccessModifier;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProjectCreateRequest {
    @NotBlank(message = "빈문자열, 공백만 입력할 수 없습니다.")
    @Size(min=1,max=20, message = "프로젝트 이름을 1자에서 20자 사이로 입력해주세요.")
    private String name;
    private String introduction;
    private AccessModifier accessModifier;
    private String masterId;
    private Long teamId;

    public ProjectCreateInput toInput() {
        final ProjectCreateInput input = new ProjectCreateInput();
        input.setName(this.name);
        input.setIntroduction(this.introduction);
        input.setAccessModifier(this.accessModifier);
        input.setMasterId(this.masterId);
        input.setTeamId(this.teamId);

        return input;
    }
}
