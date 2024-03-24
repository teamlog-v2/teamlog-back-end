package com.test.teamlog.domain.task.service;

import com.test.teamlog.domain.account.model.Account;
import com.test.teamlog.domain.account.service.query.AccountQueryService;
import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.domain.project.service.query.ProjectQueryService;
import com.test.teamlog.domain.projectmember.service.query.ProjectMemberQueryService;
import com.test.teamlog.domain.task.dto.*;
import com.test.teamlog.domain.task.entity.Task;
import com.test.teamlog.domain.task.entity.TaskPerformer;
import com.test.teamlog.domain.task.entity.TaskStatus;
import com.test.teamlog.domain.task.repository.TaskRepository;
import com.test.teamlog.global.dto.ApiResponse;
import com.test.teamlog.global.exception.ResourceNotFoundException;
import jakarta.persistence.LockModeType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// TODO: 기획 변경
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    private final AccountQueryService accountQueryService;
    private final ProjectQueryService projectQueryService;
    private final ProjectMemberQueryService projectMemberQueryService;

    // 태스크 상세 조회
    public TaskReadDetailResult readDetail(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        return TaskReadDetailResult.from(task);
    }

    /**
     * 정책 변경으로 사용 X
     * @param projectId
     * @return
     */
    // 프로젝트의 태스크들 조회
    @Deprecated(since = "2023-03-24", forRemoval = true)
    @Transactional
    public List<TaskReadByProjectResult> readAllByProject(Long projectId) {
        final Project project = projectQueryService.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("PROJECT", "id", projectId));

        Sort sort = Sort.by(Sort.Direction.ASC, "priority");
        List<Task> tasks = taskRepository.findByProject(project, sort);

        List<TaskReadByProjectResult> resultList = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getDeadline() != null &&
                    t.getDeadline().isBefore(LocalDateTime.now()) &&
                    (t.getStatus() == TaskStatus.IN_PROGRESS || t.getStatus() == TaskStatus.NOT_STARTED)
            ) {
                setTaskStatusFailed(t);
            }

            resultList.add(TaskReadByProjectResult.from(t));
        }

        return resultList;
    }

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    public void setTaskStatusFailed(Task task) {
        taskRepository.reorderInPreviousStatus(task.getProject(), task.getStatus(), task.getPriority()); // 기존 status 정리
        task.setStatus(TaskStatus.FAILED);
        task.setPriority(taskRepository.getCountByPostAndStatus(task.getProject(), TaskStatus.FAILED));
    }

    // 태스크 생성
    @Transactional
    public TaskCreateResult create(Long projectId, TaskCreateInput input, Account currentAccount) {
        Project project = projectQueryService.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("PROJECT", "id", projectId));
        // 멤버만 가능
        projectMemberQueryService.validateProjectMember(project, currentAccount);

        Task task = input.toTask();
        task.setPriority(taskRepository.getCountByPostAndStatus(project, input.getStatus()));
        task.setProject(project);

        List<TaskPerformer> performerList = new ArrayList<>();
        if (input.getPerformerIdList() != null) {
            final List<Account> accountList = accountQueryService.findAllByIdentificationIn(input.getPerformerIdList());
            final Map<String, Account> identificationToAccountMap
                    = accountList.stream().collect(Collectors.toMap(Account::getIdentification, Function.identity()));

            List<String> invalidIdentificationList = new ArrayList<>();
            for (String accountIdentification : input.getPerformerIdList()) {
                if (!identificationToAccountMap.containsKey(accountIdentification)) {
                    invalidIdentificationList.add(accountIdentification);
                    continue;
                }

                performerList.add(makeTaskPerformer(task, identificationToAccountMap.get(accountIdentification)));
            }

            if (!CollectionUtils.isEmpty(invalidIdentificationList)) {
                log.warn("Invalid identification list: {}", invalidIdentificationList);
            }

            task.addTaskPerformerList(performerList);
        }

        Task newTask = taskRepository.save(task);
        return TaskCreateResult.from(newTask);
    }

    private TaskPerformer makeTaskPerformer(Task task, Account account) {
        return TaskPerformer.builder()
                .task(task)
                .account(account)
                .build();
    }


    // 태스크 수정
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    public TaskUpdateResult update(Long taskId,
                                   TaskUpdateInput input,
                                   Account currentAccount) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("TASK", "ID", taskId));

        // 멤버만 가능
        projectMemberQueryService.validateProjectMember(task.getProject(), currentAccount);

        task.update(input.getTaskName(), input.getDeadline());

        final List<TaskPerformer> originalTaskPerformerList = task.getTaskPerformers();
        final List<String> newTaskPerformerIdList = input.getPerformerIdList();

        if (!CollectionUtils.isEmpty(originalTaskPerformerList)) {
            task.removeTaskPerformerList(originalTaskPerformerList);
        }

        List<TaskPerformer> taskPerformerList = new ArrayList<>();

        final List<Account> accountList = accountQueryService.findAllByIdentificationIn(newTaskPerformerIdList);
        final Map<String, Account> identificationToAccountMap
                = accountList.stream().collect(Collectors.toMap(Account::getIdentification, Function.identity()));
        List<String> invalidIdentificationList = new ArrayList<>();

        for (String performerId : newTaskPerformerIdList) {
            if (!identificationToAccountMap.containsKey(performerId)) {
                invalidIdentificationList.add(performerId);
                continue;
            }

            taskPerformerList.add(makeTaskPerformer(task, identificationToAccountMap.get(performerId)));
        }

        if (!CollectionUtils.isEmpty(invalidIdentificationList)) {
            log.warn("Invalid identification list: {}", invalidIdentificationList);
        }

        task.addTaskPerformerList(taskPerformerList);

        // status 변경 시 마지막으로 밀어넣기
        if (!input.getStatus().equals(task.getStatus())) {
            taskRepository.reorderInPreviousStatus(task.getProject(), task.getStatus(), task.getPriority()); // 기존 status 정리
            task.setStatus(input.getStatus());
            task.setPriority(taskRepository.getCountByPostAndStatus(task.getProject(), input.getStatus()));
        }

        return TaskUpdateResult.from(task);
    }

    // 태스크 상태 업데이트
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    public void updateTaskStatus(Long id, @Valid TaskUpdateStatusRequest request, Account currentAccount) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TASK", "ID", id));

        // 멤버만 가능
        projectMemberQueryService.validateProjectMember(task.getProject(), currentAccount);

        if (request.getStatus().equals(task.getStatus())) {
            if (request.getPriority() == task.getPriority()) return;
            if (request.getPriority() > task.getPriority()) {
                taskRepository.reorderBackInSameStatus(task.getProject(), task.getStatus(), task.getPriority(), request.getPriority());
            } else {
                taskRepository.reorderFrontInSameStatus(task.getProject(), task.getStatus(), task.getPriority(), request.getPriority());
            }
        } else {
            taskRepository.reorderInPreviousStatus(task.getProject(), task.getStatus(), task.getPriority()); // 기존 status 정리
            taskRepository.reorderInNewStatus(task.getProject(), request.getStatus(), request.getPriority()); // 기존 status 정리
            task.setStatus(request.getStatus());
        }

        task.setPriority(request.getPriority());
        taskRepository.save(task);
    }

    // 태스크 삭제
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    public ApiResponse delete(Long id, Account currentAccount) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TASK", "ID", id));

        // 멤버만 가능
        projectMemberQueryService.validateProjectMember(task.getProject(), currentAccount);

        taskRepository.reorderInPreviousStatus(task.getProject(), task.getStatus(), task.getPriority()); // 기존 status 정리
        taskRepository.delete(task);

        return new ApiResponse(Boolean.TRUE, "task 삭제 성공");
    }
}
