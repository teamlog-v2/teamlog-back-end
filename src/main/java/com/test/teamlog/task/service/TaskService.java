package com.test.teamlog.task.service;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.account.repository.AccountRepository;
import com.test.teamlog.domain.project.service.ProjectService;
import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.task.entity.Task;
import com.test.teamlog.entity.TaskPerformer;
import com.test.teamlog.entity.TaskStatus;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.TaskDTO;
import com.test.teamlog.domain.project.repository.ProjectRepository;
import com.test.teamlog.task.repository.TaskRepository;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final AccountRepository accountRepository;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;

    // 태스크 상세 조회
    public TaskDTO.TaskResponse getTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        return new TaskDTO.TaskResponse(task);
    }

    // 프로젝트의 태스크들 조회
    @Transactional
    public List<TaskDTO.TaskResponse> getTasksByProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PROJECT", "id", id));

        Sort sort = Sort.by(Sort.Direction.ASC, "priority");
        List<Task> tasks = taskRepository.findByProject(project, sort);
        List<TaskDTO.TaskResponse> responses = new ArrayList<>();
        for (Task t : tasks) {
            if(t.getDeadline() !=null) {
                if(t.getDeadline().isBefore(LocalDateTime.now()) && (t.getStatus() == TaskStatus.IN_PROGRESS || t.getStatus() == TaskStatus.NOT_STARTED)) {
                    setTaskStatusFailed(t);
                }
            }
            TaskDTO.TaskResponse taskResponse = new TaskDTO.TaskResponse(t);
            responses.add(taskResponse);
        }
        return responses;
    }

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    public void setTaskStatusFailed(Task task) {
        taskRepository.reorderInPreviousStatus(task.getProject(),task.getStatus(),task.getPriority()); // 기존 status 정리
        task.setStatus(TaskStatus.FAILED);
        task.setPriority(taskRepository.getCountByPostAndStatus(task.getProject(),TaskStatus.FAILED));
    }

    // 태스크 생성
    @Transactional
    public TaskDTO.TaskResponse createTask(Long projectId, TaskDTO.TaskRequest request, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("PROJECT", "id", projectId));
        // 멤버만 가능
        projectService.validateProjectMember(project, currentUser);

        Task task = Task.builder()
                .taskName(request.getTaskName())
                .status(request.getStatus())
                .project(project)
                .build();
        projectService.validateProjectMember(task.getProject(), currentUser);
        if(request.getDeadline() == null)
            task.setDeadline(null);
        else
            task.setDeadline(request.getDeadline().withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime());

        List<TaskPerformer> performers = new ArrayList<>();
        if (request.getPerformersId() != null) {
            for (String userId : request.getPerformersId()) {
                User tempUser = accountRepository.findByIdentification(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("USER", "id", userId));
                TaskPerformer performer = TaskPerformer.builder()
                        .task(task)
                        .user(tempUser)
                        .build();
                performers.add(performer);
            }
            task.setTaskPerformers(performers);
        }
        task.setPriority(taskRepository.getCountByPostAndStatus(project, request.getStatus()));
        Task result = taskRepository.save(task);

        return new TaskDTO.TaskResponse(result);
    }


    // 태스크 수정
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    public void updateTask(Long taskId, TaskDTO.TaskRequest request, User currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("TASK", "ID", taskId));

        // 멤버만 가능
        projectService.validateProjectMember(task.getProject(), currentUser);
        if(request.getDeadline() == null)
            task.setDeadline(null);
        else
            task.setDeadline(request.getDeadline().withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime());
        task.setTaskName(request.getTaskName());

        List<TaskPerformer> originalTaskPerformer = null;
        if (task.getTaskPerformers() != null) {
            originalTaskPerformer = task.getTaskPerformers();
        }

        if (request.getPerformersId() == null) {
            if (originalTaskPerformer != null) task.removeTaskPerformers(originalTaskPerformer);
        } else {
            List<String> newTaskPerformersId = request.getPerformersId();
            List<String> maintainedTaskPerformersId = new ArrayList<>();
            if (originalTaskPerformer != null) {
                List<TaskPerformer> deletedTaskPerformers = new ArrayList<>();
                for (TaskPerformer taskPerformer : originalTaskPerformer) {
                    if (newTaskPerformersId.contains(taskPerformer.getUser().getIdentification())) {
                        maintainedTaskPerformersId.add(taskPerformer.getUser().getIdentification());
                    } else {
                        deletedTaskPerformers.add(taskPerformer);
                    }
                }
                task.removeTaskPerformers(deletedTaskPerformers);
            }

            newTaskPerformersId.removeAll(maintainedTaskPerformersId); // new
            if (newTaskPerformersId.size() > 0) {
                List<TaskPerformer> taskPerformers = new ArrayList<>();
                for (String performerId : newTaskPerformersId) {
                    User performer = accountRepository.findByIdentification(performerId)
                            .orElseThrow(() -> new ResourceNotFoundException("USER", "id", performerId));
                    TaskPerformer newPerformer = TaskPerformer.builder()
                            .task(task)
                            .user(performer)
                            .build();
                    taskPerformers.add(newPerformer);
                }
                task.addTaskPerformers(taskPerformers);
            }
        }

        // status 변경 시 그냥 마지막으로 밀어넣기
        if (!request.getStatus().equals(task.getStatus())) {
            taskRepository.reorderInPreviousStatus(task.getProject(),task.getStatus(),task.getPriority()); // 기존 status 정리
            task.setStatus(request.getStatus());
            task.setPriority(taskRepository.getCountByPostAndStatus(task.getProject(), request.getStatus()));
        }

        taskRepository.save(task);
    }

    // 태스크 상태 업데이트
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    public void updateTaskStatus(Long id, TaskDTO.TaskDropLocation request, User currentUser) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TASK", "ID", id));

        // 멤버만 가능
        projectService.validateProjectMember(task.getProject(), currentUser);

        if (request.getStatus().equals(task.getStatus())) {
            if(request.getPriority() == task.getPriority()) return ;
            if (request.getPriority() > task.getPriority()) {
                taskRepository.reorderBackInSameStatus(task.getProject(),task.getStatus(),task.getPriority(),request.getPriority());
            } else {
                taskRepository.reorderFrontInSameStatus(task.getProject(),task.getStatus(),task.getPriority(),request.getPriority());
            }
        } else {
            taskRepository.reorderInPreviousStatus(task.getProject(),task.getStatus(),task.getPriority()); // 기존 status 정리
            taskRepository.reorderInNewStatus(task.getProject(),request.getStatus(),request.getPriority()); // 기존 status 정리
            task.setStatus(request.getStatus());
        }
        task.setPriority(request.getPriority());
        taskRepository.save(task);
    }

    // 태스크 삭제
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    public ApiResponse deleteTask(Long id, User currentUser) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TASK", "ID", id));

        // 멤버만 가능
        projectService.validateProjectMember(task.getProject(), currentUser);

        taskRepository.reorderInPreviousStatus(task.getProject(),task.getStatus(),task.getPriority()); // 기존 status 정리
        taskRepository.delete(task);
        return new ApiResponse(Boolean.TRUE, "task 삭제 성공");
    }
}
