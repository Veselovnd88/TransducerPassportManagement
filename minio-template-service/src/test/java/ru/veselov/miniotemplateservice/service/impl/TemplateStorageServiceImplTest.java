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
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.miniotemplateservice.entity.TemplateEntity;
import ru.veselov.miniotemplateservice.mapper.TemplateMapper;
import ru.veselov.miniotemplateservice.mapper.TemplateMapperImpl;
import ru.veselov.miniotemplateservice.model.Template;
import ru.veselov.miniotemplateservice.repository.TemplateRepository;
import ru.veselov.miniotemplateservice.validator.TemplateValidator;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class TemplateStorageServiceImplTest {

    public static final String BUCKET = "templates";

    public static final UUID ID = UUID.randomUUID();

    @Mock
    TemplateRepository templateRepository;

    @Mock
    TemplateValidator templateValidator;

    @InjectMocks
    TemplateStorageServiceImpl templateStorageService;

    @Captor
    ArgumentCaptor<TemplateEntity> templateArgumentCaptor;

    @BeforeEach
    void init() {
        TemplateMapper templateMapper = new TemplateMapperImpl();
        ReflectionTestUtils.setField(templateStorageService, "templateMapper", templateMapper, TemplateMapper.class);
    }

    @Test
    void shouldSaveTemplateToRepo() {
        Template template = Instancio.of(Template.class)
                .ignore(Select.field(Template::getId))
                .ignore(Select.field(Template::getCreatedAt))
                .ignore(Select.field(Template::getEditedAt))
                .set(Select.field(Template::getBucket), BUCKET).create();

        templateStorageService.saveTemplate(template);

        Mockito.verify(templateRepository, Mockito.times(1)).save(templateArgumentCaptor.capture());
        TemplateEntity captured = templateArgumentCaptor.getValue();
        Assertions.assertThat(captured.getBucket()).isEqualTo(template.getBucket());
        Assertions.assertThat(captured.getTemplateName()).isEqualTo(template.getTemplateName());
        Assertions.assertThat(captured.getPtArt()).isEqualTo(template.getPtArt());
        Assertions.assertThat(captured.getFilename()).isEqualTo(template.getFilename());
    }

    @Test
    void shouldNotSaveTemplateToRepo() {
        Template template = Instancio.of(Template.class)
                .ignore(Select.field(Template::getId))
                .ignore(Select.field(Template::getCreatedAt))
                .ignore(Select.field(Template::getEditedAt))
                .set(Select.field(Template::getBucket), BUCKET).create();
        Mockito.doThrow(EntityExistsException.class).when(templateValidator)
                .validateTemplateName(ArgumentMatchers.anyString());

        Assertions.assertThatThrownBy(() -> templateStorageService.saveTemplate(template))
                .isInstanceOf(EntityExistsException.class);

        Mockito.verify(templateRepository, Mockito.never()).save(templateArgumentCaptor.capture());
    }

    @Test
    void shouldFindTemplateById() {
        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setId(ID);
        Template template = new Template();
        template.setId(ID);
        Mockito.when(templateRepository.findById(ID)).thenReturn(Optional.of(templateEntity));

        Template foundTemplate = templateStorageService.findTemplateById(ID.toString());

        Mockito.verify(templateRepository, Mockito.times(1)).findById(ID);
        Assertions.assertThat(foundTemplate.getId()).isEqualTo(ID);
    }

    @Test
    void shouldThrowNotFoundException() {
        Mockito.when(templateRepository.findById(ID)).thenReturn(Optional.empty());
        String idString = ID.toString();
        Assertions.assertThatThrownBy(() -> templateStorageService.findTemplateById(idString))
                .isInstanceOf(EntityNotFoundException.class);
    }

}
