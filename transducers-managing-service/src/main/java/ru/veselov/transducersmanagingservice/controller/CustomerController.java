package ru.veselov.transducersmanagingservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> saveCustomer(@Valid @RequestBody CustomerDto customerDto) {
        customerService.save(customerDto);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/id/{customerId}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable("customerId") @UUID String customerId) {
        Customer foundCustomer = customerService.findCustomerById(customerId);
        return ResponseEntity.ok(foundCustomer);
    }

    @GetMapping("/inn/{inn}")
    public ResponseEntity<Customer> getCustomerByInn(@PathVariable("inn") @NotBlank String inn) {
        Customer foundCustomer = customerService.findCustomerByInn(inn);
        return ResponseEntity.ok(foundCustomer);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Customer>> getAllCustomer(@SortingParam SortingParams sortingParams) {
        List<Customer> allCustomers = customerService.getAllCustomers(sortingParams);
        return ResponseEntity.ok(allCustomers);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable("customerId") @UUID String customerId) {
        customerService.deleteCustomer(customerId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable("customerId") @UUID String customerId,
                                                   CustomerDto customerDto) {
        Customer updatedCustomer = customerService.updateCustomer(customerId, customerDto);
        return new ResponseEntity<>(updatedCustomer, HttpStatus.ACCEPTED);
    }

}
