package ru.veselov.generatebytemplate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.generatebytemplate.entity.ResultFileEntity;
import ru.veselov.generatebytemplate.entity.TemplateEntity;
import ru.veselov.generatebytemplate.exception.ResultFileNotFoundException;
import ru.veselov.generatebytemplate.exception.TemplateNotFoundException;
import ru.veselov.generatebytemplate.mapper.ResultFileMapper;
import ru.veselov.generatebytemplate.model.ResultFile;
import ru.veselov.generatebytemplate.repository.ResultFileRepository;
import ru.veselov.generatebytemplate.repository.TemplateRepository;
import ru.veselov.generatebytemplate.service.ResultFileStorageService;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ResultFileStorageServiceImpl implements ResultFileStorageService {

    @Value("${minio.buckets.result}")
    private String resultBucket;

    private static final String LOG_FILE_NOT_FOUND_MSG = "Generated result file with [id: {}] not found";

    private static final String EXCEPTION_FILE_NOT_FOUND_MSG = "Generated result file with [id: %s] not found";

    private final ResultFileRepository resultFileRepository;

    private final TemplateRepository templateRepository;

    private final ResultFileMapper resultFileMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResultFileEntity saveUnSynced(ResultFile resultFile) {
        UUID templateUUID = UUID.fromString(resultFile.getTemplateId());
        Optional<TemplateEntity> templateEntityOptional = templateRepository.findById(templateUUID);
        TemplateEntity templateEntity = templateEntityOptional.orElseThrow(() -> {
            log.error("Template with such [id: {}] not found", templateUUID);
            return new TemplateNotFoundException("Template with such [id: %s] not found".formatted(templateUUID));
        });
        ResultFileEntity resultFileEntity = resultFileMapper.toEntity(resultFile);
        resultFileEntity.setSynced(false);
        resultFileEntity.setTemplateEntity(templateEntity);
        resultFileEntity.setBucket(resultBucket);
        ResultFileEntity saved = resultFileRepository.save(resultFileEntity);
        log.info("Saved unsynced generated file result for [template: {}, filename: {}, id: {}]",
                templateUUID, resultFileEntity.getFilename(), resultFileEntity.getId());
        return saved;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResultFile syncResultFile(UUID resultFileId) {
        Optional<ResultFileEntity> fileEntityOptional = resultFileRepository.findById(resultFileId);
        ResultFileEntity resultFileEntity = fileEntityOptional.orElseThrow(() -> {
            log.error(LOG_FILE_NOT_FOUND_MSG, resultFileId);
            return new ResultFileNotFoundException(EXCEPTION_FILE_NOT_FOUND_MSG.formatted(resultFileId));
        });
        resultFileEntity.setSynced(true);
        ResultFileEntity savedAndSynced = resultFileRepository.save(resultFileEntity);
        return resultFileMapper.toModel(savedAndSynced);
    }

    @Override
    public ResultFile findById(String resultFileId) {
        UUID resultFileUUID = UUID.fromString(resultFileId);
        Optional<ResultFileEntity> resultFileEntityOptional = resultFileRepository
                .findById(resultFileUUID);
        ResultFileEntity resultFileEntity = resultFileEntityOptional.orElseThrow(() -> {
            log.error(LOG_FILE_NOT_FOUND_MSG, resultFileId);
            return new ResultFileNotFoundException(EXCEPTION_FILE_NOT_FOUND_MSG.formatted(resultFileId));
        });
        log.info("Found result file with [id: {}]", resultFileId);
        return resultFileMapper.toModel(resultFileEntity);
    }

}
