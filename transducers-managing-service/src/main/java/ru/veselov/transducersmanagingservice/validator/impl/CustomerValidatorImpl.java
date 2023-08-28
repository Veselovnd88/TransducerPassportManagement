package ru.veselov.transducersmanagingservice.validator.impl;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.transducersmanagingservice.entity.CustomerEntity;
import ru.veselov.transducersmanagingservice.repository.CustomerRepository;
import ru.veselov.transducersmanagingservice.validator.CustomerValidator;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerValidatorImpl implements CustomerValidator {

    private final CustomerRepository customerRepository;

    @Override
    public void validateInn(String inn) {
        Optional<CustomerEntity> foundEntity = customerRepository.findByInn(inn);
        if (foundEntity.isPresent()) {
            log.error("Customer with such [inn {}] already exists", inn);
            throw new EntityExistsException("Customer with such inn %s already exists".formatted(inn));
        }
    }
}
