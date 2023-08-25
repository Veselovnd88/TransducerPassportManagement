package ru.veselov.transducersmanagingservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class CustomerEntity extends BaseEntity{
    @Column(name = "customer_name")
    private String name;

    @Column(name = "inn")
    private String inn;

}
