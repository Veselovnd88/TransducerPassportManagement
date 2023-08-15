package ru.veselov.miniotemplateservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.miniotemplateservice.dto.TemplateDto;
import ru.veselov.miniotemplateservice.mapper.TemplateMapper;
import ru.veselov.miniotemplateservice.mapper.TemplateMapperImpl;
import ru.veselov.miniotemplateservice.model.Template;
import ru.veselov.miniotemplateservice.service.TemplateMinioService;
import ru.veselov.miniotemplateservice.service.TemplateStorageService;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class PassportTemplateServiceImplTest {

    private static final byte[] BYTES = new byte[]{1, 2, 3};

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
    void shouldCallServicesToSave() {
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

    @Test
    void shouldCallServicesToGetTemplate() {
        UUID id = UUID.randomUUID();
        String filename = "filename";
        Template template = new Template();
        template.setId(id);
        template.setFilename(filename);
        ByteArrayResource byteArrayResource = new ByteArrayResource(BYTES);
        Mockito.when(templateStorageService.findTemplateById(id.toString())).thenReturn(template);
        Mockito.when(templateMinioService.getTemplateByName(filename)).thenReturn(byteArrayResource);

        ByteArrayResource sourceBytes = passportTemplateService.getTemplate(id.toString());

        Mockito.verify(templateStorageService, Mockito.times(1)).findTemplateById(id.toString());
        Mockito.verify(templateMinioService, Mockito.times(1)).getTemplateByName(filename);
        Assertions.assertThat(sourceBytes.getByteArray()).isEqualTo(BYTES);
    }

    @Test
    void shouldNotGetTemplateIfNoDataInDB() {
        String idString = UUID.randomUUID().toString();
        Mockito.doThrow(EntityNotFoundException.class).when(templateStorageService).findTemplateById(idString);

        Assertions.assertThatThrownBy(() -> passportTemplateService.getTemplate(idString))
                .isInstanceOf(EntityNotFoundException.class);

        Mockito.verify(templateMinioService, Mockito.never()).getTemplateByName(ArgumentMatchers.anyString());
    }

}