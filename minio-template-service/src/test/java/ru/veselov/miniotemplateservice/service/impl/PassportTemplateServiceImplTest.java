package ru.veselov.miniotemplateservice.service.impl;

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
import ru.veselov.miniotemplateservice.dto.TemplateDto;
import ru.veselov.miniotemplateservice.mapper.TemplateMapper;
import ru.veselov.miniotemplateservice.mapper.TemplateMapperImpl;
import ru.veselov.miniotemplateservice.model.Template;
import ru.veselov.miniotemplateservice.service.TemplateMinioService;
import ru.veselov.miniotemplateservice.service.TemplateStorageService;
import ru.veselov.miniotemplateservice.validator.TemplateValidator;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class PassportTemplateServiceImplTest {

    private static final byte[] BYTES = new byte[]{1, 2, 3};

    @Mock
    TemplateMinioService templateMinioService;

    @Mock
    TemplateStorageService templateStorageService;

    @Mock
    TemplateValidator templateValidator;

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
    void shouldNotSaveIfNameExists() {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "filename.docx",
                MediaType.MULTIPART_FORM_DATA_VALUE, new byte[]{1, 2, 3});
        TemplateDto templateDto = new TemplateDto("name", "801877", "templates");
        Mockito.doThrow(EntityExistsException.class).when(templateValidator)
                .validateTemplateName(ArgumentMatchers.anyString());

        Assertions.assertThatThrownBy(() -> passportTemplateService.saveTemplate(multipartFile, templateDto))
                .isInstanceOf(EntityExistsException.class);

        Mockito.verify(templateStorageService, Mockito.never()).saveTemplate(ArgumentMatchers.any());
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

    @Test
    void shouldCallServicesToUpdate() {
        String templateId = UUID.randomUUID().toString();
        MockMultipartFile multipartFile = new MockMultipartFile("file", "filename.docx",
                MediaType.MULTIPART_FORM_DATA_VALUE, new byte[]{1, 2, 3});
        Template template = Instancio.create(Template.class);
        Mockito.when(templateStorageService.updateTemplate(templateId)).thenReturn(template);

        passportTemplateService.updateTemplate(multipartFile, templateId);

        Mockito.verify(templateStorageService, Mockito.times(1)).updateTemplate(templateId);
        Mockito.verify(templateMinioService, Mockito.times(1))
                .updateTemplate(ArgumentMatchers.any(), templateArgumentCaptor.capture());
        Template captured = templateArgumentCaptor.getValue();
        Assertions.assertThat(captured.getFilename()).isEqualTo(template.getFilename());
    }

    @Test
    void shouldNotCallMinioServiceToUpdateIfException() {
        String templateId = UUID.randomUUID().toString();
        Mockito.doThrow(EntityNotFoundException.class).when(templateStorageService).updateTemplate(templateId);
        MockMultipartFile multipartFile = new MockMultipartFile("file", "filename.docx",
                MediaType.MULTIPART_FORM_DATA_VALUE, new byte[]{1, 2, 3});

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
        Mockito.when(templateStorageService.deleteTemplate(templateId)).thenReturn(Optional.of(template));
        passportTemplateService.deleteTemplate(templateId);

        Mockito.verify(templateStorageService, Mockito.times(1)).deleteTemplate(templateId);
        Mockito.verify(templateMinioService, Mockito.times(1)).deleteTemplate(template.getFilename());
    }

    @Test
    void shouldNotCallServicesToDeleteTemplate() {
        String templateId = UUID.randomUUID().toString();
        Mockito.when(templateStorageService.deleteTemplate(templateId)).thenReturn(Optional.empty());
        passportTemplateService.deleteTemplate(templateId);

        Mockito.verify(templateStorageService, Mockito.times(1)).deleteTemplate(templateId);
        Mockito.verify(templateMinioService, Mockito.never()).deleteTemplate(ArgumentMatchers.anyString());
    }

}