package ru.veselov.generatebytemplate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import ru.veselov.generatebytemplate.entity.GeneratedResultFileEntity;
import ru.veselov.generatebytemplate.model.GeneratedResultFile;
import ru.veselov.generatebytemplate.service.GeneratedResultFileMinioService;
import ru.veselov.generatebytemplate.service.GeneratedResultFileService;
import ru.veselov.generatebytemplate.service.GeneratedResultFileStorageService;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeneratedResultFileServiceImpl implements GeneratedResultFileService {

    private final GeneratedResultFileStorageService generatedResultFileStorageService;

    private final GeneratedResultFileMinioService generatedResultFileMinioService;

 //   private final NotificationService notificationService; TODO

    @Override
    public void save(ByteArrayResource pdfBytes, GeneratedResultFile generatedResultFile) {
        GeneratedResultFileEntity generatedResultFileEntity = generatedResultFileStorageService
                .saveUnSynced(generatedResultFile);
        generatedResultFileMinioService.saveResult(pdfBytes, generatedResultFile);
        generatedResultFileStorageService.syncResultFile(generatedResultFileEntity.getId());
        log.info("Generated file successfully saved and sync with DB and MinIO");
    }

    @Override
    public ByteArrayResource getResultFile(String resultFileId) {
        GeneratedResultFile generatedResultFile = generatedResultFileStorageService.findById(resultFileId);
        ByteArrayResource resultFile = generatedResultFileMinioService.loadResultFile(generatedResultFile);
        log.info("Generated file retrieved from storage: [id: {}]", resultFileId);
        return resultFile;
    }

}
