package ru.veselov.transducersmanagingservice.service;


import ru.veselov.transducersmanagingservice.dto.SortingParams;
import ru.veselov.transducersmanagingservice.model.Customer;

import java.util.List;

public interface CustomerService {

    void save(Customer customer);

    Customer getCustomer(String customerId);

    List<Customer> getAllCustomers(SortingParams sortingParams);

    void deleteCustomer(String customerId);

    void updateCustomer(String customerId);

}
