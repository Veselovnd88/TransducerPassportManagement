package ru.veselov.transducersmanagingservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Passport {

    private String id;

    private String templateId;

    private String serial;

    private String ptArt;

    private String printDate;

    private String createdAt;

}
