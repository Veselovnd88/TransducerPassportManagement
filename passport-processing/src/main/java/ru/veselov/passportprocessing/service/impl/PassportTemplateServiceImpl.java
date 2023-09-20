package ru.veselov.passportprocessing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import ru.veselov.passportprocessing.service.PassportTemplateService;
import ru.veselov.passportprocessing.service.TemplateStorageHttpClient;

/*
 *Service responsible for getting template bytes from storage
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class PassportTemplateServiceImpl implements PassportTemplateService {

    private final TemplateStorageHttpClient templateStorageHttpClient;

    @Override
    public ByteArrayResource getTemplate(String templateId) {
        log.info("Retrieving template with [id:{}]", templateId);
        return templateStorageHttpClient.sendRequestToGetTemplate(templateId);
    }

}
