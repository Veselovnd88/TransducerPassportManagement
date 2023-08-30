package ru.veselov.transducersmanagingservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.transducersmanagingservice.TestConstants;
import ru.veselov.transducersmanagingservice.dto.CustomerDto;
import ru.veselov.transducersmanagingservice.entity.CustomerEntity;
import ru.veselov.transducersmanagingservice.mapper.CustomerMapper;
import ru.veselov.transducersmanagingservice.mapper.CustomerMapperImpl;
import ru.veselov.transducersmanagingservice.model.Customer;
import ru.veselov.transducersmanagingservice.repository.CustomerRepository;
import ru.veselov.transducersmanagingservice.validator.CustomerValidator;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
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

    @Test
    void shouldFindCustomerById() {
        CustomerEntity customerEntity = Instancio.create(CustomerEntity.class);
        Mockito.when(customerRepository.findById(TestConstants.CUSTOMER_ID)).thenReturn(Optional.of(customerEntity));

        Customer foundCustomer = customerService.findCustomerById(TestConstants.CUSTOMER_ID.toString());

        Mockito.verify(customerRepository, Mockito.times(1)).findById(TestConstants.CUSTOMER_ID);
        assertCustomerFields(foundCustomer, customerEntity);
    }

    @Test
    void shouldThrowEntityNotFoundException() {
        Mockito.when(customerRepository.findById(TestConstants.CUSTOMER_ID)).thenReturn(Optional.empty());
        String customerId = TestConstants.CUSTOMER_ID.toString();
        Assertions.assertThatThrownBy(() ->
                customerService.findCustomerById(customerId)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldFindCustomerByInn() {
        CustomerEntity customerEntity = Instancio.create(CustomerEntity.class);
        Mockito.when(customerRepository.findByInn(TestConstants.INN)).thenReturn(Optional.of(customerEntity));

        Customer foundCustomer = customerService.findCustomerByInn(TestConstants.INN);

        Mockito.verify(customerRepository, Mockito.times(1)).findByInn(TestConstants.INN);
        assertCustomerFields(foundCustomer, customerEntity);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionIfNoCustomerWithSuchInn() {
        Mockito.when(customerRepository.findByInn(TestConstants.INN)).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() ->
                customerService.findCustomerByInn(TestConstants.INN)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldGetAllCustomers() {
        Mockito.when(customerRepository.countAll()).thenReturn(1L);
        Page<CustomerEntity> page = Mockito.mock(Page.class);
        Mockito.when(page.getContent()).thenReturn(getRandomCustomerEntities());
        Mockito.when(customerRepository.findAll(ArgumentMatchers.any(Pageable.class))).thenReturn(page);

        List<Customer> allCustomers = customerService.getAllCustomers(TestConstants.SORTING_PARAMS);

        Mockito.verify(customerRepository, Mockito.times(1)).countAll();
        Mockito.verify(customerRepository, Mockito.times(1))
                .findAll(ArgumentMatchers.any(Pageable.class));
        Assertions.assertThat(allCustomers).hasSize(2);
    }

    @Test
    void shouldDeleteCustomer() {
        customerService.deleteCustomer(TestConstants.CUSTOMER_ID.toString());

        Mockito.verify(customerRepository, Mockito.times(1)).deleteById(TestConstants.CUSTOMER_ID);
    }

    @Test
    void shouldUpdateCustomerDiffInns() {
        CustomerEntity customerEntity = Instancio.of(CustomerEntity.class)
                .set(Select.field("inn"), TestConstants.INN).create();
        CustomerDto customerDto = Instancio.of(CustomerDto.class)
                .set(Select.field("inn"), "5167991251").create();
        Mockito.when(customerRepository.findById(TestConstants.CUSTOMER_ID))
                .thenReturn(Optional.of(customerEntity));

        customerService.updateCustomer(TestConstants.CUSTOMER_ID.toString(), customerDto);

        Mockito.verify(customerRepository, Mockito.times(1)).save(customerEntityArgumentCaptor.capture());
        Mockito.verify(customerValidator, Mockito.times(1)).validateInn(customerDto.getInn());
        CustomerEntity captured = customerEntityArgumentCaptor.getValue();
        Assertions.assertThat(captured.getInn()).isEqualTo(customerDto.getInn());
        Assertions.assertThat(captured.getName()).isEqualTo(customerDto.getName());
    }

    @Test
    void shouldUpdateCustomerEqualsInns() {
        CustomerEntity customerEntity = Instancio.of(CustomerEntity.class)
                .set(Select.field("inn"), TestConstants.INN).create();
        CustomerDto customerDto = Instancio.of(CustomerDto.class)
                .set(Select.field("inn"), TestConstants.INN).create();
        Mockito.when(customerRepository.findById(TestConstants.CUSTOMER_ID))
                .thenReturn(Optional.of(customerEntity));

        customerService.updateCustomer(TestConstants.CUSTOMER_ID.toString(), customerDto);

        Mockito.verify(customerRepository, Mockito.times(1)).save(customerEntityArgumentCaptor.capture());
        Mockito.verify(customerValidator, Mockito.never()).validateInn(customerDto.getInn());
        CustomerEntity captured = customerEntityArgumentCaptor.getValue();
        Assertions.assertThat(captured.getInn()).isEqualTo(customerEntity.getInn());
        Assertions.assertThat(captured.getName()).isEqualTo(customerDto.getName());
    }

    private void assertCustomerFields(Customer foundCustomer, CustomerEntity customerEntity) {
        Assertions.assertThat(foundCustomer.getInn()).isEqualTo(customerEntity.getInn());
        Assertions.assertThat(foundCustomer.getName()).isEqualTo(customerEntity.getName());
        Assertions.assertThat(foundCustomer.getId()).isEqualTo(customerEntity.getId().toString());
    }

    private List<CustomerEntity> getRandomCustomerEntities() {
        return List.of(Instancio.create(CustomerEntity.class),
                Instancio.create(CustomerEntity.class));
    }

}