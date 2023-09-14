package ru.veselov.transducersmanagingservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
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
public class PassportEntity extends BaseEntity{

    @Column(name = "serial_number")
    private String serial;

    @Column(name = "pt_art")
    private String ptArt;

    @ManyToOne
    @JoinColumn(name = "pt_id", referencedColumnName = "id")
    private TransducerEntity transducer;

    @ManyToOne
    @JoinColumn(name = "template_id", referencedColumnName = "id")
    private TemplateEntity templateEntity;


    @Column(name = "print_date")
    @Temporal(TemporalType.DATE)
    private LocalDate printDate;

}
