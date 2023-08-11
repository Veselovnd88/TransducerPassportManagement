package ru.veselov.miniotemplateservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface PassportTemplateService {

    void saveTemplate(MultipartFile file);
}
