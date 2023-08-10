package ru.veselov.passportprocessing.service;

import org.springframework.core.io.ByteArrayResource;

public interface TemplateStorageHttpClient {

    ByteArrayResource sendRequestToGetTemplate(String templateId);

}
