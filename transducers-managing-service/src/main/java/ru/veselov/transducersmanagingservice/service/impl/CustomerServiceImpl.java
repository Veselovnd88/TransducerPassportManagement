package ru.veselov.transducersmanagingservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.transducersmanagingservice.dto.CustomerDto;
import ru.veselov.transducersmanagingservice.dto.SortingParams;
import ru.veselov.transducersmanagingservice.entity.CustomerEntity;
import ru.veselov.transducersmanagingservice.mapper.CustomerMapper;
import ru.veselov.transducersmanagingservice.model.Customer;
import ru.veselov.transducersmanagingservice.repository.CustomerRepository;
import ru.veselov.transducersmanagingservice.service.CustomerService;
import ru.veselov.transducersmanagingservice.util.SortingParamsUtils;
import ru.veselov.transducersmanagingservice.validator.CustomerValidator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    @Value("${customer.customersPerPage}")
    private int customersPerPage;

    private final CustomerRepository customerRepository;

    private final CustomerValidator customerValidator;

    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    public void save(CustomerDto customerDto) {
        customerValidator.validateInn(customerDto.getInn());
        CustomerEntity customerEntity = customerMapper.toEntity(customerDto);
        customerRepository.save(customerEntity);
        log.info("Customer with [inn: {}], and name [{}] saved", customerDto.getInn(), customerDto.getName());
    }

    @Cacheable(value = "customer")
    @Override
    public Customer findCustomerById(String customerId) {
        UUID customerUUID = UUID.fromString(customerId);
        Optional<CustomerEntity> foundEntity = customerRepository.findById(customerUUID);
        CustomerEntity customerEntity = foundEntity.orElseThrow(() -> {
                    log.error("Customer with such [id: {}] not found", customerId);
                    throw new EntityNotFoundException("Customer with such id: %s not found".formatted(customerId));
                }
        );
        log.info("Customer with [id: {}] retrieved from DB", customerId);
        return customerMapper.toModel(customerEntity);
    }

    @Override
    public Customer findCustomerByInn(String inn) {
        Optional<CustomerEntity> foundEntity = customerRepository.findByInn(inn);
        CustomerEntity customerEntity = foundEntity.orElseThrow(() -> {
            log.error("Customer with such [inn: {}] not found", inn);
            throw new EntityNotFoundException("Customer with such inn: %s not found".formatted(inn));
        });
        log.info("Customer with [inn: {}] retrieved from DB", inn);
        return customerMapper.toModel(customerEntity);
    }

    @Override
    public List<Customer> getAllCustomers(SortingParams sortingParams) {
        long totalCount = customerRepository.countAll();
        SortingParamsUtils.validatePageNumber(sortingParams.getPage(), totalCount, customersPerPage);
        Pageable pageable = SortingParamsUtils.createPageable(sortingParams, customersPerPage);
        Page<CustomerEntity> foundCustomers = customerRepository.findAll(pageable);
        log.info("Found [{} customers]", foundCustomers.getTotalElements());
        return customerMapper.toModelList(foundCustomers.getContent());
    }

    @CacheEvict(value = "customer", key = "#customerId")
    @Override
    @Transactional
    public void deleteCustomer(String customerId) {
        UUID customerUUID = UUID.fromString(customerId);
        Optional<CustomerEntity> optional = customerRepository.findById(customerUUID);
        CustomerEntity customer = optional.orElseThrow(() -> {
            log.error("Customer with such [id: {}] not found", customerId);
            throw new EntityNotFoundException("Customer with such id: %s not found".formatted(customerId));
        });
        customerRepository.delete(customer);
        log.info("Customer with [id {}] deleted", customerId);
    }
    @CacheEvict(value = "customer", key = "#customerId")
    @Override
    @Transactional
    public Customer updateCustomer(String customerId, CustomerDto customerDto) {
        UUID customerUUID = UUID.fromString(customerId);
        Optional<CustomerEntity> foundEntity = customerRepository.findById(customerUUID);
        if (foundEntity.isEmpty()) {
            log.error("Customer with [id: {}] not found", customerId);
            throw new EntityNotFoundException("Customer with id %s not found".formatted(customerId));
        }
        CustomerEntity customerEntity = foundEntity.get();
        if (!customerDto.getInn().equals(customerEntity.getInn())) {
            customerValidator.validateInn(customerDto.getInn());
            customerEntity.setInn(customerDto.getInn());
        }
        customerEntity.setName(customerDto.getName());
        CustomerEntity saved = customerRepository.save(customerEntity);
        log.info("Customer info updated with {}", customerDto);
        return customerMapper.toModel(saved);
    }

}
