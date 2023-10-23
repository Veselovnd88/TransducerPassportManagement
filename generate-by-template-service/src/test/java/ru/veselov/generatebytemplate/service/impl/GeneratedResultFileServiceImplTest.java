package ru.veselov.generatebytemplate.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import ru.veselov.generatebytemplate.TestUtils;
import ru.veselov.generatebytemplate.entity.GeneratedResultFileEntity;
import ru.veselov.generatebytemplate.model.GeneratedResultFile;
import ru.veselov.generatebytemplate.service.GeneratedResultFileMinioService;
import ru.veselov.generatebytemplate.service.GeneratedResultFileStorageService;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class GeneratedResultFileServiceImplTest {

    @Mock
    GeneratedResultFileStorageService generatedResultFileStorageService;

    @Mock
    GeneratedResultFileMinioService generatedResultFileMinioService;

    @InjectMocks
    GeneratedResultFileServiceImpl generatedResultFileService;

    @Test
    void shouldSaveAndSyncFileRecord() {
        ByteArrayResource byteArrayResource = new ByteArrayResource(TestUtils.SOURCE_BYTES);
        GeneratedResultFile basicGeneratedResultFile = TestUtils.getBasicGeneratedResultFile();
        GeneratedResultFileEntity generatedResultFileEntity = new GeneratedResultFileEntity();
        generatedResultFileEntity.setId(UUID.randomUUID());
        Mockito.when(generatedResultFileStorageService.saveUnSynced(basicGeneratedResultFile))
                .thenReturn(generatedResultFileEntity);
        Mockito.when(generatedResultFileStorageService.syncResultFile(generatedResultFileEntity.getId()))
                .thenReturn(basicGeneratedResultFile);

        GeneratedResultFile resultFile = generatedResultFileService.save(byteArrayResource, basicGeneratedResultFile);

        Mockito.verify(generatedResultFileStorageService, Mockito.times(1))
                .saveUnSynced(basicGeneratedResultFile);
        Mockito.verify(generatedResultFileMinioService, Mockito.times(1))
                .saveResult(byteArrayResource, basicGeneratedResultFile);
        Mockito.verify(generatedResultFileStorageService, Mockito.times(1))
                .syncResultFile(generatedResultFileEntity.getId());
        Assertions.assertThat(resultFile).isEqualTo(basicGeneratedResultFile);
    }

    @Test
    void shouldGetResultFile() {
        GeneratedResultFile basicGeneratedResultFile = TestUtils.getBasicGeneratedResultFile();
        String resultFileUid = basicGeneratedResultFile.getId().toString();
        ByteArrayResource byteArrayResource = new ByteArrayResource(TestUtils.SOURCE_BYTES);
        Mockito.when(generatedResultFileMinioService.loadResultFile(basicGeneratedResultFile))
                .thenReturn(byteArrayResource);

        Mockito.when(generatedResultFileStorageService.findById(resultFileUid))
                .thenReturn(basicGeneratedResultFile);

        ByteArrayResource resultFile = generatedResultFileService.getResultFile(resultFileUid);

        Mockito.verify(generatedResultFileStorageService, Mockito.times(1))
                .findById(resultFileUid);
        Mockito.verify(generatedResultFileMinioService, Mockito.times(1))
                .loadResultFile(basicGeneratedResultFile);
        Assertions.assertThat(resultFile).isEqualTo(byteArrayResource);
    }

}