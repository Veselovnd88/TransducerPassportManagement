package ru.veselov.taskservice.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.veselov.taskservice.TestUtils;
import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.exception.GenerateServiceException;
import ru.veselov.taskservice.model.Task;
import ru.veselov.taskservice.service.GenerateServiceHttpClient;
import ru.veselov.taskservice.service.TaskService;

@ExtendWith(MockitoExtension.class)
class TaskLaunchServiceImplTest {

    @Mock
    TaskService taskService;

    @Mock
    GenerateServiceHttpClient generateServiceHttpClient;

    @InjectMocks
    TaskLaunchServiceImpl taskLaunchService;

    @Test
    void shouldLaunchTask() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        Task task = TestUtils.getTask();
        Mockito.when(taskService.createTask(generatePassportsDto, TestUtils.USERNAME))
                .thenReturn(task);

        taskLaunchService.launchTask(generatePassportsDto, TestUtils.USERNAME);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(taskService).createTask(generatePassportsDto, TestUtils.USERNAME),
                () -> Mockito.verify(generateServiceHttpClient)
                        .sendTaskToPerform(generatePassportsDto, task, TestUtils.USERNAME),
                () -> Mockito.verify(taskService).updateStatusToStarted(TestUtils.TASK_ID)
        );
    }

    @Test
    void shouldThrowExceptionIfTaskWasNotStarted() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        Task task = TestUtils.getTask();
        Mockito.when(taskService.createTask(generatePassportsDto, TestUtils.USERNAME))
                .thenReturn(task);
        Mockito.doThrow(GenerateServiceException.class).when(generateServiceHttpClient)
                .sendTaskToPerform(Mockito.any(), Mockito.any(), Mockito.any());

        Assertions.assertThatThrownBy(() ->
                taskLaunchService.launchTask(generatePassportsDto, TestUtils.USERNAME)
        ).isInstanceOf(GenerateServiceException.class);

        Mockito.verify(taskService, Mockito.never()).updateStatusToStarted(TestUtils.TASK_ID);
    }

}