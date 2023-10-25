package ru.veselov.generatebytemplate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import ru.veselov.generatebytemplate.entity.ResultFileEntity;
import ru.veselov.generatebytemplate.model.ResultFile;
import ru.veselov.generatebytemplate.service.ResultFileMinioService;
import ru.veselov.generatebytemplate.service.ResultFileService;
import ru.veselov.generatebytemplate.service.ResultFileStorageService;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResultFileServiceImpl implements ResultFileService {

    private final ResultFileStorageService resultFileStorageService;

    private final ResultFileMinioService resultFileMinioService;

    @Override
    public ResultFile save(ByteArrayResource pdfBytes, ResultFile resultFile) {
        ResultFileEntity resultFileEntity = resultFileStorageService.saveUnSynced(resultFile);
        resultFileMinioService.saveResult(pdfBytes, resultFile);
        ResultFile syncedResultFile = resultFileStorageService.syncResultFile(resultFileEntity.getId());
        log.info("Generated [file: {}] successfully saved and sync with DB and MinIO", syncedResultFile.getId());
        return syncedResultFile;
    }

    @Override
    public ByteArrayResource getResultFile(String resultFileId) {
        ResultFile generatedResultFile = resultFileStorageService.findById(resultFileId);
        ByteArrayResource resultFile = resultFileMinioService.loadResultFile(generatedResultFile);
        log.info("Generated file retrieved from storage: [id: {}]", resultFileId);
        return resultFile;
    }

}
