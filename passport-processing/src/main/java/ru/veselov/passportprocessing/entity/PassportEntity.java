package ru.veselov.passportprocessing.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "passport")
@Data
@EqualsAndHashCode(exclude = {"id"})
@ToString(exclude = {"id"})
public class PassportEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "uuid", updatable = false)
    private UUID id;

    @Column(name = "template_id", updatable = false)
    private UUID templateId;

    @Column(name = "serial_number")
    private String serial;

    @Column(name = "pt_art")
    private String ptArt;

    @Column(name = "print_date")
    private LocalDate printDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public PassportEntity(UUID templateId, String serial, String ptArt, LocalDate printDate) {
        this.templateId = templateId;
        this.serial = serial;
        this.ptArt = ptArt;
        this.printDate = printDate;
    }
}
