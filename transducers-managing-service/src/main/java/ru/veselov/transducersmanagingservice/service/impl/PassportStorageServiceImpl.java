package ru.veselov.transducersmanagingservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.transducersmanagingservice.dto.GeneratePassportsDto;
import ru.veselov.transducersmanagingservice.dto.SerialNumberDto;
import ru.veselov.transducersmanagingservice.entity.PassportEntity;
import ru.veselov.transducersmanagingservice.entity.SerialNumberEntity;
import ru.veselov.transducersmanagingservice.entity.TemplateEntity;
import ru.veselov.transducersmanagingservice.repository.PassportRepository;
import ru.veselov.transducersmanagingservice.repository.SerialNumberRepository;
import ru.veselov.transducersmanagingservice.repository.TemplateRepository;
import ru.veselov.transducersmanagingservice.service.PassportStorageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service receive generated passports dto and map it to PassportEntities for saving to DB, async
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PassportStorageServiceImpl implements PassportStorageService {

    private final SerialNumberRepository serialNumberRepository;

    private final TemplateRepository templateRepository;

    private final PassportRepository passportRepository;

    @Override
    @Async("taskExecutor")
    @Transactional
    public void save(GeneratePassportsDto generatePassportsDto) {
        List<PassportEntity> passportsToSave = new ArrayList<>();
        Optional<TemplateEntity> templateOptional = templateRepository
                .findById(UUID.fromString(generatePassportsDto.getTemplateId()));
        List<SerialNumberDto> serials = generatePassportsDto.getSerials();
        serials.forEach(x -> {
            Optional<SerialNumberEntity> serialOptional = serialNumberRepository
                    .findById(UUID.fromString(x.getSerialId()));
            if (serialOptional.isPresent()) {
                PassportEntity passportEntity = new PassportEntity();
                passportEntity.setSerialNumber(serialOptional.get());
                passportEntity.setTemplate(templateOptional.orElse(null));
                passportEntity.setPrintDate(generatePassportsDto.getPrintDate());
                passportsToSave.add(passportEntity);
            } else {
                log.warn("Serial number with [id: {}] doesn't exists, passport won't be saved", x.getSerialId());
            }

        });
        passportRepository.saveAll(passportsToSave);
        log.info("Passports saved to db: [total: {}]", passportsToSave.size());
    }

    @Scheduled(cron = "${scheduling.delete-empty-passports}")
    @Async
    @Transactional
    @Override
    public void deleteWithNullTemplateAndNullSerialNumber() {
        log.info("Scheduled task for deleting empty passports started");
        passportRepository.deleteWithNullSerialAndTemplate();
        log.info("Passport records with null template and serial number deleted");
    }

}
