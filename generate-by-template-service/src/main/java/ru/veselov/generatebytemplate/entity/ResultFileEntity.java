package ru.veselov.generatebytemplate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "result_file")
@NoArgsConstructor
@Builder
@Getter
@Setter
@AllArgsConstructor
@ToString(exclude = "templateEntity")
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
        ResultFileEntity that = (ResultFileEntity) o;
        return Objects.equals(filename, that.filename) && Objects.equals(bucket, that.bucket)
                && Objects.equals(username, that.username) && Objects.equals(taskId, that.taskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filename, bucket, username, taskId);
    }

}
