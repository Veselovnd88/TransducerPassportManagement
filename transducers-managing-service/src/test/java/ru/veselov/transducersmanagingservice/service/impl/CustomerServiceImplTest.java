package ru.veselov.transducersmanagingservice.service.impl;

import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.transducersmanagingservice.dto.CustomerDto;
import ru.veselov.transducersmanagingservice.entity.CustomerEntity;
import ru.veselov.transducersmanagingservice.mapper.CustomerMapper;
import ru.veselov.transducersmanagingservice.mapper.CustomerMapperImpl;
import ru.veselov.transducersmanagingservice.repository.CustomerRepository;
import ru.veselov.transducersmanagingservice.validator.CustomerValidator;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    CustomerRepository customerRepository;

    @Mock
    CustomerValidator customerValidator;

    @InjectMocks
    CustomerServiceImpl customerService;

    @Captor
    ArgumentCaptor<CustomerEntity> customerEntityArgumentCaptor;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(customerService, "customersPerPage", 10, int.class);
        ReflectionTestUtils.setField(customerService, "customerMapper", new CustomerMapperImpl(), CustomerMapper.class);
    }

    @Test
    void shouldSaveCustomer() {
        CustomerDto customerDto = Instancio.create(CustomerDto.class);

        customerService.save(customerDto);

        Mockito.verify(customerValidator, Mockito.times(1)).validateInn(customerDto.getInn());
        Mockito.verify(customerRepository, Mockito.times(1)).save(customerEntityArgumentCaptor.capture());
        CustomerEntity captured = customerEntityArgumentCaptor.getValue();
        Assertions.assertThat(captured.getInn()).isEqualTo(customerDto.getInn());
        Assertions.assertThat(captured.getName()).isEqualTo(customerDto.getName());
    }

}