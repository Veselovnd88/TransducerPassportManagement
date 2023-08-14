package ru.veselov.miniotemplateservice.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.miniotemplateservice.dto.TemplateDto;
import ru.veselov.miniotemplateservice.mapper.TemplateMapper;
import ru.veselov.miniotemplateservice.mapper.TemplateMapperImpl;
import ru.veselov.miniotemplateservice.model.Template;
import ru.veselov.miniotemplateservice.service.TemplateMinioService;
import ru.veselov.miniotemplateservice.service.TemplateStorageService;

@ExtendWith(MockitoExtension.class)
class PassportTemplateServiceImplTest {

    @Mock
    TemplateMinioService templateMinioService;

    @Mock
    TemplateStorageService templateStorageService;

    @InjectMocks
    PassportTemplateServiceImpl passportTemplateService;

    @Captor
    ArgumentCaptor<Template> templateArgumentCaptor;

    @BeforeEach
    void init() {
        TemplateMapper templateMapper = new TemplateMapperImpl();
        ReflectionTestUtils.setField(passportTemplateService, "templateMapper", templateMapper, TemplateMapper.class);
    }

    @Test
    void shouldCallServices() {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "filename.docx",
                MediaType.MULTIPART_FORM_DATA_VALUE, new byte[]{1, 2, 3});
        TemplateDto templateDto = new TemplateDto("name", "801877", "templates");

        passportTemplateService.saveTemplate(multipartFile, templateDto);

        Mockito.verify(templateStorageService, Mockito.times(1)).saveTemplate(templateArgumentCaptor.capture());
        Mockito.verify(templateMinioService, Mockito.times(1))
                .saveTemplate(ArgumentMatchers.any(), templateArgumentCaptor.capture());
        Template captured = templateArgumentCaptor.getValue();
        Assertions.assertThat(captured.getFilename()).isEqualTo("801877-name.docx");
    }

}