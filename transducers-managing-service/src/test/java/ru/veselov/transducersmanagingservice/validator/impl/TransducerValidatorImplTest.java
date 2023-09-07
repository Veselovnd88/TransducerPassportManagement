package ru.veselov.transducersmanagingservice.validator.impl;

import jakarta.persistence.EntityExistsException;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.veselov.transducersmanagingservice.TestConstants;
import ru.veselov.transducersmanagingservice.entity.TransducerEntity;
import ru.veselov.transducersmanagingservice.repository.TransducerRepository;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TransducerValidatorImplTest {

    @Mock
    TransducerRepository transducerRepository;

    @InjectMocks
    TransducerValidatorImpl transducerValidator;

    @Test
    void shouldValidatePtArt() {
        Mockito.when(transducerRepository.findByArt(TestConstants.PT_ART))
                .thenReturn(Optional.empty());

        Assertions.assertThatNoException().isThrownBy(() -> transducerValidator.validatePtArt(TestConstants.PT_ART));

        Mockito.verify(transducerRepository, Mockito.times(1)).findByArt(TestConstants.PT_ART);
    }

    @Test
    void shouldThrowEntityExistsException() {
        Mockito.when(transducerRepository.findByArt(TestConstants.PT_ART))
                .thenReturn(Optional.of(Instancio.create(TransducerEntity.class)));

        Assertions.assertThatThrownBy(() ->
                transducerValidator.validatePtArt(TestConstants.PT_ART)).isInstanceOf(EntityExistsException.class);
    }
}