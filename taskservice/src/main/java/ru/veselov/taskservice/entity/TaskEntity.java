package ru.veselov.taskservice.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
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
    @Column(name = "task_id", columnDefinition = "uuid")
    private UUID taskId;

    @Column(name = "username")
    private String username;

    @Builder.Default
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.CREATED;

    @Column(name = "performed_at")
    private LocalDateTime performedAt;

    @Column(name = "template_id")
    private UUID templateId;

    @Column(name = "printDate")
    private LocalDate printDate;

    @Column(name = "file_id")
    private UUID fileId;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "task_serial",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "serial_id"),
            foreignKey = @ForeignKey(name = "task_id"),
            inverseForeignKey = @ForeignKey(name = "serial_id")
    )
    private Set<SerialNumberEntity> serials = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public void addSerialNumber(SerialNumberEntity serialNumberEntity) {
        this.serials.add(serialNumberEntity);
    }

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
        return Objects.equals(username, that.username) && Objects.equals(templateId, that.templateId)
                && Objects.equals(printDate, that.printDate) && Objects.equals(serials, that.serials);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, templateId, printDate, serials);
    }
}
