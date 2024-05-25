package com.app.teamlog.domain.task.service;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.account.service.query.AccountQueryService;
import com.app.teamlog.domain.project.entity.Project;
import com.app.teamlog.domain.project.service.query.ProjectQueryService;
import com.app.teamlog.domain.projectmember.service.query.ProjectMemberQueryService;
import com.app.teamlog.domain.task.dto.*;
import com.app.teamlog.domain.task.entity.Task;
import com.app.teamlog.domain.task.entity.TaskPerformer;
import com.app.teamlog.domain.task.entity.TaskStatus;
import com.app.teamlog.domain.task.repository.TaskRepository;
import com.app.teamlog.global.exception.BadRequestException;
import com.app.teamlog.global.exception.ResourceNotFoundException;
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
public class TaskService {
    private final TaskRepository taskRepository;

    private final ProjectQueryService projectQueryService;

    private final ProjectMemberQueryService projectMemberQueryService;

    private final AccountQueryService accountQueryService;

    public TaskReadDetailResult readOne(Long idx) {
        Task task = taskRepository.findById(idx)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 Task입니다. id: "+ idx));

        return TaskReadDetailResult.from(task);
    }

    @Transactional
    public TaskCreateResult create(TaskCreateInput input, Account account) {
        final Project project = prepareProject(input.getProjectId());

        checkProjectMember(account, project);

        final Task task = input.toTask(project, account);
        task.addTaskPerformerList(makeTaskPerformers(task, input.getPerformerIdList()));

        final Task savedTask = taskRepository.save(task);
        return TaskCreateResult.from(savedTask);
    }


    private List<TaskPerformer> makeTaskPerformers(Task task, List<String> performerIdentificationList) {
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

            performerList.add(TaskPerformer.builder().task(task).account(performerAccount).build());
        }

        if (!CollectionUtils.isEmpty(notfoundIdentificationList)) {
            throw new ResourceNotFoundException("Account");
        }
        return performerList;
    }

    @Transactional
    public TaskUpdateResult update(TaskUpdateInput input, Account account) {
        final Long taskId = input.getTaskId();

        final Task task = prepareTask(taskId);

        checkProjectMember(account, task.getProject());

        final List<TaskPerformer> newTaskPerformers = makeTaskPerformers(task, input.getPerformerIdList());

        task.update(input.getTaskName(), input.getDeadline());
        task.getTaskPerformers().clear();
        task.addTaskPerformerList(newTaskPerformers);

        return TaskUpdateResult.from(task);
    }

    public List<TaskReadByProjectResult> readAllByProject(Long projectId) {
        final Project project = prepareProject(projectId);

        final List<Task> taskList = taskRepository.findAllByProject(project);

        return taskList.stream().map(TaskReadByProjectResult::from).collect(Collectors.toList());
    }

    public boolean updateStatus(Long taskId, Account account, TaskStatus status) {
        final Task task = prepareTask(taskId);

        checkProjectMember(account, task.getProject());

        if (task.getStatus() == status) {
            log.info("Task status is already {}", status);
            return false;
        }

        task.setStatus(status);

        return true;
    }

    public boolean delete(Long taskId, Account account) {
        final Task task = prepareTask(taskId);

        checkProjectMember(account, task.getProject());

        taskRepository.delete(task);
        return true;
    }

    private void checkProjectMember(Account account, Project project) {
        if (!projectMemberQueryService.isProjectMember(project, account)) throw new BadRequestException("프로젝트 멤버가 아닙니다.");
    }
    
    private Project prepareProject(Long projectId) {
        return projectQueryService.findById(projectId)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 프로젝트입니다. id: "+ projectId));
    }

    private Task prepareTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 Task입니다. id: "+ taskId));
    }
}
