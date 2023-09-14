package ru.veselov.transducersmanagingservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "serial_number")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(exclude = {"transducer"}, callSuper = false)
@ToString(exclude = {"transducer"})
public class SerialNumberEntity extends BaseEntity {

    @Column(name = "number", nullable = false)
    private String number;

    @Column(name = "pt_art", nullable = false)
    private String ptArt;

    @Column(name = "ext_comment")
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private CustomerEntity customer;

    @Column(name = "saved_at")
    @Temporal(TemporalType.DATE)
    private LocalDate savedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pt_id", referencedColumnName = "id")
    TransducerEntity transducer;

}
