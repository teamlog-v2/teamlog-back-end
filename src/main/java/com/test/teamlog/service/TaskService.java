package com.test.teamlog.service;

import com.test.teamlog.entity.Project;
import com.test.teamlog.entity.Task;
import com.test.teamlog.entity.TaskPerformer;
import com.test.teamlog.entity.User;
import com.test.teamlog.exception.ResourceNotFoundException;
import com.test.teamlog.payload.ApiResponse;
import com.test.teamlog.payload.TaskDTO;
import com.test.teamlog.payload.UserDTO;
import com.test.teamlog.repository.ProjectRepository;
import com.test.teamlog.repository.TaskPerformerRepository;
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
    private final TaskPerformerRepository taskPerformerRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    // 태스크 상세 조회
    public TaskDTO.TaskResponse getTask(Long id){
        Task task = taskRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Task","id",id));

        List<UserDTO.UserSimpleInfo> performers = new ArrayList<>();
        for (TaskPerformer temp : task.getTaskPerformers()) {
            UserDTO.UserSimpleInfo userInfo = new UserDTO.UserSimpleInfo(temp.getUser());
            performers.add(userInfo);
        }

        TaskDTO.TaskResponse taskResponse = TaskDTO.TaskResponse.builder()
                .id(task.getId())
                .taskName(task.getTaskName())
                .status(task.getStatus().getValue())
                .updateTime(task.getUpdateTime())
                .deadline(task.getDeadline())
                .performers(performers)
                .build();
        return taskResponse;
    }

    // 프로젝트의 태스크들 조회
    public List<TaskDTO.TaskResponse> getTasksByProject(Long id){
        Project project = projectRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("PROJECT","id",id));

        List<Task> tasks = taskRepository.findByProject(project);
        List<TaskDTO.TaskResponse> responses = new ArrayList<>();
        for(Task t : tasks)
        {
            TaskDTO.TaskResponse taskResponse = TaskDTO.TaskResponse.builder()
                    .id(t.getId())
                    .taskName(t.getTaskName())
                    .status(t.getStatus().getValue())
                    .deadline(t.getDeadline())
                    .updateTime(t.getUpdateTime())
                    .build();
            responses.add(taskResponse);
        }
        return responses;
    }

    // 태스크 생성
    @Transactional
    public Long createTask(Long projectId, TaskDTO.TaskRequest request){
        // project memeber 인지 확인하는 것으로 대체 하자 위의 코드.
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new ResourceNotFoundException("PROJECT","id",projectId));

        Task task = Task.builder()
                .taskName(request.getTaskName())
                .status(request.getStatus())
                .project(project)
                .build();

        Task result = taskRepository.save(task);

        List<TaskPerformer> performers = new ArrayList<>();

        for(String userId : request.getPerformersId()) {
            User tempUser = userRepository.findById(userId)
                    .orElseThrow(()-> new ResourceNotFoundException("USER","id",userId));

            TaskPerformer performer = TaskPerformer.builder()
                    .task(task)
                    .user(tempUser)
                    .build();

            performers.add(performer);
        }
        taskPerformerRepository.saveAll(performers);

        return result.getId();
    }

    // 태스크 수정
    @Transactional
    public void updateTask(Long taskId, TaskDTO.TaskRequest request){
        Task task = taskRepository.findById(taskId)
                .orElseThrow(()-> new ResourceNotFoundException("TASK","ID",taskId));
        task.setStatus(request.getStatus());
        task.setDeadline(request.getDeadline());
        task.setTaskName(request.getTaskName());

        // 비교해서 없어진 것은 지워야함.
        List<TaskPerformer> performers = new ArrayList<>();
        for(String userId : request.getPerformersId()) {
            User tempUser = userRepository.findById(userId)
                    .orElseThrow(()-> new ResourceNotFoundException("USER","id",userId));

            TaskPerformer performer = TaskPerformer.builder()
                    .task(task)
                    .user(tempUser)
                    .build();

            performers.add(performer);
        }
        taskPerformerRepository.saveAll(performers);
        taskRepository.save(task);
    }

    // 태스크 상태 업데이트
    @Transactional
    public void updateTaskStatus(Long id, TaskDTO.TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("TASK","ID",id));
        task.setStatus(request.getStatus());
        taskRepository.save(task);
    }
    // 태스크 삭제
    @Transactional
    public ApiResponse deleteTask(Long id){
        Task task = taskRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("TASK","ID",id));
        // TODO : 허가된 사용자인지 검증해야함..
        taskRepository.delete(task);
        return new ApiResponse(Boolean.TRUE,"task 삭제 성공");
    }
}
