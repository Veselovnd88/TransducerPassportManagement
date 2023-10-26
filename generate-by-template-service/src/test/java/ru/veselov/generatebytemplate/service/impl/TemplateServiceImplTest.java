package ru.veselov.generatebytemplate.service.impl;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.instancio.Select;
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
import ru.veselov.generatebytemplate.TestUtils;
import ru.veselov.generatebytemplate.dto.TemplateDto;
import ru.veselov.generatebytemplate.entity.TemplateEntity;
import ru.veselov.generatebytemplate.mapper.TemplateMapper;
import ru.veselov.generatebytemplate.mapper.TemplateMapperImpl;
import ru.veselov.generatebytemplate.model.Template;
import ru.veselov.generatebytemplate.service.TemplateMinioService;
import ru.veselov.generatebytemplate.service.TemplateStorageService;
import ru.veselov.generatebytemplate.validator.TemplateValidator;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class TemplateServiceImplTest {

    @Mock
    TemplateMinioService templateMinioService;

    @Mock
    TemplateStorageService templateStorageService;

    @Mock
    TemplateValidator templateValidator;

    @InjectMocks
    TemplateServiceImpl passportTemplateService;

    @Captor
    ArgumentCaptor<Template> templateArgumentCaptor;

    @BeforeEach
    void init() {
        TemplateMapperImpl templateMapper = new TemplateMapperImpl();
        ReflectionTestUtils.setField(passportTemplateService, "templateMapper", templateMapper, TemplateMapper.class);
    }

    @Test
    void shouldCallServicesToSave() {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "filename.docx",
                MediaType.MULTIPART_FORM_DATA_VALUE, TestUtils.SOURCE_BYTES);
        TemplateDto templateDto = new TemplateDto("name", "801877", "templates");
        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setId(TestUtils.TEMPLATE_ID);

        Mockito.when(templateStorageService.saveTemplateUnSynced(ArgumentMatchers.any()))
                .thenReturn(templateEntity);
        passportTemplateService.saveTemplate(multipartFile, templateDto);

        Mockito.verify(templateStorageService, Mockito.times(1)).saveTemplateUnSynced(templateArgumentCaptor.capture());
        Mockito.verify(templateMinioService, Mockito.times(1))
                .saveTemplate(ArgumentMatchers.any(), templateArgumentCaptor.capture());
        Template captured = templateArgumentCaptor.getValue();
        Assertions.assertThat(captured.getFilename()).isEqualTo("801877-name.docx");
        Mockito.verify(templateStorageService, Mockito.times(1)).syncTemplate(templateEntity.getId());
    }

    @Test
    void shouldNotSaveIfNameExists() {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "filename.docx",
                MediaType.MULTIPART_FORM_DATA_VALUE, TestUtils.SOURCE_BYTES);
        TemplateDto templateDto = new TemplateDto("name", "801877", "templates");
        Mockito.doThrow(EntityExistsException.class).when(templateValidator)
                .validateTemplateName(ArgumentMatchers.anyString());

        Assertions.assertThatThrownBy(() -> passportTemplateService.saveTemplate(multipartFile, templateDto))
                .isInstanceOf(EntityExistsException.class);

        Mockito.verify(templateStorageService, Mockito.never()).saveTemplateUnSynced(ArgumentMatchers.any());
    }

    @Test
    void shouldCallServicesToGetTemplate() {
        UUID id = UUID.randomUUID();
        String filename = "filename";
        Template template = new Template();
        template.setId(id);
        template.setFilename(filename);
        ByteArrayResource byteArrayResource = new ByteArrayResource(TestUtils.SOURCE_BYTES);
        Mockito.when(templateStorageService.findTemplateById(id.toString())).thenReturn(template);
        Mockito.when(templateMinioService.getTemplateByName(filename)).thenReturn(byteArrayResource);

        ByteArrayResource sourceBytes = passportTemplateService.getTemplate(id.toString());

        Mockito.verify(templateStorageService, Mockito.times(1)).findTemplateById(id.toString());
        Mockito.verify(templateMinioService, Mockito.times(1)).getTemplateByName(filename);
        Assertions.assertThat(sourceBytes.getByteArray()).isEqualTo(TestUtils.SOURCE_BYTES);
    }

    @Test
    void shouldNotGetTemplateIfNoDataInDB() {
        String idString = UUID.randomUUID().toString();
        Mockito.doThrow(EntityNotFoundException.class).when(templateStorageService).findTemplateById(idString);

        Assertions.assertThatThrownBy(() -> passportTemplateService.getTemplate(idString))
                .isInstanceOf(EntityNotFoundException.class);

        Mockito.verify(templateMinioService, Mockito.never()).getTemplateByName(ArgumentMatchers.anyString());
    }

    @Test
    void shouldCallServicesToUpdate() {
        String templateId = TestUtils.TEMPLATE_ID.toString();
        MockMultipartFile multipartFile = new MockMultipartFile("file", "filename.docx",
                MediaType.MULTIPART_FORM_DATA_VALUE, TestUtils.SOURCE_BYTES);
        Template template = Instancio.of(Template.class)
                .set(Select.field("id"), TestUtils.TEMPLATE_ID).create();
        Mockito.when(templateStorageService.findTemplateById(templateId)).thenReturn(template);

        passportTemplateService.updateTemplate(multipartFile, templateId);

        Mockito.verify(templateStorageService, Mockito.times(1)).findTemplateById(templateId);
        Mockito.verify(templateStorageService, Mockito.times(1)).updateTemplate(templateId);
        Mockito.verify(templateMinioService, Mockito.times(1))
                .updateTemplate(ArgumentMatchers.any(), templateArgumentCaptor.capture());
        Template captured = templateArgumentCaptor.getValue();
        Assertions.assertThat(captured.getFilename()).isEqualTo(template.getFilename());
    }

    @Test
    void shouldNotCallMinioServiceToUpdateIfException() {
        String templateId = UUID.randomUUID().toString();
        Mockito.doThrow(EntityNotFoundException.class).when(templateStorageService).findTemplateById(templateId);
        MockMultipartFile multipartFile = new MockMultipartFile("file", "filename.docx",
                MediaType.MULTIPART_FORM_DATA_VALUE, TestUtils.SOURCE_BYTES);

        Assertions.assertThatThrownBy(() -> passportTemplateService.updateTemplate(multipartFile, templateId))
                .isInstanceOf(EntityNotFoundException.class);

        Mockito.verify(templateMinioService, Mockito.never())
                .updateTemplate(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void shouldCallServicesToDeleteTemplate() {
        String templateId = UUID.randomUUID().toString();
        Template template = Instancio.of(Template.class)
                .set(Select.field(Template.class, "id"), UUID.fromString(templateId))
                .create();
        Mockito.when(templateStorageService.findTemplateById(templateId)).thenReturn(template);
        passportTemplateService.deleteTemplate(templateId);

        Mockito.verify(templateStorageService, Mockito.times(1)).deleteTemplate(templateId);
        Mockito.verify(templateMinioService, Mockito.times(1)).deleteTemplate(template.getFilename());
    }

}
