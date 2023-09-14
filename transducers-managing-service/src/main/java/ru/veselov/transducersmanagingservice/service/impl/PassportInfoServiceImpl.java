package ru.veselov.transducersmanagingservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.transducersmanagingservice.dto.DateParams;
import ru.veselov.transducersmanagingservice.dto.SortingParams;
import ru.veselov.transducersmanagingservice.entity.PassportEntity;
import ru.veselov.transducersmanagingservice.mapper.PassportMapper;
import ru.veselov.transducersmanagingservice.model.Passport;
import ru.veselov.transducersmanagingservice.repository.PassportRepository;
import ru.veselov.transducersmanagingservice.service.PassportInfoService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PassportInfoServiceImpl implements PassportInfoService {

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

        return null;
    }

    @Override
    public List<Passport> getAllBetweenDates(SortingParams sortingParams, DateParams dateParams) {
        return null;
    }

    @Override
    public List<Passport> getAllForSerialBetweenDates(String serialNumber, SortingParams sortingParams, DateParams dateParams) {
        return null;
    }

    @Override
    public List<Passport> getAllForPtArtBetweenDates(String ptArt, SortingParams sortingParams, DateParams dateParams) {
        return null;
    }

    @Override
    public void deleteById(String passportId) {

    }
}
