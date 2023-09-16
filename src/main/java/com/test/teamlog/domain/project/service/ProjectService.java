package com.test.teamlog.domain.project.service;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.account.service.query.AccountQueryService;
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
import com.test.teamlog.exception.ResourceForbiddenException;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.global.entity.AccessModifier;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.ProjectDTO;
import com.test.teamlog.payload.Relation;
import com.test.teamlog.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    private final FileStorageService fileStorageService;
    private final AccountQueryService accountQueryService;
    private final ProjectJoinQueryService projectJoinQueryService;
    private final ProjectMemberQueryService projectMemberQueryService;
    private final ProjectFollowQueryService projectFollowQueryService;

    @Transactional
    public ApiResponse updateThumbnail(Long projectId, MultipartFile image, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        if (project.getThumbnail() != null) {
            fileStorageService.deleteFile(project.getThumbnail());
        }

        String thumbnailPath = fileStorageService.storeFile(image, null, null);
        project.setThumbnail(thumbnailPath);

        return new ApiResponse(Boolean.TRUE, "프로젝트 썸네일 수정 성공");
    }

    @Transactional
    public ApiResponse deleteThumbnail(Long projectId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        if (project.getThumbnail() != null) {
            fileStorageService.deleteFile(project.getThumbnail());
            project.setThumbnail(null);
        }

        return new ApiResponse(Boolean.TRUE, "프로젝트 썸네일 삭제 성공");
    }

    // 유저가 팔로우 중인 프로젝트
    public List<ProjectReadUserFollowingResult> readAllUserFollowing(String identification, User currentUser) {
        User user;

        boolean isMyProjectList = false;
        if (currentUser == null) {
            user = readByIdentification(identification);
        } else {
            isMyProjectList = currentUser.getIdentification().equals(identification);
            user = isMyProjectList ? currentUser : readByIdentification(identification);
        }

        List<ProjectFollower> userFollowingProjectList = projectFollowQueryService.findAllByUser(user);
        List<ProjectReadUserFollowingResult> resultList = new ArrayList<>();

        for (ProjectFollower userFollowingProject : userFollowingProjectList) {
            Project project = userFollowingProject.getProject();
            if (!isMyProjectList && isNotMemberAndPrivateProject(currentUser, project))
                continue;

            resultList.add(ProjectReadUserFollowingResult.from(project));
        }

        return resultList;
    }

    // 프로젝트 검색
    public List<ProjectSearchResult> search(String name, User currentUser) {
        List<Project> projectList = projectRepository.searchProjectByName(name);
        List<ProjectSearchResult> responseList = new ArrayList<>();

        for (Project project : projectList) {
            if (isNotMemberAndPrivateProject(currentUser, project))
                continue;

            responseList.add(ProjectSearchResult.from(project));
        }
        return responseList;
    }

    // 프로젝트와의 관계
    private Relation detectRelation(Project project, User currentUser) {
        if (currentUser == null) return Relation.NONE;
        if (project.isProjectMaster(currentUser)) return Relation.MASTER;
        if (projectMemberQueryService.isProjectMember(project, currentUser)) return Relation.MEMBER;

        ProjectJoin projectJoin = projectJoinQueryService.findByProjectAndUser(project, currentUser).orElse(null);

        if (projectJoin != null) {
            if (projectJoin.getIsAccepted() && !projectJoin.getIsInvited()) return Relation.APPLIED;
            if (!projectJoin.getIsAccepted() && projectJoin.getIsInvited()) return Relation.INVITED;
        }

        return Relation.NONE;
    }

    // 단일 프로젝트 조회
    public ProjectReadResult readOne(Long id, User currentUser) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        // Private 시 검증
        if (project.getAccessModifier() == AccessModifier.PRIVATE) {
            projectMemberQueryService.validateProjectMember(project, currentUser);
        }

        final ProjectReadResult result = ProjectReadResult.from(project);

        // TODO: 프로젝트 썸네일 관련 로직 추가

        result.setRelation(detectRelation(project, currentUser));

        return result;
    }

    // 사용자 프로젝트 리스트 조회
    public List<ProjectReadByUserResult> readAllByUser(String identification, User currentUser) {
        User user;

        boolean isMyProjectList = false;
        if (currentUser == null) {
            user = readByIdentification(identification);
        } else {
            isMyProjectList = currentUser.getIdentification().equals(identification);
            user = isMyProjectList ? currentUser : readByIdentification(identification);
        }

        List<Project> projectList = projectRepository.findProjectByUser(user);
        List<ProjectReadByUserResult> resultList = new ArrayList<>();

        for (Project project : projectList) {
            if (!isMyProjectList && isNotMemberAndPrivateProject(currentUser, project)) {
                continue;
            }

            resultList.add(ProjectReadByUserResult.from(project));
        }

        return resultList;
    }

    // 본인이 속하지 않은 비공개 프로젝트인지 확인
    private boolean isNotMemberAndPrivateProject(User currentUser, Project project) {
        return !projectMemberQueryService.isProjectMember(project, currentUser) && project.getAccessModifier() == AccessModifier.PRIVATE;
    }

    /**
     * 프로젝트 생성
     *
     * @param input
     * @param currentUser
     * @return
     */
    @Transactional
    public ProjectCreateResult create(ProjectCreateInput input, User currentUser) {
        final Project project = input.toProject(currentUser);
        project.addProjectMember(ProjectMember.create(project, currentUser));

        final Project newProject = projectRepository.save(project);
        return ProjectCreateResult.of(newProject, Relation.MASTER);
    }

    /**
     * 프로젝트 수정
     *
     * @param id
     * @param input
     * @param currentUser
     * @return
     */
    @Transactional
    public ProjectUpdateResult update(Long id, ProjectUpdateInput input, User currentUser) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", id));
        validateMasterUser(project, currentUser);

        project.update(input.getName(), input.getIntroduction(), input.getAccessModifier());

        return ProjectUpdateResult.of(project, Relation.MASTER);
    }

    // 프로젝트 팀
    @Transactional
    public ProjectDTO.ProjectResponse setTeamInProject(Long id, ProjectDTO.ProjectRequest request, User currentUser) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", id));
        validateMasterUser(project, currentUser);

        projectRepository.save(project);
        ProjectDTO.ProjectResponse result = new ProjectDTO.ProjectResponse(project);
        result.setRelation(Relation.MASTER);
        return result;
    }

    // 프로젝트 마스터 위임
    @Transactional
    public ApiResponse delegateMaster(Long id, String newMasterIdentification, User currentUser) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", id));
        validateMasterUser(project, currentUser);

        final User newMaster = readByIdentification(newMasterIdentification); // 존재하는지 검증
        project.delegateMaster(newMaster);

        return new ApiResponse(Boolean.TRUE, "프로젝트 마스터 위임 성공");
    }

    // 프로젝트 삭제
    @Transactional
    public ApiResponse delete(Long id, User currentUser) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", id));
        validateMasterUser(project, currentUser);

        projectRepository.delete(project);
        return new ApiResponse(Boolean.TRUE, "프로젝트 삭제 성공");
    }

    // 마스터 검증
    private void validateMasterUser(Project project, User currentUser) {
        if (!project.isProjectMaster(currentUser)) {
            throw new ResourceForbiddenException("권한이 없습니다.\n( 프로젝트 마스터 아님 )");
        }
    }

    // 프로젝트의 해시태그들 조회
    public List<String> readHashTagsInProjectPosts(Long projectId) {
        final List<PostTag> hashTagList = projectRepository.findAllPostTagByProjectId(projectId);
        return hashTagList.stream().map(PostTag::getName).collect(Collectors.toList());
    }

    public Project findOne(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", projectId));
    }

    private User readByIdentification(String identification) {
        return accountQueryService.findByIdentification(identification)
                .orElseThrow(() -> new ResourceNotFoundException("User", "identification", identification));
    }
}
