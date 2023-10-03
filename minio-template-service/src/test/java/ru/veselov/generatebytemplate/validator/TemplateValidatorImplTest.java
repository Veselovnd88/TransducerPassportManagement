package ru.veselov.generatebytemplate.validator;

import jakarta.persistence.EntityExistsException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.veselov.generatebytemplate.entity.TemplateEntity;
import ru.veselov.generatebytemplate.repository.TemplateRepository;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TemplateValidatorImplTest {

    public static final String TEMPLATE_NAME = "templateName";

    @Mock
    TemplateRepository templateRepository;

    @InjectMocks
    TemplateValidatorImpl templateValidator;

    @Test
    void shouldPass() {

        Mockito.when(templateRepository.findByName(TEMPLATE_NAME))
                .thenReturn(Optional.empty());
        Assertions.assertThatNoException().isThrownBy(() -> templateValidator.validateTemplateName(TEMPLATE_NAME));
    }

    @Test
    void shouldThrowException() {
        Mockito.when(templateRepository.findByName(TEMPLATE_NAME)).thenReturn(
                Optional.of(new TemplateEntity()));
        Assertions.assertThatThrownBy(() -> templateValidator.validateTemplateName(TEMPLATE_NAME))
                .isInstanceOf(EntityExistsException.class);
    }

}
