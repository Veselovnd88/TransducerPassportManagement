package ru.veselov.transducersmanagingservice.validator.impl;

import jakarta.persistence.EntityExistsException;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.veselov.transducersmanagingservice.TestConstants;
import ru.veselov.transducersmanagingservice.entity.CustomerEntity;
import ru.veselov.transducersmanagingservice.repository.CustomerRepository;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CustomerValidatorImplTest {

    @Mock
    CustomerRepository customerRepository;

    @InjectMocks
    CustomerValidatorImpl customerValidator;

    @Test
    void shouldValidate() {
        Mockito.when(customerRepository.findByInn(TestConstants.INN)).thenReturn(Optional.empty());
        Assertions.assertThatNoException().isThrownBy(() -> customerValidator.validateInn(TestConstants.INN));
    }

    @Test
    void shouldNotValidate() {
        CustomerEntity customer = Instancio.create(CustomerEntity.class);
        Mockito.when(customerRepository.findByInn(TestConstants.INN)).thenReturn(Optional.of(customer));

        Assertions.assertThatThrownBy(() -> customerValidator.validateInn(TestConstants.INN))
                .isInstanceOf(EntityExistsException.class);
    }

}