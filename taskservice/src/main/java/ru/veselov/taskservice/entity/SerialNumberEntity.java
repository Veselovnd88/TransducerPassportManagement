package ru.veselov.taskservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "serial_number")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SerialNumberEntity {

    @Id
    @Column(name = "serial_id")
    private UUID serialId;

    @Column(name = "serial")
    private String serial;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SerialNumberEntity that = (SerialNumberEntity) o;
        return Objects.equals(serialId, that.serialId) && Objects.equals(serial, that.serial);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialId, serial);
    }
}
