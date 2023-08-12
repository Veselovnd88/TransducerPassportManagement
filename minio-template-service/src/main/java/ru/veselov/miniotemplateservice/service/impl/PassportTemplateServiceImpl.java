package ru.veselov.miniotemplateservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.veselov.miniotemplateservice.dto.TemplateDto;
import ru.veselov.miniotemplateservice.service.PassportTemplateService;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassportTemplateServiceImpl implements PassportTemplateService {
    @Override
    public void saveTemplate(MultipartFile file, TemplateDto templateInfo) {
        log.info("{}", file);
        log.info("{}", templateInfo);
        System.out.println(file);
    }
}
