package ru.veselov.generatebytemplate.service.impl;

import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.generatebytemplate.TestUtils;
import ru.veselov.generatebytemplate.dto.SortingParams;
import ru.veselov.generatebytemplate.entity.TemplateEntity;
import ru.veselov.generatebytemplate.exception.PageExceedsMaximumValueException;
import ru.veselov.generatebytemplate.exception.TemplateNotFoundException;
import ru.veselov.generatebytemplate.mapper.TemplateMapper;
import ru.veselov.generatebytemplate.mapper.TemplateMapperImpl;
import ru.veselov.generatebytemplate.model.Template;
import ru.veselov.generatebytemplate.repository.TemplateRepository;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"rawtypes", "unchecked"})
class TemplateStorageServiceImplTest {

    @Mock
    TemplateRepository templateRepository;

    @InjectMocks
    TemplateStorageServiceImpl templateStorageService;

    @Captor
    ArgumentCaptor<TemplateEntity> templateArgumentCaptor;

    @BeforeEach
    void init() {
        TemplateMapperImpl templateMapper = new TemplateMapperImpl();
        ReflectionTestUtils.setField(templateStorageService, "templateMapper", templateMapper, TemplateMapper.class);
        ReflectionTestUtils.setField(templateStorageService, "templatesPerPage", 5, int.class);
    }

