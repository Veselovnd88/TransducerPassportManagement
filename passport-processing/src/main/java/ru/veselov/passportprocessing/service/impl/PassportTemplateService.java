package ru.veselov.passportprocessing.service.impl;

import java.io.InputStream;

public interface PassportTemplateService {

    InputStream getTemplate(String templateId);

}
