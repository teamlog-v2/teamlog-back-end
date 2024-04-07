package com.test.teamlog.domain.task.service;

import com.test.teamlog.domain.account.model.Account;
import com.test.teamlog.domain.account.service.query.AccountQueryService;
import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.domain.project.service.query.ProjectQueryService;
import com.test.teamlog.domain.projectmember.service.query.ProjectMemberQueryService;
import com.test.teamlog.domain.task.dto.*;
import com.test.teamlog.domain.task.entity.Task;
import com.test.teamlog.domain.task.entity.TaskStatus;
import com.test.teamlog.domain.task.repository.TaskRepository;
import com.test.teamlog.global.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectMemberQueryService projectMemberQueryService;

    @Mock
    private ProjectQueryService projectQueryService;

    @Mock
    private AccountQueryService accountQueryService;

    @InjectMocks
    private TaskService sut;

    @Nested
    @DisplayName("태스크 상세 조회")
    class ReadDetailTest {
        @Test
        @DisplayName("성공")
        void test01() {
            // given
            Long idx = 1L;
            final Task task = Task.builder().id(idx).status(TaskStatus.NOT_STARTED).build();
            given(taskRepository.findById(anyLong())).willReturn(Optional.of(task));

            // when
            final TaskReadDetailResult actual = sut.readOne(idx);

            // then
            assertEquals(actual.getId(), idx);
        }

        @Test
        @DisplayName("존재하지 않는 태스크")
        void test02() {
            // given
            Long idx = 1L;
            given(taskRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            // then
            assertThrows(ResourceNotFoundException.class, () -> sut.readOne(idx));
        }
    }

    @Nested
    @DisplayName("태스크 생성")
    class CreateTest {
        @Test
        @DisplayName("성공")
        void test01() {
            // given
            final TaskCreateInput input = makeCreateInput("테스트 코드 작성하기", List.of("duckling"), 1L, TaskStatus.NOT_STARTED);
            final List<Account> accountList = input.getPerformerIdList().stream().map(identification -> Account.builder().identification(identification).build()).toList();

            final Account account = Account.builder().build();
            final Project project = Project.builder().id(input.getProjectId()).build();

            given(projectQueryService.findById(anyLong())).willReturn(Optional.of(project));
            given(projectMemberQueryService.isProjectMember(any(Project.class), any(Account.class))).willReturn(true);
            given(accountQueryService.findAllByIdentificationIn(input.getPerformerIdList())).willReturn(accountList);
            given(taskRepository.save(any(Task.class))).willAnswer(invocation -> invocation.getArgument(0, Task.class));

            // when
            final TaskCreateResult actual = sut.create(input, account);

            // then
            then(taskRepository).should().save(argThat(
                    task ->
                            task.getTaskName().equals(input.getTaskName()) &&
                                    task.getProject().getId().equals(input.getProjectId()) &&
                                    task.getStatus().equals(input.getStatus()) &&
                                    task.getTaskPerformers().size() == input.getPerformerIdList().size()
            ));

            assertAll(
                    () -> assertEquals(input.getTaskName(), actual.getTaskName()),
                    () -> assertEquals(input.getDeadline(), actual.getDeadline()),
                    () -> assertEquals(input.getStatus().getValue(), actual.getStatus()),
                    () -> assertEquals(input.getPerformerIdList().size(), actual.getPerformers().size()));
        }

        @Test
        @DisplayName("존재하지 않는 프로젝트")
        void test02() {
            // given
            long projectId = 1L;
            final List<String> performerIdentificationList = List.of("duck", "duckling");
            String taskName = "테스트 코드 작성하기";
            TaskStatus status = TaskStatus.NOT_STARTED;

            final TaskCreateInput input = makeCreateInput(taskName, performerIdentificationList, projectId, status);

            final Account account = Account.builder().build();

            given(projectQueryService.findById(projectId)).willThrow(ResourceNotFoundException.class);

            // when
            // then
            assertThrows(ResourceNotFoundException.class, () -> sut.create(input, account));
        }

        @Test
        @DisplayName("생성자가 프로젝트 멤버가 아닐 경우 실패")
        void tes03() {
            // given
            long projectId = 1L;
            final List<String> performerIdentificationList = List.of("duck", "duckling");
            String taskName = "테스트 코드 작성하기";
            TaskStatus status = TaskStatus.NOT_STARTED;

            final TaskCreateInput input = makeCreateInput(taskName, performerIdentificationList, projectId, status);

            final Account account = Account.builder().build();
            final Project project = Project.builder().id(projectId).build();

            given(projectQueryService.findById(anyLong())).willReturn(Optional.of(project));
            given(projectMemberQueryService.isProjectMember(any(Project.class), any(Account.class))).willReturn(false);

            // when
            // then
            assertThrows(ResourceNotFoundException.class, () -> sut.create(input, account));
        }

        @Test
        @DisplayName("수행자 중 존재하지 않는 멤버가 포함될 경우")
        void test() {
            // given
            Long projectId = 1L;
            final List<String> performerIdentificationList = List.of("duckling");
            String taskName = "테스트 코드 작성하기";
            TaskStatus status = TaskStatus.NOT_STARTED;

            final TaskCreateInput input = makeCreateInput(taskName, performerIdentificationList, projectId, status);

            final Account account = Account.builder().build();
            final Project project = Project.builder().id(projectId).build();

            given(projectQueryService.findById(anyLong())).willReturn(Optional.of(project));
            given(projectMemberQueryService.isProjectMember(any(Project.class), any(Account.class))).willReturn(true);
            given(accountQueryService.findAllByIdentificationIn(anyList())).willReturn(Collections.emptyList());

            // when
            // then
            assertThrows(ResourceNotFoundException.class, () -> sut.create(input, account));
        }

        private TaskCreateInput makeCreateInput(String taskName,
                                                List<String> performerIdList,
                                                long projectId,
                                                TaskStatus status) {
            final TaskCreateInput input = new TaskCreateInput();
            input.setTaskName(taskName);
            input.setPerformerIdList(performerIdList);
            input.setProjectId(projectId);
            input.setStatus(status);
            input.setDeadline(LocalDateTime.now());

            return input;
        }
    }

    @Nested
    @DisplayName("태스크 수정")
    class UpdateTest {
        @Test
        @DisplayName("성공")
        void test01() {
            // given
            final Account account = Account.builder().build();
            final List<String> performerIdList = List.of("duck");
            final long taskId = 1L;

            final TaskUpdateInput input = makeUpdateInput(taskId, "테스트 코드 작성하기", performerIdList);
            final Project expectedProject = Project.builder().build();
            final Task expectedTask = Task.builder().id(taskId).project(expectedProject).build();

            given(taskRepository.findById(anyLong())).willReturn(Optional.of(expectedTask));
            given(projectMemberQueryService.isProjectMember(any(Project.class), any(Account.class))).willReturn(true);
            given(accountQueryService.findAllByIdentificationIn(performerIdList)).willReturn(List.of(Account.builder().identification("duck").build()));

            // when
            final TaskUpdateResult actual = sut.update(input, account);

            // then
            assertAll(
                    () -> assertEquals(input.getTaskId(), actual.getTaskId()),
                    () -> assertEquals(input.getTaskName(), actual.getTaskName()),
                    () -> assertEquals(input.getTaskName(), actual.getTaskName()),
                    () -> assertEquals(input.getDeadline(), actual.getDeadline()),
                    () -> assertEquals(input.getPerformerIdList().size(), actual.getPerformers().size()));
        }

        @Test
        @DisplayName("존재하지 않는 태스크")
        void test02() {
            // given
            final TaskUpdateInput input = makeUpdateInput(1L, "테스트 코드 작성하기", List.of("duck"));

            given(taskRepository.findById(anyLong())).willThrow(ResourceNotFoundException.class);

            // when
            // then
            assertThrows(ResourceNotFoundException.class, () -> sut.update(input, Account.builder().build()));
        }

        @Test
        @DisplayName("수정자가 프로젝트 멤버가 아닐 경우")
        void test03() {
            // given
            final Account account = Account.builder().idx(1L).build();
            final TaskUpdateInput input = makeUpdateInput(1L, "테스트 코드 작성하기", List.of("duck"));

            given(taskRepository.findById(anyLong())).willReturn(Optional.of(Task.builder().project(Project.builder().build()).build()));
            given(projectMemberQueryService.isProjectMember(any(Project.class), any(Account.class))).willReturn(false);

            // when
            // then
            assertThrows(ResourceNotFoundException.class, () -> sut.update(input, account));
        }

        @Test
        @DisplayName("존재하지 않는 태스크 수행자가 포함된 경우")
        void test04() {
            // given
            final List<String> performerIdList = List.of("duck", "duckling");
            final TaskUpdateInput input = makeUpdateInput(1L, "테스트 코드 작성하기", performerIdList);
            final Account account = Account.builder().build();

            given(taskRepository.findById(anyLong())).willReturn(Optional.of(Task.builder().project(Project.builder().build()).build()));
            given(projectMemberQueryService.isProjectMember(any(Project.class), any(Account.class))).willReturn(true);
            given(accountQueryService.findAllByIdentificationIn(performerIdList)).willReturn(List.of(Account.builder().identification("notFound").build()));

            // when
            // then
            assertThrows(ResourceNotFoundException.class, () -> sut.update(input, account));
        }

        private TaskUpdateInput makeUpdateInput(long taskId, String taskName, List<String> performerIdList) {
            final TaskUpdateInput input = new TaskUpdateInput();
            input.setTaskId(taskId);
            input.setTaskName(taskName);
            input.setDeadline(LocalDateTime.now());
            input.setPerformerIdList(performerIdList);

            return input;
        }
    }

    @Nested
    @DisplayName("프로젝트 내 태스크 조회")
    class ReadAllByProjectTest {
        @Test
        @DisplayName("성공")
        void test01() {
            // given
            Long projectId = 1L;
            List<Task> taskList = List.of(Task.builder().status(TaskStatus.NOT_STARTED).build());

            given(projectQueryService.findById(anyLong())).willReturn(Optional.of(mock(Project.class)));
            given(taskRepository.findAllByProject(any(Project.class))).willReturn(taskList);

            // when
            final List<TaskReadByProjectResult> actual = sut.readAllByProject(projectId);

            // then
            then(taskRepository).should().findAllByProject(any(Project.class));
            assertEquals(taskList.size(), actual.size());
        }

        @Test
        @DisplayName("존재하지 않는 프로젝트")
        void test02() {
            // given
            Long projectId = 1L;
            given(projectQueryService.findById(anyLong())).willThrow(ResourceNotFoundException.class);

            // when
            // then
            assertThrows(ResourceNotFoundException.class, () -> sut.readAllByProject(projectId));
        }
    }

    @Nested
    @DisplayName("태스크 상태 변경")
    class UpdateStatusTest {
        @Test
        @DisplayName("성공")
        void test01() {
            // given
            Long taskId = 1L;
            TaskStatus status = TaskStatus.IN_PROGRESS;
            final Account account = makeAccount(1L);
            final Task task = makeProject(TaskStatus.NOT_STARTED);
            
            given(taskRepository.findById(anyLong())).willReturn(Optional.of(task));
            given(projectMemberQueryService.isProjectMember(any(Project.class), any(Account.class))).willReturn(true);

            // when
            final boolean actual = sut.updateStatus(taskId, account, status);

            // then
            assertTrue(actual);
        }

        @Test
        @DisplayName("존재하지 않는 태스크")
        void test02() {
            // given
            Long taskId = 1L;
            TaskStatus status = TaskStatus.IN_PROGRESS;
            final Account account = makeAccount(1L);
            
            given(taskRepository.findById(anyLong())).willThrow(ResourceNotFoundException.class);

            // when
            // then
            assertThrows(ResourceNotFoundException.class, () -> sut.updateStatus(taskId, account, status));
        }

        @Test
        @DisplayName("기존 태스크의 상태가 요청 상태와 동일한 경우")
        void test03() {
            // given
            Long taskId = 1L;
            TaskStatus status = TaskStatus.IN_PROGRESS;
            final Account account = makeAccount(1L);
            final Task task = makeProject(TaskStatus.IN_PROGRESS);

            given(taskRepository.findById(anyLong())).willReturn(Optional.of(task));
            given(projectMemberQueryService.isProjectMember(any(Project.class), any(Account.class))).willReturn(true);

            // when
            final boolean actual = sut.updateStatus(taskId, account, status);

            // then
            assertFalse(actual);
        }

        private Task makeProject(TaskStatus inProgress) {
            return Task.builder().project(Project.builder().build()).status(inProgress).build();
        }

        private Account makeAccount(Long idx) {
            return Account.builder().idx(idx).build();
        }
    }

    @Nested
    @DisplayName("태스크 삭제")
    class DeleteTest {
        @Test
        @DisplayName("성공")
        void test01() {
            // given
            Long taskId = 1L;
            final Project expectedProject = Project.builder().build();
            final Task expectedTask = Task.builder().id(taskId).project(expectedProject).build();
            final Account account = Account.builder().build();

            given(taskRepository.findById(anyLong())).willReturn(Optional.of(expectedTask));
            given(projectMemberQueryService.isProjectMember(any(Project.class), any(Account.class))).willReturn(true);

            // when
            final boolean actual = sut.delete(taskId, account);

            // then
            then(taskRepository).should().delete(expectedTask);
            assertTrue(actual);
        }

        @Test
        @DisplayName("존재하지 않는 태스크")
        void test02() {
            // given
            Long taskId = 1L;
            final Account account = Account.builder().build();

            given(taskRepository.findById(anyLong())).willThrow(ResourceNotFoundException.class);

            // when
            // then
            assertThrows(ResourceNotFoundException.class, () -> sut.delete(taskId, account));
        }

        @Test
        @DisplayName("본인이 작성한 태스크가 아닌 경우")
        void test03() {
            // given
            Long taskId = 1L;
            final Account account = Account.builder().build();
            final Project expectedProject = Project.builder().build();
            final Task expectedTask = Task.builder().id(taskId).project(expectedProject).build();

            given(taskRepository.findById(anyLong())).willReturn(Optional.of(expectedTask));
            given(projectMemberQueryService.isProjectMember(any(Project.class), any(Account.class))).willReturn(false);

            // when
            // then
            assertThrows(ResourceNotFoundException.class, () -> sut.delete(taskId, account));
        }
    }
}