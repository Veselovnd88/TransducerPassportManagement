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
import ru.veselov.generatebytemplate.TestUtils;
import ru.veselov.generatebytemplate.entity.ResultFileEntity;
import ru.veselov.generatebytemplate.entity.TemplateEntity;
import ru.veselov.generatebytemplate.exception.ResultFileNotFoundException;
import ru.veselov.generatebytemplate.exception.TemplateNotFoundException;
import ru.veselov.generatebytemplate.mapper.ResultFileMapper;
import ru.veselov.generatebytemplate.mapper.ResultFileMapperImpl;
import ru.veselov.generatebytemplate.model.ResultFile;
import ru.veselov.generatebytemplate.repository.ResultFileRepository;
import ru.veselov.generatebytemplate.repository.TemplateRepository;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ResultFileStorageServiceImplTest {

    @Mock
    ResultFileRepository resultFileRepository;

    @Mock
    TemplateRepository templateRepository;

    @InjectMocks
    ResultFileStorageServiceImpl resultFileStorageService;

    @Captor
    ArgumentCaptor<ResultFileEntity> resultFileEntityArgumentCaptor;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(resultFileStorageService,
                "resultFileMapper", new ResultFileMapperImpl(), ResultFileMapper.class);
        ReflectionTestUtils
                .setField(resultFileStorageService, "resultBucket", TestUtils.RESULT_BUCKET, String.class);
    }

    @Test
    void shouldSaveUnSyncedResultFile() {
        ResultFile resultFile = TestUtils.getBasicGeneratedResultFile();
        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setId(UUID.fromString(resultFile.getTemplateId()));

        Mockito.when(templateRepository.findById(UUID.fromString(resultFile.getTemplateId())))
                .thenReturn(Optional.of(templateEntity));

        resultFileStorageService.saveUnSynced(resultFile);

        Mockito.verify(resultFileRepository).save(resultFileEntityArgumentCaptor.capture());
        ResultFileEntity captured = resultFileEntityArgumentCaptor.getValue();
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(captured.getSynced()).isFalse(),
                () -> Assertions.assertThat(captured.getTemplateEntity()).isEqualTo(templateEntity),
                () -> Assertions.assertThat(captured.getBucket()).isEqualTo(TestUtils.RESULT_BUCKET)
        );
    }

    @Test
    void shouldThrowNotFoundExceptionIfTemplateNotExists() {
        ResultFile resultFile = TestUtils.getBasicGeneratedResultFile();
        Mockito.when(templateRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() ->
                        resultFileStorageService.saveUnSynced(resultFile))
                .isInstanceOf(TemplateNotFoundException.class);
    }

    @Test
    void shouldFindEntityAndSetSyncToTrue() {
        UUID fileUid = UUID.randomUUID();
        ResultFileEntity resultFileEntity = new ResultFileEntity();
        resultFileEntity.setSynced(false);
        resultFileEntity.setId(fileUid);
        Mockito.when(resultFileRepository.findById(fileUid))
                .thenReturn(Optional.of(resultFileEntity));

        resultFileStorageService.syncResultFile(fileUid);

        Mockito.verify(resultFileRepository).save(resultFileEntityArgumentCaptor.capture());
        ResultFileEntity captured = resultFileEntityArgumentCaptor.getValue();
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(captured.getId()).isEqualTo(resultFileEntity.getId()),
                () -> Assertions.assertThat(captured.getSynced()).isTrue()
        );
    }

    @Test
    void shouldThrowExceptionIfResultFileNotFoundWhileSyncing() {
        UUID fileUid = UUID.randomUUID();
        Mockito.when(resultFileRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() ->
                        resultFileStorageService.syncResultFile(fileUid))
                .isInstanceOf(ResultFileNotFoundException.class);
    }

    @Test
    void shouldFindResultFileById() {
        UUID fileUid = UUID.randomUUID();
        ResultFileEntity resultFileEntity = new ResultFileEntity();
        resultFileEntity.setId(fileUid);
        Mockito.when(resultFileRepository.findById(fileUid))
                .thenReturn(Optional.of(resultFileEntity));

        ResultFile foundById = resultFileStorageService.findById(fileUid.toString());

        Assertions.assertThat(foundById.getId()).isEqualTo(fileUid);
    }

    @Test
    void shouldThrowNotFoundExceptionIfResultNotFound() {
        String fileUid = UUID.randomUUID().toString();
        Mockito.when(resultFileRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() ->
                        resultFileStorageService.findById(fileUid))
                .isInstanceOf(ResultFileNotFoundException.class);
    }

}
