package ru.veselov.transducersmanagingservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "passport")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

public class PassportEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)//fields would be filled with queries
    @JoinColumn(name = "serial_id", referencedColumnName = "id")
    private SerialNumberEntity serialNumber;
    @ManyToOne(fetch = FetchType.LAZY)//fields would be filled with queries
    @JoinColumn(name = "template_id", referencedColumnName = "id")
    private TemplateEntity template;

    @Column(name = "print_date")
    @Temporal(TemporalType.DATE)
    private LocalDate printDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PassportEntity that = (PassportEntity) o;
        return Objects.equals(getSerialNumber(), that.getSerialNumber()) && Objects.equals(getTemplate(), that.getTemplate()) && Objects.equals(getPrintDate(), that.getPrintDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getSerialNumber(), getTemplate(), getPrintDate());
    }
}
