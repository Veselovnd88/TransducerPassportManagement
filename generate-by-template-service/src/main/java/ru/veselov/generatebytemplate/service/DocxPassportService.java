package ru.veselov.generatebytemplate.service;

import org.springframework.core.io.ByteArrayResource;
import ru.veselov.generatebytemplate.dto.GeneratePassportsDto;

public interface DocxPassportService {

    ByteArrayResource createDocxPassports(GeneratePassportsDto generatePassportsDto);

}
