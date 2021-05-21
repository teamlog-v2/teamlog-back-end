package com.test.teamlog.service;

import com.test.teamlog.entity.*;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.TaskDTO;
import com.test.teamlog.repository.ProjectRepository;
import com.test.teamlog.repository.TaskRepository;
import com.test.teamlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    // 태스크 상세 조회
    public TaskDTO.TaskResponse getTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        return new TaskDTO.TaskResponse(task);
    }

    // 프로젝트의 태스크들 조회
    public List<TaskDTO.TaskResponse> getTasksByProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PROJECT", "id", id));

        List<Task> tasks = taskRepository.findByProject(project);
        List<TaskDTO.TaskResponse> responses = new ArrayList<>();
        for (Task t : tasks) {
            TaskDTO.TaskResponse taskResponse = new TaskDTO.TaskResponse(t);
            responses.add(taskResponse);
        }
        return responses;
    }

    // 태스크 생성
    @Transactional
    public TaskDTO.TaskResponse createTask(Long projectId, TaskDTO.TaskRequest request) {
        // project memeber 인지 확인하는 것으로 대체 하자 위의 코드.
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("PROJECT", "id", projectId));
        // TODO : post status 비교 priority 주기.
        Task task = Task.builder()
                .taskName(request.getTaskName())
                .status(request.getStatus())
                .priority(taskRepository.getCountByPostAndStatus(project, request.getStatus()))
                .project(project)
                .build();
        List<TaskPerformer> performers = new ArrayList<>();
        if (request.getPerformersId() != null) {
            for (String userId : request.getPerformersId()) {
                User tempUser = userRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("USER", "id", userId));
                TaskPerformer performer = TaskPerformer.builder()
                        .task(task)
                        .user(tempUser)
                        .build();
                performers.add(performer);
            }
            task.setTaskPerformers(performers);
        }
        Task result = taskRepository.save(task);

        return new TaskDTO.TaskResponse(result);
    }

    // 태스크 수정
    @Transactional
    public void updateTask(Long taskId, TaskDTO.TaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("TASK", "ID", taskId));
        // TODO : 마지막에다가 그냥 밀어 넣기
        if (!request.getStatus().equals(task.getStatus())) {
            task.setStatus(request.getStatus());
            task.setPriority(taskRepository.getCountByPostAndStatus(task.getProject(), request.getStatus()));
            // TODO : 기존 꺼는 밀기
        }

        task.setDeadline(request.getDeadline());
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
                    if (newTaskPerformersId.contains(taskPerformer.getUser().getId())) {
                        maintainedTaskPerformersId.add(taskPerformer.getUser().getId());
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
                    User performer = userRepository.findById(performerId)
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

        taskRepository.save(task);
    }

    // TODO : query value 값 받아서 뭐든 하는 걸로 바꾸자
    // 태스크 상태 업데이트
    @Transactional
    public void updateTaskStatus(Long id, TaskDTO.TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TASK", "ID", id));
        if (request.getStatus().equals(task.getStatus())) {
            // TODO : priority 값만 받고 민다.
        } else {
            // TODO : priority 값과 status값을 받고 민다.
        }
        task.setStatus(request.getStatus());
        taskRepository.save(task);
    }

    // 태스크 삭제
    @Transactional
    public ApiResponse deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TASK", "ID", id));
        // TODO : 허가된 사용자인지 검증해야함..
        // TODO : validateUser
        taskRepository.delete(task);
        return new ApiResponse(Boolean.TRUE, "task 삭제 성공");
    }
}
