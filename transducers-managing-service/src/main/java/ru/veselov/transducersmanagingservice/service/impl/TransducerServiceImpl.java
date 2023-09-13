package ru.veselov.transducersmanagingservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.transducersmanagingservice.dto.SortingParams;
import ru.veselov.transducersmanagingservice.dto.TransducerDto;
import ru.veselov.transducersmanagingservice.entity.TransducerEntity;
import ru.veselov.transducersmanagingservice.mapper.TransducerMapper;
import ru.veselov.transducersmanagingservice.model.Transducer;
import ru.veselov.transducersmanagingservice.repository.TransducerRepository;
import ru.veselov.transducersmanagingservice.service.TransducerService;
import ru.veselov.transducersmanagingservice.util.SortingParamsUtils;
import ru.veselov.transducersmanagingservice.validator.TransducerValidator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TransducerServiceImpl implements TransducerService {

    @Value("${transducer.transducersPerPage}")
    private int transducersPerPage;

    private final TransducerRepository transducerRepository;

    private final TransducerValidator transducerValidator;

    private final TransducerMapper transducerMapper;

    @Override
    @Transactional
    public Transducer saveTransducer(TransducerDto transducerDto) {
        transducerValidator.validatePtArt(transducerDto.getArt());
        TransducerEntity transducerEntity = transducerMapper.toEntity(transducerDto);
        TransducerEntity saved = transducerRepository.save(transducerEntity);
        log.info("Transducer successfully saved with [id {}]", saved.getId());
        return transducerMapper.toModel(saved);
    }

    @Cacheable(value = "transducer")
    @Override
    public Transducer findTransducerById(String transducerId) {
        UUID uuid = UUID.fromString(transducerId);
        Optional<TransducerEntity> foundTransducer = transducerRepository.findById(uuid);
        TransducerEntity transducerEntity = foundTransducer.orElseThrow(() -> {
            log.error("Transducer with such [id: {}] not found", transducerId);
            throw new EntityNotFoundException("Transducer with such id %s not found".formatted(transducerId));
        });
        log.info("Found transducer with [id: {}]", transducerId);
        return transducerMapper.toModel(transducerEntity);
    }

    @Override
    public Transducer findTransducerByArt(String ptArt) {
        Optional<TransducerEntity> foundTransducer = transducerRepository.findByArt(ptArt);
        TransducerEntity transducerEntity = foundTransducer.orElseThrow(() -> {
            log.error("Transducer with such [art: {}] not found", ptArt);
            throw new EntityNotFoundException("Transducer with such art %s not found".formatted(ptArt));
        });
        log.info("Found transducer with [art: {}]", ptArt);
        return transducerMapper.toModel(transducerEntity);
    }

    @Override
    @Transactional
    public void deleteTransducer(String transducerId) {
        UUID uuid = UUID.fromString(transducerId);
        Optional<TransducerEntity> optional = transducerRepository.findById(uuid);
        TransducerEntity transducerEntity = optional.orElseThrow(() -> {
            log.error("Transducer with such [id: {}] not found", transducerId);
            throw new EntityNotFoundException("Transducer with such id %s not found".formatted(transducerId));
        });
        transducerRepository.delete(transducerEntity);
        log.info("Transducer with [id: {}] was deleted", transducerId);
    }

    @Override
    @Transactional
    public Transducer updateTransducer(String transducerId, TransducerDto transducerDto) {
        UUID uuid = UUID.fromString(transducerId);
        Optional<TransducerEntity> foundTransducer = transducerRepository.findById(uuid);
        TransducerEntity transducerEntity = foundTransducer.orElseThrow(() -> {
            log.error("Transducer with [id: {}] not found", transducerId);
            throw new EntityNotFoundException("Transducer with id %s not found".formatted(transducerId));
        });
        if (!transducerEntity.getArt().equals(transducerDto.getArt())) {
            transducerValidator.validatePtArt(transducerDto.getArt());
        }
        updateTransducerEntity(transducerEntity, transducerDto);
        TransducerEntity updated = transducerRepository.save(transducerEntity);
        log.info("Transducer with [id: {}] successfully updated", transducerId);
        return transducerMapper.toModel(updated);
    }

    @Override
    public List<Transducer> getAll(SortingParams sortingParams) {
        long countAll = transducerRepository.countAll();
        SortingParamsUtils.validatePageNumber(sortingParams.getPage(), countAll, transducersPerPage);
        Pageable pageable = SortingParamsUtils.createPageable(sortingParams, transducersPerPage);
        Page<TransducerEntity> allTransducers = transducerRepository.getAll(pageable);
        log.info("Found [{}] transducers", allTransducers.getTotalElements());
        return transducerMapper.toModels(allTransducers.getContent());
    }

    private void updateTransducerEntity(TransducerEntity transducerEntity, TransducerDto transducerDto) {
        transducerEntity.setAccuracy(transducerDto.getAccuracy());
        transducerEntity.setTransducerName(transducerDto.getTransducerName());
        transducerEntity.setArt(transducerDto.getArt());
        transducerEntity.setModel(transducerDto.getModel());
        transducerEntity.setCode(transducerDto.getCode());
        transducerEntity.setConnector(transducerDto.getConnector());
        transducerEntity.setElectricalOutput(transducerDto.getElectricalOutput());
        transducerEntity.setOptions(transducerDto.getOptions());
        transducerEntity.setThread(transducerDto.getThread());
        transducerEntity.setOutputCode(transducerDto.getOutputCode());
        transducerEntity.setPinOut(transducerDto.getPinOut());
        transducerEntity.setPressureRange(transducerDto.getPressureRange());
        transducerEntity.setPressureType(transducerDto.getPressureType());

    }


}
