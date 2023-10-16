package ru.veselov.generatebytemplate.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import ru.veselov.generatebytemplate.TestUtils;
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
        generatedResultFileService.save(byteArrayResource, basicGeneratedResultFile);


        Mockito.verify(generatedResultFileStorageService, Mockito.times(1))
                .saveUnSynced(basicGeneratedResultFile);
        Mockito.verify(generatedResultFileMinioService, Mockito.times(1))
                .saveResult(byteArrayResource, basicGeneratedResultFile);
        Mockito.verify(generatedResultFileStorageService, Mockito.times(1))
                .syncResultFile(ArgumentMatchers.any(UUID.class));
    }


}