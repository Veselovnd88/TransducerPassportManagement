package ru.veselov.passportprocessing.service;

import java.io.InputStream;

public interface PassportTemplateService {

    InputStream getTemplate(String templateId);

}
