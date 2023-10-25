package ru.veselov.generatebytemplate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.generatebytemplate.repository.ResultFileRepository;
import ru.veselov.generatebytemplate.repository.TemplateRepository;
import ru.veselov.generatebytemplate.service.ScheduledDeleteService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledDeleteServiceImpl implements ScheduledDeleteService {

    @Value("${scheduling.days-until-delete-unsync-template}")
    private int daysUntilDeleteUnSyncTemplate;

    @Value("${scheduling.days-until-delete-unsync-result}")
    private int daysUntilDeleteUnSyncResult;

    @Value("${scheduling.days-until-delete-result}")
    private int daysUntilDeleteResult;

    private final TemplateRepository templateRepository;

    private final ResultFileRepository resultFileRepository;

    @Override
    @Transactional
    @Scheduled(cron = "${scheduling.delete-unsync-template}")
    public void deleteUnSynchronizedTemplates() {
        LocalDateTime deleteDate = LocalDateTime.now().minusDays(daysUntilDeleteUnSyncTemplate);
        templateRepository.deleteAllWithUnSyncFalse(deleteDate);
        log.info("Unsynchronized template records older than {} days are deleted", daysUntilDeleteUnSyncTemplate);
    }

    @Override
    @Transactional
    @Scheduled(cron = "${scheduling.delete-unsync-result}")
    public void deleteUnSynchronizedGenerateResultFiles() {
        LocalDateTime deleteDate = LocalDateTime.now().minusDays(daysUntilDeleteUnSyncResult);
        resultFileRepository.deleteAllWithUnSyncFalse(deleteDate);
        log.info("Unsynchronized result files records older than {} days are deleted", daysUntilDeleteUnSyncResult);
    }

    @Override
    @Transactional
    @Scheduled(cron = "${scheduling.delete-result}")
    public void deleteExpiredResultFiles() {
        LocalDateTime deleteDate = LocalDateTime.now().minusDays(daysUntilDeleteResult);
        resultFileRepository.deleteExpiredResultFiles(deleteDate);
        log.info("Expired result files older than {} days are deleted: ", daysUntilDeleteResult);
    }

}
