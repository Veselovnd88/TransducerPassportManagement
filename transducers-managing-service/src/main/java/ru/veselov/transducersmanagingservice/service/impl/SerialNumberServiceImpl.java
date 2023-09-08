package ru.veselov.transducersmanagingservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.veselov.transducersmanagingservice.dto.DateParams;
import ru.veselov.transducersmanagingservice.dto.SerialsDto;
import ru.veselov.transducersmanagingservice.dto.SortingParams;
import ru.veselov.transducersmanagingservice.entity.CustomerEntity;
import ru.veselov.transducersmanagingservice.entity.SerialNumberEntity;
import ru.veselov.transducersmanagingservice.entity.TransducerEntity;
import ru.veselov.transducersmanagingservice.mapper.SerialNumberMapper;
import ru.veselov.transducersmanagingservice.model.SerialNumber;
import ru.veselov.transducersmanagingservice.repository.CustomerRepository;
import ru.veselov.transducersmanagingservice.repository.SerialNumberRepository;
import ru.veselov.transducersmanagingservice.repository.TransducerRepository;
import ru.veselov.transducersmanagingservice.service.SerialNumberService;
import ru.veselov.transducersmanagingservice.service.XlsxParseService;
import ru.veselov.transducersmanagingservice.util.SortingParamsUtils;

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

    private final CustomerRepository customerRepository;

    private final XlsxParseService xlsxParseService;

    private final SerialNumberMapper serialNumberMapper;

    @Override
    @Transactional
    public void saveSerials(SerialsDto serialsDto, MultipartFile multipartFile) {
        List<String> serials = xlsxParseService.parseSerials(multipartFile);
        String ptArt = serialsDto.getPtArt();
        UUID customerId = UUID.fromString(serialsDto.getCustomerId());
        Optional<TransducerEntity> transducerEntityOptional = transducerRepository.findByArt(ptArt);
        TransducerEntity transducerEntity = transducerEntityOptional.orElseThrow(() -> {
            log.error("Transducer with [art: {}] not found", ptArt);
            throw new EntityNotFoundException("Transducer with art %s not found".formatted(ptArt));
        });
        Optional<CustomerEntity> customerEntityOptional = customerRepository.findById(customerId);
        CustomerEntity customerEntity = customerEntityOptional.orElseThrow(() -> {
            log.error("Customer with [id: {}] not found", customerId);
            throw new EntityNotFoundException("Customer with id %s not found".formatted(customerId));
        });
        ArrayList<SerialNumberEntity> serialNumberEntities = new ArrayList<>();
        serials.forEach(serial -> {
            SerialNumberEntity serialNumberEntity = createAndSetUpEntity(serialsDto);
            serialNumberEntity.setTransducer(transducerEntity);
            serialNumberEntity.setCustomer(customerEntity);
            serialNumberEntity.setPtArt(transducerEntity.getArt());
            serialNumberEntity.setNumber(serial);
            serialNumberEntities.add(serialNumberEntity);
        });
        serialNumberRepository.saveAll(serialNumberEntities);
        log.info("Serial number saved to DB, [total: {}]", serials.size());
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
        Optional<SerialNumberEntity> foundSerial = serialNumberRepository.findById(uuid);
        return serialNumberMapper.toSerialNumberModel(foundSerial.orElseThrow(() -> {
            log.error("Device with serial [number: {}] not found", serialId);
            throw new EntityNotFoundException("Device with serial [number: %s] not found".formatted(serialId));
        }));
    }

    @Override
    public List<SerialNumber> findByArt(SortingParams sortingParams, String ptArt) {
        long totalCount = serialNumberRepository.countAllByPtArt(ptArt);
        SortingParamsUtils.validatePageNumber(sortingParams.getPage(), totalCount, serialsPerPage);
        Pageable pageable = SortingParamsUtils.createPageable(sortingParams, serialsPerPage);
        Page<SerialNumberEntity> foundSerials = serialNumberRepository.findAllByPtArt(ptArt, pageable);
        log.info("Found [{} serials] with [pt art: {}]", foundSerials.getContent().size(), ptArt);
        return serialNumberMapper.toModelList(foundSerials.getContent());
    }

    @Override
    public List<SerialNumber> findBetweenDates(SortingParams sortingParams, DateParams dateParams) {
        LocalDate after = dateParams.getAfter();
        LocalDate before = dateParams.getBefore();
        long totalCount = serialNumberRepository.countAllBetweenDates(after, before);
        SortingParamsUtils.validatePageNumber(sortingParams.getPage(), totalCount, serialsPerPage);
        Pageable pageable = SortingParamsUtils.createPageable(sortingParams, serialsPerPage);
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
        SortingParamsUtils.validatePageNumber(sortingParams.getPage(), totalCount, serialsPerPage);
        Pageable pageable = SortingParamsUtils.createPageable(sortingParams, serialsPerPage);
        Page<SerialNumberEntity> foundSerials = serialNumberRepository.findAllByPtArtBetweenDates(ptArt, after, before, pageable);
        log.info("Found [{} serials] with [ptArt: {}] between dates [{} - {}]", foundSerials.getContent().size(),
                ptArt, after, before);
        return serialNumberMapper.toModelList(foundSerials.getContent());
    }

    @Override
    public List<SerialNumber> findByArtAndCustomerBetweenDates(SortingParams sortingParams,
                                                               String ptArt,
                                                               String customerId,
                                                               DateParams dateParams) {
        LocalDate after = dateParams.getAfter();
        LocalDate before = dateParams.getBefore();
        UUID customerUUID = UUID.fromString(customerId);
        long totalCount = serialNumberRepository
                .countAllByPtArtAnCustomerBetweenDates(ptArt, customerUUID, after, before);
        SortingParamsUtils.validatePageNumber(sortingParams.getPage(), totalCount, serialsPerPage);
        Pageable pageable = SortingParamsUtils.createPageable(sortingParams, serialsPerPage);
        Page<SerialNumberEntity> foundSerials = serialNumberRepository
                .findAllByPtArtAndCustomerBetweenDates(ptArt, customerUUID, after, before, pageable);
        log.info("Found [{} serials] with [ptArt: {} for customer: {}] between dates [{} - {}]",
                foundSerials.getContent().size(), customerId, ptArt, after, before);
        return serialNumberMapper.toModelList(foundSerials.getContent());
    }

    @Override
    @Transactional
    public void deleteSerial(String serialId) {
        UUID serialNumberUUID = UUID.fromString(serialId);
        Optional<SerialNumberEntity> optional = serialNumberRepository.findById(serialNumberUUID);
        SerialNumberEntity serialNumberEntity = optional.orElseThrow(() -> {
            log.error("Device with serial [number: {}] not found", serialId);
            throw new EntityNotFoundException("Device with serial [number: %s] not found".formatted(serialId));
        });
        serialNumberRepository.delete(serialNumberEntity);
        log.info("Serial number with [id: {}] was deleted from DB", serialId);
    }

    private SerialNumberEntity createAndSetUpEntity(SerialsDto serialsDto) {
        SerialNumberEntity serialNumberEntity = new SerialNumberEntity();
        serialNumberEntity.setComment(serialsDto.getComment());
        serialNumberEntity.setSavedAt(serialsDto.getSavedAt());
        return serialNumberEntity;
    }

}
