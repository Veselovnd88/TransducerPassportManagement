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

import java.time.LocalDateTime;

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
        Task task = new Task(TestUtils.TASK_ID, false, false, TestUtils.PRINT_DATE, LocalDateTime.now(), LocalDateTime.now());
        Mockito.when(taskService.createTask(generatePassportsDto, TestUtils.USERNAME))
                .thenReturn(task);

        taskLaunchService.startTask(generatePassportsDto, TestUtils.USERNAME);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(taskService).createTask(generatePassportsDto, TestUtils.USERNAME),
                () -> Mockito.verify(generateServiceHttpClient)
                        .sendTaskToPerform(generatePassportsDto, task, TestUtils.USERNAME),
                () -> Mockito.verify(taskService).updateStatusToStart(TestUtils.TASK_ID)
        );
    }

    @Test
    void shouldThrowExceptionIfTaskWasNotStarted() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        Task task = new Task(TestUtils.TASK_ID, false, false, TestUtils.PRINT_DATE, LocalDateTime.now(), LocalDateTime.now());
        Mockito.when(taskService.createTask(generatePassportsDto, TestUtils.USERNAME))
                .thenReturn(task);
        Mockito.doThrow(GenerateServiceException.class).when(generateServiceHttpClient)
                .sendTaskToPerform(Mockito.any(), Mockito.any(), Mockito.any());

        Assertions.assertThatThrownBy(() ->
                taskLaunchService.startTask(generatePassportsDto, TestUtils.USERNAME)
        ).isInstanceOf(GenerateServiceException.class);

        Mockito.verify(taskService, Mockito.never()).updateStatusToStart(TestUtils.TASK_ID);
    }

}