package ru.veselov.taskservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "serial_number")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SerialNumberEntity {

    @Id
    @GeneratedValue
    @Column(name = "serial_id")
    private UUID serialId;

    private String serial;

}
