package ru.veselov.generatebytemplate.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "generated_result_file")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class GeneratedResultFileEntity extends BaseEntity {

    private String filename;

    private String bucket;

    private Boolean synced;

}
