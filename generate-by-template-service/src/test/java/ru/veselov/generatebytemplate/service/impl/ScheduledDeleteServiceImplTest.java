package ru.veselov.generatebytemplate.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.generatebytemplate.repository.GeneratedResultFileRepository;
import ru.veselov.generatebytemplate.repository.TemplateRepository;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class ScheduledDeleteServiceImplTest {

    public static final int DELETE_DAYS = 5;

    @Mock
    TemplateRepository templateRepository;

    @Mock
    GeneratedResultFileRepository generatedResultFileRepository;

    @InjectMocks
    ScheduledDeleteServiceImpl scheduledDeleteService;

    @Captor
    ArgumentCaptor<LocalDateTime> localDateTimeArgumentCaptor;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(scheduledDeleteService, "daysUntilDeleteUnSyncTemplate", DELETE_DAYS, int.class);
        ReflectionTestUtils.setField(scheduledDeleteService, "daysUntilDeleteUnSyncResult", DELETE_DAYS, int.class);
        ReflectionTestUtils.setField(scheduledDeleteService, "daysUntilDeleteResult", DELETE_DAYS, int.class);
    }

    @Test
    void shouldDeleteUnSynchronizedTemplates() {
        scheduledDeleteService.deleteUnSynchronizedTemplates();

        Mockito.verify(templateRepository, Mockito.times(1))
                .deleteAllWithUnSyncFalse(localDateTimeArgumentCaptor.capture());
        LocalDateTime captured = localDateTimeArgumentCaptor.getValue();
        Assertions.assertThat(captured).isBeforeOrEqualTo(LocalDateTime.now().minusDays(DELETE_DAYS));
    }

    @Test
    void shouldDeleteUnSynchronizedGenerateResultFiles() {
        scheduledDeleteService.deleteUnSynchronizedGenerateResultFiles();

        Mockito.verify(generatedResultFileRepository, Mockito.times(1))
                .deleteAllWithUnSyncFalse(localDateTimeArgumentCaptor.capture());
        LocalDateTime captured = localDateTimeArgumentCaptor.getValue();
        Assertions.assertThat(captured).isBeforeOrEqualTo(LocalDateTime.now().minusDays(DELETE_DAYS));

    }

    @Test
    void shouldDeleteExpiredResultFiles() {
        scheduledDeleteService.deleteExpiredResultFiles();

        Mockito.verify(generatedResultFileRepository, Mockito.times(1))
                .deleteExpiredResultFiles(localDateTimeArgumentCaptor.capture());

        LocalDateTime captured = localDateTimeArgumentCaptor.getValue();
        Assertions.assertThat(captured).isBeforeOrEqualTo(LocalDateTime.now().minusDays(DELETE_DAYS));
    }

}