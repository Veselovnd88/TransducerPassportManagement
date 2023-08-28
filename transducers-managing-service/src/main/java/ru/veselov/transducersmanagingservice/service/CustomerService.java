package ru.veselov.transducersmanagingservice.service;


import ru.veselov.transducersmanagingservice.dto.CustomerDto;
import ru.veselov.transducersmanagingservice.dto.SortingParams;
import ru.veselov.transducersmanagingservice.model.Customer;

import java.util.List;

public interface CustomerService {

    void save(CustomerDto customerDto);

    Customer findCustomerById(String customerId);

    Customer findCustomerByInn(String inn);

    List<Customer> getAllCustomers(SortingParams sortingParams);

    void deleteCustomer(String customerId);

    void updateCustomer(String customerId, CustomerDto customerDto);

}
