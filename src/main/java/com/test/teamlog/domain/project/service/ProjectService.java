package com.test.teamlog.domain.project.service;

import com.test.teamlog.domain.account.model.Account;
import com.test.teamlog.domain.account.service.query.AccountQueryService;
import com.test.teamlog.domain.file.info.entity.FileInfo;
import com.test.teamlog.domain.file.management.service.FileManagementService;
import com.test.teamlog.domain.posttag.entity.PostTag;
import com.test.teamlog.domain.project.dto.*;
import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.domain.project.repository.ProjectRepository;
import com.test.teamlog.domain.projectfollow.entity.ProjectFollower;
import com.test.teamlog.domain.projectfollow.service.query.ProjectFollowQueryService;
import com.test.teamlog.domain.projectjoin.entity.ProjectJoin;
import com.test.teamlog.domain.projectjoin.service.query.ProjectJoinQueryService;
import com.test.teamlog.domain.projectmember.entity.ProjectMember;
import com.test.teamlog.domain.projectmember.service.query.ProjectMemberQueryService;
import com.test.teamlog.global.dto.ApiResponse;
import com.test.teamlog.global.entity.AccessModifier;
import com.test.teamlog.global.exception.BadRequestException;
import com.test.teamlog.global.exception.ResourceForbiddenException;
import com.test.teamlog.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    private final AccountQueryService accountQueryService;
    private final FileManagementService fileManagementService;
    private final ProjectJoinQueryService projectJoinQueryService;
    private final ProjectMemberQueryService projectMemberQueryService;
    private final ProjectFollowQueryService projectFollowQueryService;

    @Transactional
    public ApiResponse updateThumbnail(Long projectId, MultipartFile image) throws IOException {
        Project project = prepareProject(projectId);

        final FileInfo fileInfo = fileManagementService.uploadFile(image);
        project.updateThumbnail(fileInfo);

        return new ApiResponse(Boolean.TRUE, "프로젝트 썸네일 수정 성공");
    }

    @Transactional
    public ApiResponse deleteThumbnail(Long projectId, Account currentAccount) {
        Project project = prepareProject(projectId);

        if (project.isProjectMaster(currentAccount)) {
            throw new BadRequestException("프로젝트 멤버가 아닙니다.");
        }

        project.updateThumbnail(null);
        return new ApiResponse(Boolean.TRUE, "프로젝트 썸네일 삭제 성공");
    }

    // 유저가 팔로우 중인 프로젝트
    public List<ProjectReadAccountFollowingResult> readAllAccountFollowing(String identification, Account currentAccount) {
        Account account;

        boolean isMyProjectList = false;
        if (currentAccount == null) {
            account = prepareAccount(identification);
        } else {
            isMyProjectList = currentAccount.getIdentification().equals(identification);
            account = isMyProjectList ? currentAccount : prepareAccount(identification);
        }

        List<ProjectFollower> accountFollowingProjectList = projectFollowQueryService.findAllByAccount(account);
        List<ProjectReadAccountFollowingResult> resultList = new ArrayList<>();

        for (ProjectFollower accountFollowingProject : accountFollowingProjectList) {
            Project project = accountFollowingProject.getProject();
            if (!isMyProjectList && isNotMemberAndPrivateProject(currentAccount, project))
                continue;

            resultList.add(ProjectReadAccountFollowingResult.from(project));
        }

        return resultList;
    }

    // 프로젝트 검색
    public List<ProjectSearchResult> search(String name, Account currentAccount) {
        List<Project> projectList = projectRepository.searchProjectByName(name);
        List<ProjectSearchResult> responseList = new ArrayList<>();

        for (Project project : projectList) {
            if (isNotMemberAndPrivateProject(currentAccount, project))
                continue;

            responseList.add(ProjectSearchResult.from(project));
        }
        return responseList;
    }

    // 프로젝트와의 관계
    private Relation detectRelation(Project project, Account currentAccount) {
        if (currentAccount == null) return Relation.NONE;
        if (project.isProjectMaster(currentAccount)) return Relation.MASTER;
        if (projectMemberQueryService.isProjectMember(project, currentAccount)) return Relation.MEMBER;

        ProjectJoin projectJoin = projectJoinQueryService.findByProjectAndAccount(project, currentAccount).orElse(null);

        if (projectJoin != null) {
            if (projectJoin.getIsAccepted() && !projectJoin.getIsInvited()) return Relation.APPLIED;
            if (!projectJoin.getIsAccepted() && projectJoin.getIsInvited()) return Relation.INVITED;
        }

        return Relation.NONE;
    }

    // 단일 프로젝트 조회
    public ProjectReadResult readOne(Long id, Account currentAccount) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 프로젝트입니다. id: " + id));

        // Private 시 검증
        if (project.getAccessModifier() == AccessModifier.PRIVATE) {
            projectMemberQueryService.validateProjectMember(project, currentAccount);
        }

        final ProjectReadResult result = ProjectReadResult.from(project);
        result.setRelation(detectRelation(project, currentAccount));

        return result;
    }

    // 사용자 프로젝트 리스트 조회
    public List<ProjectReadByAccountResult> readAllByAccount(String identification, Account currentAccount) {
        Account account;

        boolean isMyProjectList = false;
        if (currentAccount == null) {
            account = prepareAccount(identification);
        } else {
            isMyProjectList = currentAccount.getIdentification().equals(identification);
            account = isMyProjectList ? currentAccount : prepareAccount(identification);
        }

        List<Project> projectList = projectRepository.findProjectByAccount(account);
        List<ProjectReadByAccountResult> resultList = new ArrayList<>();

        for (Project project : projectList) {
            if (!isMyProjectList && isNotMemberAndPrivateProject(currentAccount, project)) {
                continue;
            }

            resultList.add(ProjectReadByAccountResult.from(project));
        }

        return resultList;
    }

    // 본인이 속하지 않은 비공개 프로젝트인지 확인
    private boolean isNotMemberAndPrivateProject(Account currentAccount, Project project) {
        return !projectMemberQueryService.isProjectMember(project, currentAccount) && project.getAccessModifier() == AccessModifier.PRIVATE;
    }

    /**
     * 프로젝트 생성
     *
     * @param input
     * @param currentAccount
     * @return
     */
    @Transactional
    public ProjectCreateResult create(ProjectCreateInput input, Account currentAccount) {
        final Project project = input.toProject(currentAccount);
        project.addProjectMember(ProjectMember.create(project, currentAccount));

        final Project newProject = projectRepository.save(project);
        return ProjectCreateResult.of(newProject, Relation.MASTER);
    }

    /**
     * 프로젝트 수정
     *
     * @param id
     * @param input
     * @param currentAccount
     * @return
     */
    @Transactional
    public ProjectUpdateResult update(Long id, ProjectUpdateInput input, Account currentAccount) {
        Project project = prepareProject(id);
        validateMasterAccount(project, currentAccount);

        project.update(input.getName(), input.getIntroduction(), input.getAccessModifier());

        return ProjectUpdateResult.of(project, Relation.MASTER);
    }

    // 프로젝트 마스터 위임
    @Transactional
    public ApiResponse delegateMaster(Long id, String newMasterIdentification, Account currentAccount) {
        Project project = prepareProject(id);
        validateMasterAccount(project, currentAccount);

        final Account newMaster = prepareAccount(newMasterIdentification); // 존재하는지 검증
        project.delegateMaster(newMaster);

        return new ApiResponse(Boolean.TRUE, "프로젝트 마스터 위임 성공");
    }

    // 프로젝트 삭제
    @Transactional
    public ApiResponse delete(Long id, Account currentAccount) {
        Project project = prepareProject(id);
        validateMasterAccount(project, currentAccount);

        projectRepository.delete(project);
        return new ApiResponse(Boolean.TRUE, "프로젝트 삭제 성공");
    }

    // 마스터 검증
    private void validateMasterAccount(Project project, Account currentAccount) {
        if (!project.isProjectMaster(currentAccount)) {
            throw new ResourceForbiddenException("권한이 없습니다.\n( 프로젝트 마스터 아님 )");
        }
    }

    // 프로젝트의 해시태그들 조회
    public List<String> readHashTagsInProjectPosts(Long projectId) {
        final List<PostTag> hashTagList = projectRepository.findAllPostTagByProjectId(projectId);

        return hashTagList.stream().map(PostTag::getName).collect(Collectors.toList());
    }

    private Account prepareAccount(String identification) {
        return accountQueryService.findByIdentification(identification)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 회원입니다. id: " + identification));
    }

    private Project prepareProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 프로젝트입니다. id: "+ projectId));
    }
}
