package ru.veselov.miniotemplateservice.service;

import org.springframework.core.io.ByteArrayResource;

public interface TemplateService {

    ByteArrayResource getTemplateByName(String name);

}
