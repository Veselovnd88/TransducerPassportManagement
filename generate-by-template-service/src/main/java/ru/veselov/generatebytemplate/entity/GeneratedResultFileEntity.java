package ru.veselov.generatebytemplate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "generated_result_file")
@NoArgsConstructor
@Builder
@Getter
@Setter
@AllArgsConstructor
public class GeneratedResultFileEntity extends BaseEntity {

    @Column(name = "filename")
    private String filename;

    @Column(name = "bucket")
    private String bucket;

    @Column(name = "synced")
    private Boolean synced;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", referencedColumnName = "id")
    private TemplateEntity templateEntity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GeneratedResultFileEntity that = (GeneratedResultFileEntity) o;
        return Objects.equals(getFilename(), that.getFilename()) && Objects.equals(getBucket(), that.getBucket())
                && Objects.equals(getSynced(), that.getSynced())
                && Objects.equals(getTemplateEntity(), that.getTemplateEntity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getFilename(), getBucket(), getSynced(), getTemplateEntity());
    }
}
