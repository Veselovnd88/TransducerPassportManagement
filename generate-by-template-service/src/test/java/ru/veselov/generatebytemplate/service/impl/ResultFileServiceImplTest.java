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
        ResultFile basicResultFile = TestUtils.getBasicGeneratedResultFile();
        ResultFileEntity resultFileEntity = new ResultFileEntity();
        resultFileEntity.setId(UUID.randomUUID());
        Mockito.when(resultFileStorageService.saveUnSynced(basicResultFile))
                .thenReturn(resultFileEntity);
        Mockito.when(resultFileStorageService.syncResultFile(resultFileEntity.getId()))
                .thenReturn(basicResultFile);

        ResultFile resultFile = generatedResultFileService.save(byteArrayResource, basicResultFile);

        Mockito.verify(resultFileStorageService, Mockito.times(1))
                .saveUnSynced(basicResultFile);
        Mockito.verify(resultFileMinioService, Mockito.times(1))
                .saveResult(byteArrayResource, basicResultFile);
        Mockito.verify(resultFileStorageService, Mockito.times(1))
                .syncResultFile(resultFileEntity.getId());
        Assertions.assertThat(resultFile).isEqualTo(basicResultFile);
    }

    @Test
    void shouldGetResultFile() {
        ResultFile basicResultFile = TestUtils.getBasicGeneratedResultFile();
        String resultFileUid = basicResultFile.getId().toString();
        ByteArrayResource byteArrayResource = new ByteArrayResource(TestUtils.SOURCE_BYTES);
        Mockito.when(resultFileMinioService.loadResultFile(basicResultFile))
                .thenReturn(byteArrayResource);

        Mockito.when(resultFileStorageService.findById(resultFileUid))
                .thenReturn(basicResultFile);

        ByteArrayResource resultFile = generatedResultFileService.getResultFile(resultFileUid);

        Mockito.verify(resultFileStorageService, Mockito.times(1))
                .findById(resultFileUid);
        Mockito.verify(resultFileMinioService, Mockito.times(1))
                .loadResultFile(basicResultFile);
        Assertions.assertThat(resultFile).isEqualTo(byteArrayResource);
    }

}