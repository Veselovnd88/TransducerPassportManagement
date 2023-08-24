package ru.veselov.transducersmanagingservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.transducersmanagingservice.dto.DateParams;
import ru.veselov.transducersmanagingservice.dto.SerialsDto;
import ru.veselov.transducersmanagingservice.dto.SortingParams;
import ru.veselov.transducersmanagingservice.entity.SerialNumberEntity;
import ru.veselov.transducersmanagingservice.entity.TransducerEntity;
import ru.veselov.transducersmanagingservice.exception.PageExceedsMaximumValueException;
import ru.veselov.transducersmanagingservice.mapper.SerialNumberMapper;
import ru.veselov.transducersmanagingservice.model.SerialNumber;
import ru.veselov.transducersmanagingservice.repository.SerialNumberRepository;
import ru.veselov.transducersmanagingservice.repository.TransducerRepository;
import ru.veselov.transducersmanagingservice.service.SerialNumberService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SerialNumberServiceImpl implements SerialNumberService {

    @Value("${serial.serialsPerPage}")
    private int serialsPerPage;

    private final SerialNumberRepository serialNumberRepository;

    private final TransducerRepository transducerRepository;

    private final SerialNumberMapper serialNumberMapper;

    @Override
    @Transactional
    public void saveSerials(SerialsDto serialsDto) {
        List<String> serials = serialsDto.getSerials();
        String ptArt = serialsDto.getPtArt();
        Optional<TransducerEntity> transducerEntityOptional = transducerRepository.findByArt(ptArt);
        if (transducerEntityOptional.isPresent()) {
            TransducerEntity transducerEntity = transducerEntityOptional.get();
            ArrayList<SerialNumberEntity> serialNumberEntities = new ArrayList<>();
            serials.forEach(serial -> {
                SerialNumberEntity serialNumberEntity = createAndSetUpEntity(serialsDto);
                serialNumberEntity.setTransducerEntity(transducerEntity);
                serialNumberEntity.setPtArt(transducerEntity.getArt());
                serialNumberEntity.setNumber(serial);
                serialNumberEntities.add(serialNumberEntity);
            });
            serialNumberRepository.saveAll(serialNumberEntities);
            log.info("Serial number saved to DB, [total: {}]", serials.size());
        } else {
            log.error("Transducer with [art: {}] not found", ptArt);
            throw new EntityNotFoundException("Transducer with art %s not found".formatted(ptArt));
        }
    }

    @Override
    public List<SerialNumber> findByNumber(String number) {
        List<SerialNumberEntity> foundSerials = serialNumberRepository.findAllByNumber(number);
        log.info("Found [{} serials] with [number: {}]", foundSerials.size(), number);
        return serialNumberMapper.toModelList(foundSerials);
    }

    @Override
    public SerialNumber findById(String serialId) {
        UUID uuid = UUID.fromString(serialId);
        Optional<SerialNumberEntity> foundById = serialNumberRepository.findById(uuid);
        return serialNumberMapper.toSerialNumberModel(foundById.orElseThrow(() -> {
            log.error("Device with serial [number: {}] not found", serialId);
            throw new EntityNotFoundException("Device with serial [number: %s] not found".formatted(serialId));
        }));
    }

    @Override
    public List<SerialNumber> findByArt(SortingParams sortingParams, String ptArt) {
        long totalWithPtArt = serialNumberRepository.countAllByPtArt(ptArt);
        validatePageNumber(sortingParams.getPage(), totalWithPtArt);
        Pageable pageable = createPageable(sortingParams);
        Page<SerialNumberEntity> foundSerials = serialNumberRepository.findAllByPtArt(ptArt, pageable);
        log.info("Found [{} serials] with [pt art: {}]", foundSerials.getContent().size(), ptArt);
        return serialNumberMapper.toModelList(foundSerials.getContent());
    }

    @Override
    public List<SerialNumber> findBetweenDates(SortingParams sortingParams, DateParams dateParams) {
        LocalDate after = dateParams.getAfter();
        LocalDate before = dateParams.getBefore();
        long totalCount = serialNumberRepository.countAllBetweenDates(after, before);
        validatePageNumber(sortingParams.getPage(), totalCount);
        Pageable pageable = createPageable(sortingParams);
        Page<SerialNumberEntity> foundSerials = serialNumberRepository.findAllBetweenDates(after, before, pageable);
        log.info("Found [{} serials] between dates [{} - {}]", foundSerials.getContent().size(), after, before);
        return serialNumberMapper.toModelList(foundSerials.getContent());
    }

    @Override
    public List<SerialNumber> findByPtArtBetweenDates(SortingParams sortingParams, String ptArt,
                                                      DateParams dateParams) {
        LocalDate after = dateParams.getAfter();
        LocalDate before = dateParams.getBefore();
        long totalCount = serialNumberRepository.countAllByPtArtBetweenDates(ptArt, after, before);
        validatePageNumber(sortingParams.getPage(), totalCount);
        Pageable pageable = createPageable(sortingParams);
        Page<SerialNumberEntity> foundSerials = serialNumberRepository.findAllByPtARtBetweenDates(ptArt, after, before, pageable);
        log.info("Found [{} serials] with [ptArt: {}] between dates [{} - {}]", foundSerials.getContent().size(),
                ptArt, after, before);
        return serialNumberMapper.toModelList(foundSerials.getContent());
    }

    @Override
    public void deleteSerial(String serialId) {
        UUID serialNumberUUID = UUID.fromString(serialId);
        serialNumberRepository.deleteById(serialNumberUUID);
        log.info("Serial number with [id: {}] was deleted from DB", serialId);
    }

    private Pageable createPageable(SortingParams sortingParams) {
        int page = sortingParams.getPage();
        String sort = sortingParams.getSort();
        String order = sortingParams.getOrder();
        Sort sortOrder;
        if (StringUtils.equals(order, "asc")) {
            sortOrder = Sort.by(sort).ascending();
        } else {
            sortOrder = Sort.by(sort).descending();
        }
        return PageRequest.of(page, serialsPerPage).withSort(sortOrder);
    }

    private void validatePageNumber(int page, long count) {
        long totalPages = count / serialsPerPage;
        if (page > totalPages) {
            log.error("Page number exceeds maximum value [max: {}, was: {}}]", totalPages, page);
            throw new PageExceedsMaximumValueException("Page number exceeds maximum value [max: %s, was: %s]"
                    .formatted(totalPages, page), page);
        }
    }

    private SerialNumberEntity createAndSetUpEntity(SerialsDto serialsDto) {
        SerialNumberEntity serialNumberEntity = new SerialNumberEntity();
        serialNumberEntity.setComment(serialsDto.getComment());
        serialNumberEntity.setCustomer(serialsDto.getCustomer());
        serialNumberEntity.setSavedAt(serialsDto.getSavedAt());
        return serialNumberEntity;
    }

}
