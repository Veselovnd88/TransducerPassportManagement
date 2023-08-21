package ru.veselov.transducersmanagingservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "serial_number")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(exclude = {"transducerEntity"}, callSuper = false)
@ToString(exclude = {"transducerEntity"})
@Builder
public class SerialNumberEntity extends BaseEntity {

    @Column(name = "number", nullable = false)
    private String number;

    @Column(name = "pt_art", nullable = false)
    private String ptArt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pt_id", referencedColumnName = "id")
    TransducerEntity transducerEntity;

}
