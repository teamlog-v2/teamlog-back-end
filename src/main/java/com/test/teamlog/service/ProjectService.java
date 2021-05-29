package com.test.teamlog.service;

import com.test.teamlog.entity.*;
import com.test.teamlog.exception.BadRequestException;
import com.test.teamlog.exception.ResourceAlreadyExistsException;
import com.test.teamlog.exception.ResourceForbiddenException;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.*;
import com.test.teamlog.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectFollowerRepository projectFollowerRepository;
    private final ProjectJoinRepository projectJoinRepository;
    private final PostRepository postRepository;
    private final FileStorageService fileStorageService;
    private String[] defaultProjectImages = new String[]{"20210504(81931d0a-14c3-43bd-912d-c4bd687c31ea)",
            "20210504(97a31008-24f4-4dc0-98bd-c83cf8d57b95)",
            "20210504(171eb9ac-f7ce-4e30-b4c6-a19a28e45c75)",
            "20210504(31157ace-269d-4a84-a73a-7a584f91ad9f)"};

    @Transactional
    public ApiResponse updateProjectThumbnail(Long projectId, MultipartFile image, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        if (project.getThumbnail() != null) {
            fileStorageService.deleteFile(project.getThumbnail());
            project.setThumbnail(null);
        }
        String thumbnailPath = fileStorageService.storeFile(image, null, null);
        project.setThumbnail(thumbnailPath);
        projectRepository.save(project);
        return new ApiResponse(Boolean.TRUE, "프로젝트 썸네일 수정 성공");
    }

    @Transactional
    public ApiResponse deleteProjectThumbnail(Long projectId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        if (project.getThumbnail() != null) {
            fileStorageService.deleteFile(project.getThumbnail());
            project.setThumbnail(null);
        }
        projectRepository.save(project);
        return new ApiResponse(Boolean.TRUE, "프로젝트 썸네일 삭제 성공");
    }

    // 팀 내 프로젝트 리스트 조회
    // TODO : 팀 내는 다 보여주지만 private/public 여부도 줘서 뭔가 다른 효과를 보여주는게 좋을 것 같음. 아니면 팀 멤버는 다 볼 수 있나?
    public List<ProjectDTO.ProjectListResponse> getProjectsByTeam(Long teamId) {
        User user = null;
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", teamId));
        List<Project> teamProjectList = projectRepository.findAllByTeam(team);

        List<ProjectDTO.ProjectListResponse> projects = new ArrayList<>();
        for (Project project : teamProjectList) {
            long postcount = postRepository.getPostsCount(project);

            String path = null;
            if (project.getThumbnail() == null) {
                path = defaultProjectImages[project.getId().intValue() % 4];
            } else {
                path = project.getThumbnail();
            }
            String imgUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/resources/")
                    .path(path)
                    .toUriString();

            ProjectDTO.ProjectListResponse item = ProjectDTO.ProjectListResponse.builder()
                    .id(project.getId())
                    .name(project.getName())
                    .postCount(postcount)
                    .updateTime(project.getUpdateTime())
                    .updateTimeStr(project.getUpdateTime().toString())
                    .thumbnail(imgUri)
                    .build();
            projects.add(item);
        }
        return projects;
    }

    // 유저가 팔로우 중인 프로젝트
    public List<ProjectDTO.ProjectListResponse> getUserFollowingProjects(String id, User currentUser) {
        User user = null;
        boolean isMyProjectList = false;
        if (currentUser == null) {
            user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("USER", "id", id));
        } else {
            isMyProjectList = currentUser.getId().equals(id);
            if (isMyProjectList)
                user = currentUser;
            else
                user = userRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("USER", "id", id));
        }
        List<ProjectFollower> userFollowingProjects = projectFollowerRepository.findAllByUser(user);

        List<ProjectDTO.ProjectListResponse> projects = new ArrayList<>();
        for (ProjectFollower userFollowingProject : userFollowingProjects) {
            Project project = userFollowingProject.getProject();
            if (!isMyProjectList) {
                // 팀멤버도 아니고 private면 x
                if (!isUserMemberOfProject(project, currentUser) && project.getAccessModifier() == AccessModifier.PRIVATE)
                    continue;
            }

            long postcount = postRepository.getPostsCount(project);

            String path = null;
            if (project.getThumbnail() == null) {
                path = defaultProjectImages[project.getId().intValue() % 4];
            } else {
                path = project.getThumbnail();
            }
            String imgUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/resources/")
                    .path(path)
                    .toUriString();
            ProjectDTO.ProjectListResponse item = ProjectDTO.ProjectListResponse.builder()
                    .id(project.getId())
                    .name(project.getName())
                    .postCount(postcount)
                    .updateTime(project.getUpdateTime())
                    .thumbnail(imgUri)
                    .build();
            projects.add(item);
        }

        return projects;
    }

    // 프로젝트 검색
    public List<ProjectDTO.ProjectListResponse> searchProject(String name, User currentUser) {
        List<Project> projectList = projectRepository.searchProjectByName(name);

        List<ProjectDTO.ProjectListResponse> projects = new ArrayList<>();
        for (Project project : projectList) {
            if (!isUserMemberOfProject(project, currentUser) && project.getAccessModifier() == AccessModifier.PRIVATE)
                continue;

            long postCount = project.getPosts().size();

            String path = null;
            if (project.getThumbnail() == null) {
                path = defaultProjectImages[project.getId().intValue() % 4];
            } else {
                path = project.getThumbnail();
            }
            String imgUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/resources/")
                    .path(path)
                    .toUriString();
            ProjectDTO.ProjectListResponse item = ProjectDTO.ProjectListResponse.builder()
                    .id(project.getId())
                    .name(project.getName())
                    .postCount(postCount)
                    .updateTime(project.getUpdateTime())
                    .thumbnail(imgUri)
                    .build();
            projects.add(item);
        }
        return projects;
    }

    // 프로젝트와의 관계
    public Relation getRelation(Project project, User currentUser) {
        if (currentUser == null) return Relation.NONE;
        if (project.getMaster().getId().equals(currentUser.getId())) return Relation.MASTER;
        if (isUserMemberOfProject(project, currentUser)) return Relation.MEMBER;

        ProjectJoin join = projectJoinRepository.findByProjectAndUser(project, currentUser).orElse(null);
        if (join != null) {
            if (join.getIsAccepted() == true && join.getIsInvited() == false) return Relation.APPLIED;
            if (join.getIsAccepted() == false && join.getIsInvited() == true) return Relation.INVITED;
        }
        return Relation.NONE;
    }

    // 단일 프로젝트 조회
    public ProjectDTO.ProjectResponse getProject(Long id, User currentUser) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        // Private 시 검증
        if (project.getAccessModifier() == AccessModifier.PRIVATE) {
            validateUserIsMemberOfProject(project, currentUser);
        }
        ProjectDTO.ProjectResponse projectResponse = new ProjectDTO.ProjectResponse(project);
        String path = null;
        if (project.getThumbnail() == null) {
            path = defaultProjectImages[project.getId().intValue() % 4];
        } else {
            path = project.getThumbnail();
        }
        String imgUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/resources/")
                .path(path)
                .toUriString();
        projectResponse.setThumbnail(imgUri);
        projectResponse.setRelation(getRelation(project, currentUser));
        return projectResponse;
    }

    // 사용자 프로젝트 리스트 조회
    public List<ProjectDTO.ProjectListResponse> getProjectsByUser(String id, User currentUser) {
        User user = null;
        boolean isMyProjectList = false;
        if (currentUser == null) {
            user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("USER", "id", id));
        } else {
            isMyProjectList = currentUser.getId().equals(id);
            if (isMyProjectList)
                user = currentUser;
            else
                user = userRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("USER", "id", id));
        }
        List<ProjectMember> userProjectList = projectMemberRepository.findByUser(user);

        List<ProjectDTO.ProjectListResponse> projects = new ArrayList<>();
        for (ProjectMember projectMember : userProjectList) {
            Project project = projectMember.getProject();
            if (!isMyProjectList) {
                // 팀멤버도 아니고 private면 x
                if (!isUserMemberOfProject(project, currentUser) && project.getAccessModifier() == AccessModifier.PRIVATE)
                    continue;
            }

            long postcount = postRepository.getPostsCount(project);

            String path = null;
            if (project.getThumbnail() == null) {
                path = defaultProjectImages[project.getId().intValue() % 4];
            } else {
                path = project.getThumbnail();
            }
            String imgUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/resources/")
                    .path(path)
                    .toUriString();
            ProjectDTO.ProjectListResponse item = ProjectDTO.ProjectListResponse.builder()
                    .id(project.getId())
                    .masterId(project.getMaster().getId())
                    .name(project.getName())
                    .postCount(postcount)
                    .updateTime(project.getUpdateTime())
                    .thumbnail(imgUri)
                    .build();
            if (project.getTeam() != null) {
                item.setTeam(project.getTeam());
            }
            projects.add(item);
        }
        return projects;
    }

    // 프로젝트 생성
    @Transactional
    public ProjectDTO.ProjectResponse createProject(ProjectDTO.ProjectRequest request, User currentUser) {
        Team team = null;
        if (request.getTeamId() != null) {
            team = teamRepository.findById(request.getTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team", "id", request.getTeamId()));
        }
        Project project = Project.builder()
                .name(request.getName())
                .introduction(request.getIntroduction())
                .accessModifier(request.getAccessModifier())
                .master(currentUser)
                .team(team)
                .build();
        projectRepository.save(project);


        ProjectMember member = ProjectMember.builder()
                .user(currentUser)
                .project(project)
                .build();
        project.getProjectMembers().add(member);

        ProjectDTO.ProjectResponse result = new ProjectDTO.ProjectResponse(project);
        result.setRelation(Relation.MASTER);
        return result;
    }

    // 프로젝트 수정 ( 위임 일단 포함 )
    @Transactional
    public ProjectDTO.ProjectResponse updateProject(Long id, ProjectDTO.ProjectRequest request, User currentUser) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", id));
        validateUserIsMaster(project, currentUser);

        if(request.getName() != null) {
            project.setName(request.getName());
        }
        if(request.getAccessModifier() != null) {
            project.setAccessModifier(request.getAccessModifier());
        }
        if(request.getIntroduction() != null) {
            project.setIntroduction(request.getIntroduction());
        }

        if (request.getMasterId() != null) {
            User newMaster = userRepository.findById(request.getMasterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project", "id", request.getMasterId()));
            project.setMaster(newMaster);
        }
        Team team = null;
        if (request.getTeamId() != null) {
            team = teamRepository.findById(request.getTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team", "id", request.getTeamId()));
        }
        project.setTeam(team);

        projectRepository.save(project);

        ProjectDTO.ProjectResponse result = new ProjectDTO.ProjectResponse(project);
        result.setRelation(Relation.MASTER);
        return result;
    }

    // 프로젝트 마스터 위임
    @Transactional
    public ApiResponse delegateProjectMaster(Long id, String newMasterId, User currentUser) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", id));
        validateUserIsMaster(project, currentUser);

        User newMaster = userRepository.findById(newMasterId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", newMasterId));
        project.setMaster(newMaster);
        projectRepository.save(project);
        return new ApiResponse(Boolean.TRUE, "프로젝트 마스터 위임 성공");
    }

    // 프로젝트 삭제
    @Transactional
    public ApiResponse deleteProject(Long id, User currentUser) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "ID", id));
        validateUserIsMaster(project, currentUser);

        projectRepository.delete(project);
        return new ApiResponse(Boolean.TRUE, "프로젝트 삭제 성공");
    }

    // ---------------------------
    // -------- 검증 메소드 --------
    // ---------------------------
    // 마스터 검증
    public void validateUserIsMaster(Project project, User currentUser) {
        if (!project.getMaster().getId().equals(currentUser.getId()))
            throw new ResourceForbiddenException("권한이 없습니다. ( 프로젝트 마스터 아님 )");
    }

    // 이미 ProjectJoin 있을 경우
    public Boolean isJoinAlreadyExist(Project project, User currentUser) {
        return projectJoinRepository.findByProjectAndUser(project, currentUser).isPresent();
    }

    // 프로젝트 멤버인지 아닌지
    public Boolean isUserMemberOfProject(Project project, User currentUser) {
        return projectMemberRepository.findByProjectAndUser(project, currentUser).isPresent();
    }

    // 프로젝트 멤버 검증
    public void validateUserIsMemberOfProject(Project project, User currentUser) {
        if (currentUser == null) throw new ResourceForbiddenException("권한이 없습니다. 로그인 해주세요.");
        projectMemberRepository.findByProjectAndUser(project, currentUser)
                .orElseThrow(() -> new ResourceForbiddenException("권한이 없습니다. ( 프로젝트 멤버 아님 )"));
    }
}
