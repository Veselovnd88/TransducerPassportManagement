package ru.veselov.transducersmanagingservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transducer")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
public class TransducerEntity extends BaseEntity {

    @Column(name = "art", unique = true, nullable = false)
    private String art;

    @Column(name = "tr_name", nullable = false)
    private String transducerName;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "pressure_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PressureType pressureType;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "output_code", nullable = false)
    private String outputCode;

    @Column(name = "pressure_range", nullable = false)
    private String pressureRange;

    @Column(name = "accuracy", nullable = false)
    private String accuracy;

    @Column(name = "electrical_output", nullable = false)
    private String electricalOutput;

    @Column(name = "thread", nullable = false)
    private String thread;

    @Column(name = "connector", nullable = false)
    private String connector;

    @Column(name = "pin_out", nullable = false)
    private String pinOut;

    @Column(name = "options")
    private String options;

}
