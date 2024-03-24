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
import com.test.teamlog.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceV2 {
    private final TaskRepository taskRepository;

    private final ProjectQueryService projectQueryService;

    private final ProjectMemberQueryService projectMemberQueryService;

    private final AccountQueryService accountQueryService;

    public TaskReadDetailResult readDetail(Long idx) {
        Task task = taskRepository.findById(idx)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", idx));

        return TaskReadDetailResult.from(task);
    }

    @Transactional
    public TaskCreateResult create(TaskCreateInput input, Account account) {
        final Long projectId = input.getProjectId();
        final Project project = projectQueryService.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        if (!projectMemberQueryService.isProjectMember(project, account)) {
            throw new ResourceNotFoundException("ProjectMember", "account", account);
        }

        final Task task = input.toTask(project);
        task.addTaskPerformerList(makeTaskPerformers(input.getPerformerIdList()));

        final Task savedTask = taskRepository.save(task);
        return TaskCreateResult.from(savedTask);
    }


    private List<TaskPerformer> makeTaskPerformers(List<String> performerIdentificationList) {
        Map<String, Account> identificationToAccountMap
                = accountQueryService.findAllByIdentificationIn(performerIdentificationList).stream()
                .collect(Collectors.toMap(Account::getIdentification, Function.identity()));

        List<String> notfoundIdentificationList = new ArrayList<>();
        List<TaskPerformer> performerList = new ArrayList<>();

        for (String performerId : performerIdentificationList) {
            final Account performerAccount = identificationToAccountMap.get(performerId);

            if (performerAccount == null) {
                notfoundIdentificationList.add(performerId);
                continue;
            }

            performerList.add(TaskPerformer.builder().account(performerAccount).build());
        }

        if (!CollectionUtils.isEmpty(notfoundIdentificationList)) {
            throw new ResourceNotFoundException("Account", "identification", notfoundIdentificationList);
        }
        return performerList;
    }

    @Transactional
    public TaskUpdateResult update(TaskUpdateInput input, Account account) {
        final Long taskId = input.getTaskId();

        final Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        final Project project = task.getProject();
        if (!projectMemberQueryService.isProjectMember(project, account)) {
            throw new ResourceNotFoundException("ProjectMember", "account", account);
        }

        final List<TaskPerformer> newTaskPerformers = makeTaskPerformers(input.getPerformerIdList());

        task.update(input.getTaskName(), input.getDeadline());
        task.getTaskPerformers().clear();
        task.addTaskPerformerList(newTaskPerformers);

        return TaskUpdateResult.from(task);
    }

    public List<TaskReadByProjectResult> readAllByProject(Long projectId) {
        final Project project = projectQueryService.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        final List<Task> taskList = taskRepository.findAllByProject(project);

        return taskList.stream().map(TaskReadByProjectResult::from).collect(Collectors.toList());
    }

    public boolean updateStatus(Long taskId, TaskStatus status) {
        final Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        if (task.getStatus() == status) {
            log.info("Task status is already {}", status);
            return false;
        }

        task.setStatus(status);
        return true;
    }

    public boolean delete(Long taskId, Account account) {
        final Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        final Project project = task.getProject();
        if (!projectMemberQueryService.isProjectMember(project, account)) {
            throw new ResourceNotFoundException("ProjectMember", "account", account);
        }

        taskRepository.delete(task);
        return true;
    }
}
