package ru.veselov.passportprocessing.service;

import org.springframework.core.io.ByteArrayResource;

import java.io.InputStream;

public interface PassportTemplateService {

    ByteArrayResource getTemplate(String templateId);

}
