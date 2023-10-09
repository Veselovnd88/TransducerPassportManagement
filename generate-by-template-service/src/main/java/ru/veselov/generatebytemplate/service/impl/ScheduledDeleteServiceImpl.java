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

    @Value("${scheduling.days-until-delete}")
    private int daysUntilDeleteUnSync;

    private TemplateRepository templateRepository;

    private GeneratedResultFileRepository generatedResultFileRepository;

    @Override
    @Transactional
    @Scheduled(cron = "${scheduling.delete-unsync}")
    public void deleteUnSynchronizedTemplates() {
        LocalDateTime deleteDate = LocalDateTime.now().minusDays(daysUntilDeleteUnSync);
        templateRepository.deleteAllWithUnSyncFalse(deleteDate);
    }

    @Override
    public void deleteUnSynchronizedGenerateResultFiles() {
        LocalDateTime deleteDate = LocalDateTime.now().minusDays(daysUntilDeleteUnSync);
        //TODO
    }

    @Override
    public void deleteGeneratedResultFilesWithNullTemplates() {

    }
}
