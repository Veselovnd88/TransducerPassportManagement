package ru.veselov.miniotemplateservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.veselov.miniotemplateservice.dto.TemplateDto;
import ru.veselov.miniotemplateservice.service.PassportTemplateService;
import ru.veselov.miniotemplateservice.service.TemplateMinioService;
import ru.veselov.miniotemplateservice.service.TemplateStorageService;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassportTemplateServiceImpl implements PassportTemplateService {

    private final TemplateMinioService templateMinioService;

    private final TemplateStorageService templateStorageService;

    @Override
    @Transactional
    public void saveTemplate(MultipartFile file, TemplateDto templateInfo) {

    }
}
