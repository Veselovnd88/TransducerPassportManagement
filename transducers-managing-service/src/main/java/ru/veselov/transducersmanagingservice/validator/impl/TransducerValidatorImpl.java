package ru.veselov.transducersmanagingservice.validator.impl;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.transducersmanagingservice.entity.TransducerEntity;
import ru.veselov.transducersmanagingservice.repository.TransducerRepository;
import ru.veselov.transducersmanagingservice.validator.TransducerValidator;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransducerValidatorImpl implements TransducerValidator {

    private final TransducerRepository transducerRepository;

    @Override
    public void validatePtArt(String ptArt) {
        Optional<TransducerEntity> foundByPtArt = transducerRepository.findByArt(ptArt);
        if (foundByPtArt.isPresent()) {
            log.error("Transducer with [art: {}] already exists", ptArt);
            throw new EntityExistsException("Transducer with art %s already exists".formatted(ptArt));
        }
        log.debug("[Transducer with ptArt: {}] successfully validated", ptArt);
    }

}
