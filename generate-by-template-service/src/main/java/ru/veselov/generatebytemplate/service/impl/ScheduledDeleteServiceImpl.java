package ru.veselov.generatebytemplate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.generatebytemplate.repository.GeneratedResultFileRepository;
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

    private TemplateRepository templateRepository;

    private GeneratedResultFileRepository generatedResultFileRepository;

    @Override
    @Transactional
    @Scheduled(cron = "${scheduling.delete-unsync-template}")
    public void deleteUnSynchronizedTemplates() {
        LocalDateTime deleteDate = LocalDateTime.now().minusDays(daysUntilDeleteUnSyncTemplate);
        templateRepository.deleteAllWithUnSyncFalse(deleteDate);
        log.info("UnSynchronized template records deleted until");
    }

    @Override
    @Transactional
    @Scheduled(cron = "${scheduling.delete-unsync-result}")
    public void deleteUnSynchronizedGenerateResultFiles() {
        LocalDateTime deleteDate = LocalDateTime.now().minusDays(daysUntilDeleteUnSyncResult);
        generatedResultFileRepository.deleteAllWithUnSyncFalse(deleteDate);
        log.info("UnSynchronized results deleted");
    }

    @Override
    @Transactional
    @Scheduled(cron = "${scheduling.delete-result}")
    public void deleteExpiredResultFiles() {
        LocalDateTime deleteDate = LocalDateTime.now().minusDays(daysUntilDeleteResult);
        generatedResultFileRepository.deleteExpiredResultFiles(deleteDate);
        log.info("Expired result files deleted");
    }

}
