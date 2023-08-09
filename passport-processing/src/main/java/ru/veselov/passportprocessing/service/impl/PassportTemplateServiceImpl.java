package ru.veselov.passportprocessing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import ru.veselov.passportprocessing.service.PassportTemplateService;
import ru.veselov.passportprocessing.service.TemplateStorageHttpClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassportTemplateServiceImpl implements PassportTemplateService {

    private final TemplateStorageHttpClient templateStorageHttpClient;

    @Override
    @Cacheable(value = "templates")
    public ByteArrayResource getTemplate(String templateId) {
        return templateStorageHttpClient.sendRequestToGetTemplate(templateId);
    }

}
