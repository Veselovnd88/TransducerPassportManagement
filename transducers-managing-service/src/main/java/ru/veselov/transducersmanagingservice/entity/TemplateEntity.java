package ru.veselov.transducersmanagingservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "pass_template")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateEntity extends BaseEntity {

    @Column(name = "pt_art", nullable = false)
    private String ptArt;

    @Column(name = "template_name", nullable = false, unique = true)
    private String templateName;

    @Column(name = "filename", nullable = false, unique = true)
    private String filename;

    @Column(name = "bucket", nullable = false)
    private String bucket;

    @Column(name = "edited_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime editedAt;

    @Column(name = "synced")
    private Boolean synced;

}