    @Test
    void shouldSaveTemplateUnSyncedToRepo() {
        Template template = Instancio.of(Template.class)
                .ignore(Select.field(Template::getId))
                .ignore(Select.field(Template::getCreatedAt))
                .ignore(Select.field(Template::getEditedAt))
                .set(Select.field(Template::getBucket), TestUtils.TEMPLATE_BUCKET).create();

        templateStorageService.saveTemplateUnSynced(template);

        Mockito.verify(templateRepository).save(templateArgumentCaptor.capture());
        TemplateEntity captured = templateArgumentCaptor.getValue();
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(captured.getBucket()).isEqualTo(template.getBucket()),
                () -> Assertions.assertThat(captured.getTemplateName()).isEqualTo(template.getTemplateName()),
                () -> Assertions.assertThat(captured.getPtArt()).isEqualTo(template.getPtArt()),
                () -> Assertions.assertThat(captured.getFilename()).isEqualTo(template.getFilename()),
                () -> Assertions.assertThat(captured.getSynced()).isFalse()
        );
    }

    @Test
    void shouldSync() {
        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setId(TestUtils.TEMPLATE_ID);
        Mockito.when(templateRepository.findById(TestUtils.TEMPLATE_ID)).thenReturn(
                Optional.of(templateEntity));

        templateStorageService.syncTemplate(TestUtils.TEMPLATE_ID);

        Mockito.verify(templateRepository).save(templateArgumentCaptor.capture());
        TemplateEntity captured = templateArgumentCaptor.getValue();
        Assertions.assertThat(captured.getSynced()).isTrue();
    }

    @Test
    void shouldThrowExceptionIfTemplateNotFoundForSync() {
        Mockito.when(templateRepository.findById(TestUtils.TEMPLATE_ID)).thenReturn(
                Optional.empty());

        Assertions.assertThatThrownBy(() -> templateStorageService.syncTemplate(TestUtils.TEMPLATE_ID))
                .isInstanceOf(TemplateNotFoundException.class);
    }

    @Test
    void shouldFindTemplateById() {
        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setId(TestUtils.TEMPLATE_ID);
        Template template = new Template();
        template.setId(TestUtils.TEMPLATE_ID);
        Mockito.when(templateRepository.findById(TestUtils.TEMPLATE_ID)).thenReturn(Optional.of(templateEntity));

        Template foundTemplate = templateStorageService.findTemplateById(TestUtils.TEMPLATE_ID.toString());

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(templateRepository).findById(TestUtils.TEMPLATE_ID),
                () -> Assertions.assertThat(foundTemplate.getId()).isEqualTo(TestUtils.TEMPLATE_ID)
        );
    }

    @Test
    void shouldThrowNotFoundException() {
        Mockito.when(templateRepository.findById(TestUtils.TEMPLATE_ID)).thenReturn(Optional.empty());
        String idString = TestUtils.TEMPLATE_ID.toString();
        Assertions.assertThatThrownBy(() -> templateStorageService.findTemplateById(idString))
                .isInstanceOf(TemplateNotFoundException.class);
    }

    @Test
    void shouldReturnListOfTemplatesWithSortingParams() {
        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setId(TestUtils.TEMPLATE_ID);
        List<TemplateEntity> templateEntities = List.of(templateEntity);
        Page page = Mockito.mock(Page.class);
        Mockito.when(page.getContent()).thenReturn(templateEntities);
        Mockito.when(templateRepository.countAll()).thenReturn(1L);
        Mockito.when(templateRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);
        SortingParams sortingParams = new SortingParams(0, TestUtils.SORT_PT_ART, TestUtils.SORT_ASC);

        List<Template> all = templateStorageService.findAll(sortingParams);

        Template template = new Template();
        template.setId(TestUtils.TEMPLATE_ID);
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(all).contains(template).hasSize(1),
                () -> Mockito.verify(templateRepository).countAll(),
                () -> Mockito.verify(templateRepository).findAll(Mockito.any(Pageable.class))
        );
    }

    @Test
    void shouldThrowPageExceedsMaximumExceptionFindAll() {
        Mockito.when(templateRepository.countAll()).thenReturn(1L);
        SortingParams sortingParams = new SortingParams(1, TestUtils.SORT_PT_ART, TestUtils.SORT_ASC);

        Assertions.assertThatThrownBy(() -> templateStorageService.findAll(sortingParams))
                .isInstanceOf(PageExceedsMaximumValueException.class);
    }

    @Test
    void shouldReturnListOfTemplatesWithPtArtAndSortingParams() {
        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setId(TestUtils.TEMPLATE_ID);
        List<TemplateEntity> templateEntities = List.of(templateEntity);
        Page page = Mockito.mock(Page.class);
        Mockito.when(page.getContent()).thenReturn(templateEntities);
        Mockito.when(templateRepository.countAllByPtArt(TestUtils.SORT_PT_ART)).thenReturn(1L);
        Mockito.when(templateRepository.findAllByPtArt(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(page);
        SortingParams sortingParams = new SortingParams(0, TestUtils.SORT_PT_ART, TestUtils.SORT_ASC);

        List<Template> all = templateStorageService.findAllByPtArt(TestUtils.SORT_PT_ART, sortingParams);

        Template template = new Template();
        template.setId(TestUtils.TEMPLATE_ID);
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(all).contains(template).hasSize(1),
                () -> Mockito.verify(templateRepository).countAllByPtArt(TestUtils.SORT_PT_ART),
                () -> Mockito.verify(templateRepository)
                        .findAllByPtArt(Mockito.anyString(), Mockito.any(Pageable.class))
        );
    }

    @Test
    void shouldThrowPageExceedsMaximumExceptionFindAllByPtArt() {
        Mockito.when(templateRepository.countAllByPtArt(TestUtils.SORT_PT_ART)).thenReturn(1L);
        SortingParams sortingParams = new SortingParams(1, TestUtils.SORT_PT_ART, TestUtils.SORT_ASC);

        Assertions.assertThatThrownBy(() -> templateStorageService.findAllByPtArt(TestUtils.SORT_PT_ART, sortingParams))
                .isInstanceOf(PageExceedsMaximumValueException.class);
    }

    @Test
    void shouldUpdateTemplate() {
        String templateIdUUID = TestUtils.TEMPLATE_ID.toString();
        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setId(TestUtils.TEMPLATE_ID);
        templateEntity.setFilename("filename");
        Mockito.when(templateRepository.findById(TestUtils.TEMPLATE_ID))
                .thenReturn(Optional.of(templateEntity));
        Mockito.when(templateRepository.save(Mockito.any())).thenReturn(templateEntity);
        templateStorageService.updateTemplate(templateIdUUID);

        Mockito.verify(templateRepository).findById(TestUtils.TEMPLATE_ID);
        Mockito.verify(templateRepository).save(templateArgumentCaptor.capture());
        TemplateEntity captured = templateArgumentCaptor.getValue();
        Assertions.assertThat(captured.getId()).isEqualTo(templateEntity.getId());
        Assertions.assertThat(captured.getEditedAt()).isNotNull();
    }

    @Test
    void shouldThrowExceptionIfNotFoundDuringUpdate() {
        Mockito.when(templateRepository.findById(TestUtils.TEMPLATE_ID)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> templateStorageService.updateTemplate(TestUtils.TEMPLATE_ID_STRING))
                .isInstanceOf(TemplateNotFoundException.class);
    }

    @Test
    void shouldDeleteTemplate() {
        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setId(TestUtils.TEMPLATE_ID);
        templateEntity.setFilename("filename");
        Mockito.when(templateRepository.findById(TestUtils.TEMPLATE_ID)).thenReturn(Optional.of(templateEntity));

        templateStorageService.deleteTemplate(TestUtils.TEMPLATE_ID_STRING);

        Mockito.verify(templateRepository).delete(templateEntity);
    }

    @Test
    void shouldThrowExceptionIfNotTemplateForDelete() {
        Mockito.when(templateRepository.findById(TestUtils.TEMPLATE_ID)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> templateStorageService.deleteTemplate(TestUtils.TEMPLATE_ID_STRING))
                .isInstanceOf(TemplateNotFoundException.class);
    }

}
