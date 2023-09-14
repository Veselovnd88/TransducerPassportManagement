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
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "passport")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassportEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serial_id", referencedColumnName = "id")
    private SerialNumberEntity serialNumber;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", referencedColumnName = "id")
    private TemplateEntity template;

    @Column(name = "print_date")
    @Temporal(TemporalType.DATE)
    private LocalDate printDate;

}
