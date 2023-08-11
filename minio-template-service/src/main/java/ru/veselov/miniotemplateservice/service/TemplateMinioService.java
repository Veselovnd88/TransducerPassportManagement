package ru.veselov.miniotemplateservice.service;

import org.springframework.core.io.ByteArrayResource;

public interface TemplateMinioService {

    ByteArrayResource getTemplateByName(String name);

}
