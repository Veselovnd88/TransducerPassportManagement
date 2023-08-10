package ru.veselov.passportprocessing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.veselov.passportprocessing.dto.GeneratePassportsDto;
import ru.veselov.passportprocessing.entity.PassportEntity;
import ru.veselov.passportprocessing.repository.PassportRepository;
import ru.veselov.passportprocessing.service.PassportStorageService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class PassportStorageServiceImpl implements PassportStorageService {
    private final PassportRepository passportRepository;

    @Override
    @Async("taskExecutor")
    public CompletableFuture<List<PassportEntity>> savePassports(GeneratePassportsDto generatePassportsDto) {
        List<PassportEntity> passportsToSave = generatePassportsDto.getSerials().stream().map(serial ->
                new PassportEntity(UUID.fromString(generatePassportsDto.getTemplateId()),
                        serial,
                        generatePassportsDto.getPtArt(),
                        generatePassportsDto.getDate()
                )).toList();
        List<PassportEntity> saved = passportRepository.saveAll(passportsToSave);
        log.info("Passports saved to db: [total: {}]", passportsToSave.size());
        return CompletableFuture.completedFuture(saved);
    }

}
