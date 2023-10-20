package ru.veselov.generatebytemplate.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.generatebytemplate.TestUtils;
import ru.veselov.generatebytemplate.entity.GeneratedResultFileEntity;
import ru.veselov.generatebytemplate.entity.TemplateEntity;
import ru.veselov.generatebytemplate.mapper.GeneratedResultFileMapper;
import ru.veselov.generatebytemplate.mapper.GeneratedResultFileMapperImpl;
import ru.veselov.generatebytemplate.model.GeneratedResultFile;
import ru.veselov.generatebytemplate.repository.GeneratedResultFileRepository;
import ru.veselov.generatebytemplate.repository.TemplateRepository;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class GeneratedResultFileStorageServiceImplTest {

    @Mock
    GeneratedResultFileRepository generatedResultFileRepository;

    @Mock
    TemplateRepository templateRepository;

    @InjectMocks
    GeneratedResultFileStorageServiceImpl generatedResultFileStorageService;

    @Captor
    ArgumentCaptor<GeneratedResultFileEntity> resultFileEntityArgumentCaptor;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(generatedResultFileStorageService,
                "generatedResultFileMapper", new GeneratedResultFileMapperImpl(), GeneratedResultFileMapper.class);
        ReflectionTestUtils
                .setField(generatedResultFileStorageService, "resultBucket", TestUtils.RESULT_BUCKET, String.class);
    }

    @Test
    void shouldSaveUnSyncedResultFile() {
        GeneratedResultFile generatedResultFile = TestUtils.getBasicGeneratedResultFile();
        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setId(UUID.fromString(generatedResultFile.getTemplateId()));

        Mockito.when(templateRepository.findById(UUID.fromString(generatedResultFile.getTemplateId())))
                .thenReturn(Optional.of(templateEntity));

        generatedResultFileStorageService.saveUnSynced(generatedResultFile);

        Mockito.verify(generatedResultFileRepository, Mockito.times(1)).save(resultFileEntityArgumentCaptor.capture());
        GeneratedResultFileEntity captured = resultFileEntityArgumentCaptor.getValue();
        Assertions.assertThat(captured.getSynced()).isFalse();
        Assertions.assertThat(captured.getTemplateEntity()).isEqualTo(templateEntity);
        Assertions.assertThat(captured.getBucket()).isEqualTo(TestUtils.RESULT_BUCKET);
    }

    @Test
    void shouldThrowNotFoundExceptionIfTemplateNotExists() {
        GeneratedResultFile generatedResultFile = TestUtils.getBasicGeneratedResultFile();
        Mockito.when(templateRepository.findById(ArgumentMatchers.any()))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() ->
                        generatedResultFileStorageService.saveUnSynced(generatedResultFile))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldFindEntityAndSetSyncToTrue() {
        UUID fileUid = UUID.randomUUID();
        GeneratedResultFileEntity generatedResultFileEntity = new GeneratedResultFileEntity();
        generatedResultFileEntity.setSynced(false);
        generatedResultFileEntity.setId(fileUid);
        Mockito.when(generatedResultFileRepository.findById(fileUid))
                .thenReturn(Optional.of(generatedResultFileEntity));

        generatedResultFileStorageService.syncResultFile(fileUid);

        Mockito.verify(generatedResultFileRepository, Mockito.times(1)).save(resultFileEntityArgumentCaptor.capture());
        GeneratedResultFileEntity captured = resultFileEntityArgumentCaptor.getValue();
        Assertions.assertThat(captured.getId()).isEqualTo(generatedResultFileEntity.getId());
        Assertions.assertThat(captured.getSynced()).isTrue();
    }

    @Test
    void shouldThrowExceptionIfResultFileNotFoundWhileSyncing() {
        UUID fileUid = UUID.randomUUID();
        Mockito.when(generatedResultFileRepository.findById(ArgumentMatchers.any()))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() ->
                        generatedResultFileStorageService.syncResultFile(fileUid))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldFindResultFileById() {
        UUID fileUid = UUID.randomUUID();
        GeneratedResultFileEntity generatedResultFileEntity = new GeneratedResultFileEntity();
        generatedResultFileEntity.setId(fileUid);
        Mockito.when(generatedResultFileRepository.findById(fileUid))
                .thenReturn(Optional.of(generatedResultFileEntity));

        GeneratedResultFile foundById = generatedResultFileStorageService.findById(fileUid.toString());

        Assertions.assertThat(foundById.getId()).isEqualTo(fileUid);
    }

    @Test
    void shouldThrowNotFoundExceptionIfResultNotFound() {
        String fileUid = UUID.randomUUID().toString();
        Mockito.when(generatedResultFileRepository.findById(ArgumentMatchers.any()))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() ->
                        generatedResultFileStorageService.findById(fileUid))
                .isInstanceOf(EntityNotFoundException.class);
    }

}
