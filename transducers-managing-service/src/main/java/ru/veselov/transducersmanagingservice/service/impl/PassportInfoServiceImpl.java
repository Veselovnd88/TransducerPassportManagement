package ru.veselov.transducersmanagingservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.transducersmanagingservice.dto.DateParams;
import ru.veselov.transducersmanagingservice.dto.SortingParams;
import ru.veselov.transducersmanagingservice.entity.PassportEntity;
import ru.veselov.transducersmanagingservice.mapper.PassportMapper;
import ru.veselov.transducersmanagingservice.model.Passport;
import ru.veselov.transducersmanagingservice.repository.PassportRepository;
import ru.veselov.transducersmanagingservice.service.PassportInfoService;
import ru.veselov.transducersmanagingservice.util.SortingParamsUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PassportInfoServiceImpl implements PassportInfoService {
    @Value("${passport.passportsPerPage}")
    private int passportsPerPage;

    private PassportRepository passportRepository;

    private PassportMapper passportMapper;

    @Override
    public Passport getById(String passportId) {
        UUID uuid = UUID.fromString(passportId);
        Optional<PassportEntity> optionalPassport = passportRepository.findById(uuid);
        PassportEntity passportEntity = optionalPassport.orElseThrow(() -> {
            log.error("Passport with [id: {}] not found", passportId);
            throw new EntityNotFoundException("Passport with [id: %s] not found".formatted(passportId));
        });
        log.info("Retrieved passport with [id: {}] from DB", passportId);
        return passportMapper.toModel(passportEntity);
    }

    @Override
    public List<Passport> getAllBetweenDates(SortingParams sortingParams, DateParams dateParams) {
        long count = passportRepository.countAllBetweenDates(dateParams.getAfter(), dateParams.getBefore());
        SortingParamsUtils.validatePageNumber(sortingParams.getPage(), count, passportsPerPage);
        Pageable pageable = SortingParamsUtils.createPageable(sortingParams, passportsPerPage);
        Page<PassportEntity> foundPassports = passportRepository
                .findAllBetweenDates(dateParams.getAfter(), dateParams.getBefore(), pageable);
        log.info("Found [{} passports]", foundPassports.getTotalElements());
        return passportMapper.toModels(foundPassports.getContent());
    }

    @Override
    public List<Passport> getAllForSerialBetweenDates(String serialNumber,
                                                      SortingParams sortingParams,
                                                      DateParams dateParams) {
        return null;
    }

    @Override
    public List<Passport> getAllForPtArtBetweenDates(String ptArt, SortingParams sortingParams, DateParams dateParams) {
        return null;
    }

    @Override
    @Transactional
    public void deleteById(String passportId) {
        UUID uuid = UUID.fromString(passportId);
        Optional<PassportEntity> optionalPassport = passportRepository.findById(uuid);
        PassportEntity passportEntity = optionalPassport.orElseThrow(() -> {
            log.error("Passport with [id: {}] not found", passportId);
            throw new EntityNotFoundException("Passport with [id: %s] not found".formatted(passportId));
        });
        passportRepository.delete(passportEntity);
        log.info("Passport with [id: {}] deleted", passportId);
    }
}
