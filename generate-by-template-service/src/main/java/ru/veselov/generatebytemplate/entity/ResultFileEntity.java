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
import java.util.UUID;

@Entity
@Table(name = "result_file")
@NoArgsConstructor
@Builder
@Getter
@Setter
@AllArgsConstructor
public class ResultFileEntity extends BaseEntity {

    @Column(name = "filename")
    private String filename;

    @Column(name = "bucket")
    private String bucket;

    @Column(name = "synced")
    private Boolean synced;

    @Column(name = "username")
    private String username;

    @Column(name = "task_id")
    private UUID taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", referencedColumnName = "id")
    private TemplateEntity templateEntity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ResultFileEntity that = (ResultFileEntity) o;
        return Objects.equals(getFilename(), that.getFilename()) && Objects.equals(getBucket(), that.getBucket())
                && Objects.equals(getSynced(), that.getSynced())
                && Objects.equals(getTemplateEntity(), that.getTemplateEntity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getFilename(), getBucket(), getSynced(), getTemplateEntity());
    }
}
