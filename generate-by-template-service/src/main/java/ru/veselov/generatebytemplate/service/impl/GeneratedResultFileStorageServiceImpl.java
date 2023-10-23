package ru.veselov.generatebytemplate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.generatebytemplate.entity.GeneratedResultFileEntity;
import ru.veselov.generatebytemplate.entity.TemplateEntity;
import ru.veselov.generatebytemplate.exception.ResultFileNotFoundException;
import ru.veselov.generatebytemplate.exception.TemplateNotFoundException;
import ru.veselov.generatebytemplate.mapper.GeneratedResultFileMapper;
import ru.veselov.generatebytemplate.model.GeneratedResultFile;
import ru.veselov.generatebytemplate.repository.GeneratedResultFileRepository;
import ru.veselov.generatebytemplate.repository.TemplateRepository;
import ru.veselov.generatebytemplate.service.GeneratedResultFileStorageService;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GeneratedResultFileStorageServiceImpl implements GeneratedResultFileStorageService {

    @Value("${minio.buckets.result}")
    private String resultBucket;

    private static final String LOG_FILE_NOT_FOUND_MSG = "Generated result file with [id: {}] not found";

    private static final String EXCEPTION_FILE_NOT_FOUND_MSG = "Generated result file with [id: %s] not found";

    private final GeneratedResultFileRepository generatedResultFileRepository;

    private final TemplateRepository templateRepository;

    private final GeneratedResultFileMapper generatedResultFileMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public GeneratedResultFileEntity saveUnSynced(GeneratedResultFile resultFile) {
        UUID templateUUID = UUID.fromString(resultFile.getTemplateId());
        Optional<TemplateEntity> templateEntityOptional = templateRepository.findById(templateUUID);
        TemplateEntity templateEntity = templateEntityOptional.orElseThrow(() -> {
            log.error("Template with such [id: {}] not found", templateUUID);
            return new TemplateNotFoundException("Template with such [id: %s] not found".formatted(templateUUID));
        });
        GeneratedResultFileEntity resultFileEntity = generatedResultFileMapper.toEntity(resultFile);
        resultFileEntity.setSynced(false);
        resultFileEntity.setTemplateEntity(templateEntity);
        resultFileEntity.setBucket(resultBucket);
        GeneratedResultFileEntity saved = generatedResultFileRepository.save(resultFileEntity);
        log.info("Saved unsynced generated file result for [template: {}, filename: {}, id: {}]",
                templateUUID, resultFileEntity.getFilename(), resultFileEntity.getId());
        return saved;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public GeneratedResultFile syncResultFile(UUID resultFileId) {
        Optional<GeneratedResultFileEntity> fileEntityOptional = generatedResultFileRepository.findById(resultFileId);
        GeneratedResultFileEntity resultFileEntity = fileEntityOptional.orElseThrow(() -> {
            log.error(LOG_FILE_NOT_FOUND_MSG, resultFileId);
            return new ResultFileNotFoundException(EXCEPTION_FILE_NOT_FOUND_MSG.formatted(resultFileId));
        });
        resultFileEntity.setSynced(true);
        GeneratedResultFileEntity savedAndSynced = generatedResultFileRepository.save(resultFileEntity);
        return generatedResultFileMapper.toModel(savedAndSynced);
    }

    @Override
    public GeneratedResultFile findById(String resultFileId) {
        UUID resultFileUUID = UUID.fromString(resultFileId);
        Optional<GeneratedResultFileEntity> resultFileEntityOptional = generatedResultFileRepository
                .findById(resultFileUUID);
        GeneratedResultFileEntity resultFileEntity = resultFileEntityOptional.orElseThrow(() -> {
            log.error(LOG_FILE_NOT_FOUND_MSG, resultFileId);
            return new ResultFileNotFoundException(EXCEPTION_FILE_NOT_FOUND_MSG.formatted(resultFileId));
        });
        log.info("Found result file with [id: {}]", resultFileId);
        return generatedResultFileMapper.toModel(resultFileEntity);
    }

}
