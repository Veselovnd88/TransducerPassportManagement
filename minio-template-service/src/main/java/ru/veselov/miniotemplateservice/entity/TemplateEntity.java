package ru.veselov.miniotemplateservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pass_template")
@Data
@EqualsAndHashCode(exclude = {"id"})
@NoArgsConstructor
public class TemplateEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "uuid", updatable = false)
    private UUID id;

    @Column(name = "pt_art", nullable = false)
    private String ptArt;

    @Column(name = "template_name", nullable = false, unique = true)
    private String templateName;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "bucket", nullable = false)
    private String bucket;

    @Column(name = "edited_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime editedAt;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;


    @PreUpdate
    public void preUpdate() {
        this.editedAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

}
