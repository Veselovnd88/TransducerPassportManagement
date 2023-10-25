package ru.veselov.generatebytemplate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "pass_template")
@NoArgsConstructor
@Getter
@Setter
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemplateEntity that = (TemplateEntity) o;
        return Objects.equals(getPtArt(), that.getPtArt()) && Objects.equals(getTemplateName(), that.getTemplateName())
                && Objects.equals(getFilename(), that.getFilename()) && Objects.equals(getBucket(), that.getBucket())
                && Objects.equals(getEditedAt(), that.getEditedAt()) && Objects.equals(getSynced(), that.getSynced());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPtArt(), getTemplateName(), getFilename(), getBucket(), getEditedAt(), getSynced());
    }

}
