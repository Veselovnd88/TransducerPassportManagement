package ru.veselov.transducersmanagingservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
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
import ru.veselov.transducersmanagingservice.service.PassportSavingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassportSavingServiceImpl implements PassportSavingService {

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
            PassportEntity passportEntity = new PassportEntity();
            passportEntity.setSerialNumber(serialOptional.orElse(null));
            passportEntity.setTemplate(templateOptional.orElse(null));
            passportEntity.setPrintDate(generatePassportsDto.getPrintDate());
            passportsToSave.add(passportEntity);
        });
        passportRepository.saveAll(passportsToSave);
        log.info("Passports saved to db: [total: {}]", serials.size());
    }

}
