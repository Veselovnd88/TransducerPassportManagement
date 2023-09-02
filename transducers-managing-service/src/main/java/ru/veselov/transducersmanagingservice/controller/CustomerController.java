package ru.veselov.transducersmanagingservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.transducersmanagingservice.annotation.SortingParam;
import ru.veselov.transducersmanagingservice.dto.CustomerDto;
import ru.veselov.transducersmanagingservice.dto.SortingParams;
import ru.veselov.transducersmanagingservice.model.Customer;
import ru.veselov.transducersmanagingservice.service.CustomerService;
import ru.veselov.transducersmanagingservice.validator.groups.CustomerField;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
@Validated({CustomerField.class, Default.class})
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void saveCustomer(@Valid @RequestBody CustomerDto customerDto) {
        customerService.save(customerDto);
    }

    @GetMapping("/id/{customerId}")
    public Customer getCustomerById(@PathVariable("customerId") @UUID String customerId) {
        return customerService.findCustomerById(customerId);
    }

    @GetMapping("/inn/{inn}")
    public Customer getCustomerByInn(@PathVariable("inn") String inn) {
        return customerService.findCustomerByInn(inn);
    }

    @GetMapping("/all")
    public List<Customer> getAllCustomer(@SortingParam @Valid SortingParams sortingParams) {
        return customerService.getAllCustomers(sortingParams);
    }

    @DeleteMapping("/delete/{customerId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteCustomer(@PathVariable("customerId") @UUID String customerId) {
        customerService.deleteCustomer(customerId);
    }

    @PutMapping("/update/{customerId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Customer updateCustomer(@PathVariable("customerId") @UUID String customerId,
                                   @Valid @RequestBody CustomerDto customerDto) {
        return customerService.updateCustomer(customerId, customerDto);
    }

}
