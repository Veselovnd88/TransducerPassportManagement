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
import ru.veselov.generatebytemplate.entity.ResultFileEntity;
import ru.veselov.generatebytemplate.model.ResultFile;
import ru.veselov.generatebytemplate.service.ResultFileMinioService;
import ru.veselov.generatebytemplate.service.ResultFileStorageService;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ResultFileServiceImplTest {

    @Mock
    ResultFileStorageService resultFileStorageService;

    @Mock
    ResultFileMinioService resultFileMinioService;

    @InjectMocks
    ResultFileServiceImpl generatedResultFileService;

    @Test
    void shouldSaveAndSyncFileRecord() {
        ByteArrayResource byteArrayResource = new ByteArrayResource(TestUtils.SOURCE_BYTES);
        ResultFile resultFile = TestUtils.getBasicGeneratedResultFile();
        ResultFileEntity resultFileEntity = new ResultFileEntity();
        resultFileEntity.setId(UUID.randomUUID());
        Mockito.when(resultFileStorageService.saveUnSynced(resultFile))
                .thenReturn(resultFileEntity);
        Mockito.when(resultFileStorageService.syncResultFile(resultFileEntity.getId()))
                .thenReturn(resultFile);

        ResultFile saved = generatedResultFileService.save(byteArrayResource, resultFile);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(resultFileStorageService).saveUnSynced(resultFile),
                () -> Mockito.verify(resultFileMinioService).saveResult(byteArrayResource, resultFile),
                () -> Mockito.verify(resultFileStorageService).syncResultFile(resultFileEntity.getId()),
                () -> Assertions.assertThat(saved).isEqualTo(resultFile)
        );

    }

    @Test
    void shouldGetResultFile() {
        ResultFile resultFile = TestUtils.getBasicGeneratedResultFile();
        String resultFileUid = resultFile.getId().toString();
        ByteArrayResource byteArrayResource = new ByteArrayResource(TestUtils.SOURCE_BYTES);
        Mockito.when(resultFileMinioService.loadResultFile(resultFile)).thenReturn(byteArrayResource);
        Mockito.when(resultFileStorageService.findById(resultFileUid)).thenReturn(resultFile);

        ByteArrayResource receivedResultFile = generatedResultFileService.getResultFile(resultFileUid);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(resultFileStorageService).findById(resultFileUid),
                () -> Mockito.verify(resultFileMinioService).loadResultFile(resultFile),
                () -> Assertions.assertThat(receivedResultFile).isEqualTo(byteArrayResource)
        );
    }

}
