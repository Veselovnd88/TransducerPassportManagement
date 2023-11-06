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

    @ManyToMany
    @JoinTable(
            name = "task_serial",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "serial_id")
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
        return Objects.equals(getUsername(), that.getUsername()) &&
                Objects.equals(getIsPerformed(), that.getIsPerformed()) &&
                Objects.equals(getPerformedAt(), that.getPerformedAt()) &&
                Objects.equals(getCreatedAt(), that.getCreatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getIsPerformed(), getPerformedAt(), getCreatedAt());
    }
}
