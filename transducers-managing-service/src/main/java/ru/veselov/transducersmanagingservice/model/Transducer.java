package ru.veselov.transducersmanagingservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.veselov.transducersmanagingservice.entity.PressureType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transducer {

    private String id;

    private String art;

    private String name;

    private PressureType pressureType;

    private String model;

    private String outputCode;

    private String pressureRange;

    private String accuracy;

    private String electricalOutput;

    private String thread;

    private String connector;

    private String pinOut;

    private String options;

    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    private LocalDateTime createdAt;

}
