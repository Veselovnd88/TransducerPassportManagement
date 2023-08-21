package ru.veselov.transducersmanagingservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.transducersmanagingservice.entity.SerialNumberEntity;
import ru.veselov.transducersmanagingservice.entity.TransducerEntity;
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

    private final SerialNumberRepository serialNumberRepository;

    private final TransducerRepository transducerRepository;

    @Override
    @Transactional
    public void saveSerials(List<String> serials, String ptArt) {
        Optional<TransducerEntity> transducerEntityOptional = transducerRepository.findByArt(ptArt);
        if (transducerEntityOptional.isPresent()) {
            TransducerEntity transducerEntity = transducerEntityOptional.get();
            ArrayList<SerialNumberEntity> serialNumberEntities = new ArrayList<>();
            serials.forEach(serial -> {
                SerialNumberEntity serialNumberEntity = SerialNumberEntity.builder().transducerEntity(transducerEntity)
                        .ptArt(transducerEntity.getArt())
                        .number(serial).build();
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
        return null;
    }

    @Override
    public List<SerialNumber> findByArt(String ptArt) {
        return null;
    }

    @Override
    public List<SerialNumber> findByDate(LocalDate before, LocalDate after) {
        return null;
    }

    @Override
    public void deleteSerial(String serialId) {
        UUID serialNumberUUID = UUID.fromString(serialId);
        serialNumberRepository.deleteById(serialNumberUUID);
        log.info("Serial number with [id: {}] was deleted from DB", serialId);
    }
}
