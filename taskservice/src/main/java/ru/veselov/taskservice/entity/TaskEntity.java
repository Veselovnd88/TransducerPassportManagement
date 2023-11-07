package ru.veselov.taskservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "task")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "uuid")
    private UUID taskId;

    @Column(name = "username")
    private String username;

    @Column(name = "is_performed")
    private Boolean isPerformed;

    @Column(name = "performed_at")
    private LocalDateTime performedAt;

    @Column(name = "template_id")
    private UUID templateId;

    @Column(name = "printDate")
    private LocalDate printDate;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "task_serial",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "serial_id"),
            foreignKey = @ForeignKey(name = "id"),
            inverseForeignKey = @ForeignKey(name = "serial_id")
    )
    Set<SerialNumberEntity> serials;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskEntity that = (TaskEntity) o;
        return Objects.equals(taskId, that.taskId) && Objects.equals(username, that.username)
                && Objects.equals(isPerformed, that.isPerformed)
                && Objects.equals(performedAt, that.performedAt)
                && Objects.equals(templateId, that.templateId)
                && Objects.equals(printDate, that.printDate)
                && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, username, isPerformed, performedAt, templateId, printDate, createdAt);
    }
}
