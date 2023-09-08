package ru.veselov.transducersmanagingservice.service.impl;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.transducersmanagingservice.TestConstants;
import ru.veselov.transducersmanagingservice.dto.TransducerDto;
import ru.veselov.transducersmanagingservice.entity.TransducerEntity;
import ru.veselov.transducersmanagingservice.mapper.TransducerMapper;
import ru.veselov.transducersmanagingservice.mapper.TransducerMapperImpl;
import ru.veselov.transducersmanagingservice.model.Transducer;
import ru.veselov.transducersmanagingservice.repository.TransducerRepository;
import ru.veselov.transducersmanagingservice.validator.TransducerValidator;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class TransducerServiceImplTest {

    @Mock
    TransducerRepository transducerRepository;

    @Mock
    TransducerValidator transducerValidator;

    @InjectMocks
    TransducerServiceImpl transducerService;

    TransducerMapperImpl transducerMapper = new TransducerMapperImpl();

    @Captor
    ArgumentCaptor<TransducerEntity> transducerEntityArgumentCaptor;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(transducerService, "transducersPerPage", 10, int.class);
        ReflectionTestUtils.
                setField(transducerService, "transducerMapper", transducerMapper, TransducerMapper.class);
    }

    @Test
    void shouldSaveTransducer() {
        TransducerDto transducerDto = Instancio.create(TransducerDto.class);
        TransducerEntity transducerEntity = transducerMapper.toEntity(transducerDto);
        transducerEntity.setId(TestConstants.TRANSDUCER_ID);
        Mockito.when(transducerRepository.save(ArgumentMatchers.any())).thenReturn(transducerEntity);

        transducerService.saveTransducer(transducerDto);

        Mockito.verify(transducerRepository, Mockito.times(1)).save(transducerEntityArgumentCaptor.capture());
        Mockito.verify(transducerValidator, Mockito.times(1)).validatePtArt(transducerDto.getArt());
        TransducerEntity captured = transducerEntityArgumentCaptor.getValue();
        Assertions.assertThat(captured.getTransducerName()).isEqualTo(transducerDto.getTransducerName());
        Assertions.assertThat(captured.getArt()).isEqualTo(transducerDto.getArt());
    }

    @Test
    void shouldFindTransducerById() {
        TransducerEntity transducerEntity = Instancio.create(TransducerEntity.class);
        transducerEntity.setId(TestConstants.TRANSDUCER_ID);
        Mockito.when(transducerRepository
                .findById(TestConstants.TRANSDUCER_ID)).thenReturn(Optional.of(transducerEntity));

        Transducer foundTransducer = transducerService.findTransducerById(TestConstants.TRANSDUCER_ID.toString());

        Assertions.assertThat(foundTransducer.getId()).isEqualTo(transducerEntity.getId().toString());
        Mockito.verify(transducerRepository, Mockito.times(1)).findById(TestConstants.TRANSDUCER_ID);
    }

    @Test
    void shouldThrowEntityNotFoundException() {
        Mockito.when(transducerRepository
                .findById(TestConstants.TRANSDUCER_ID)).thenReturn(Optional.empty());
        String transducerId = TestConstants.TRANSDUCER_ID.toString();

        Assertions.assertThatThrownBy(() -> transducerService.findTransducerById(transducerId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldFindTransducerByArt() {
        TransducerEntity transducerEntity = Instancio.of(TransducerEntity.class)
                .set(Select.field("art"), TestConstants.PT_ART).create();

        Mockito.when(transducerRepository
                .findByArt(TestConstants.PT_ART)).thenReturn(Optional.of(transducerEntity));

        Transducer foundTransducer = transducerService.findTransducerByArt(TestConstants.PT_ART);

        Assertions.assertThat(foundTransducer.getArt()).isEqualTo(transducerEntity.getArt());
        Mockito.verify(transducerRepository, Mockito.times(1)).findByArt(TestConstants.PT_ART);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionIfArt() {
        Mockito.when(transducerRepository
                .findByArt(TestConstants.PT_ART)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> transducerService.findTransducerByArt(TestConstants.PT_ART))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldDelete() {
        TransducerEntity transducerEntity = Instancio.of(TransducerEntity.class)
                .set(Select.field("art"), TestConstants.PT_ART).create();
        transducerEntity.setId(TestConstants.TRANSDUCER_ID);
        Mockito.when(transducerRepository.findById(TestConstants.TRANSDUCER_ID))
                .thenReturn(Optional.of(transducerEntity));

        transducerService.deleteTransducer(TestConstants.TRANSDUCER_ID.toString());

        Mockito.verify(transducerRepository, Mockito.times(1)).delete(transducerEntity);
    }

    @Test
    void shouldThrowExceptionIfNoIdForDeleting() {
        Mockito.when(transducerRepository.findById(TestConstants.TRANSDUCER_ID)).thenReturn(Optional.empty());
        String transducerId = TestConstants.TRANSDUCER_ID.toString();
        Assertions.assertThatThrownBy(() -> transducerService.deleteTransducer(transducerId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldUpdateTransducerDifferentArt() {
        TransducerDto transducerDto = Instancio.of(TransducerDto.class).set(Select.field("art"), "15").create();
        TransducerEntity transducerEntity = Instancio.of(TransducerEntity.class)
                .set(Select.field("art"), TestConstants.PT_ART).create();
        transducerEntity.setId(TestConstants.TRANSDUCER_ID);
        Mockito.when(transducerRepository.findById(TestConstants.TRANSDUCER_ID))
                .thenReturn(Optional.of(transducerEntity));

        transducerService.updateTransducer(TestConstants.TRANSDUCER_ID.toString(), transducerDto);
        Mockito.verify(transducerRepository, Mockito.times(1)).save(transducerEntityArgumentCaptor.capture());
        TransducerEntity captured = transducerEntityArgumentCaptor.getValue();
        Assertions.assertThat(captured.getArt()).isEqualTo(transducerDto.getArt());
        Mockito.verify(transducerValidator, Mockito.times(1)).validatePtArt(transducerDto.getArt());
    }

    @Test
    void shouldUpdateTransducerSameArt() {
        TransducerDto transducerDto = Instancio
                .of(TransducerDto.class).set(Select.field("art"), TestConstants.PT_ART).create();
        TransducerEntity transducerEntity = Instancio.of(TransducerEntity.class)
                .set(Select.field("art"), TestConstants.PT_ART).create();
        transducerEntity.setId(TestConstants.TRANSDUCER_ID);
        Mockito.when(transducerRepository.findById(TestConstants.TRANSDUCER_ID))
                .thenReturn(Optional.of(transducerEntity));

        transducerService.updateTransducer(TestConstants.TRANSDUCER_ID.toString(), transducerDto);
        Mockito.verify(transducerRepository, Mockito.times(1)).save(transducerEntityArgumentCaptor.capture());
        TransducerEntity captured = transducerEntityArgumentCaptor.getValue();
        Assertions.assertThat(captured.getArt()).isEqualTo(transducerDto.getArt());
        Mockito.verify(transducerValidator, Mockito.never()).validatePtArt(TestConstants.PT_ART);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionIfUpdatingTransducerNotFound() {
        TransducerDto transducerDto = Instancio.create(TransducerDto.class);
        Mockito.when(transducerRepository.findById(TestConstants.TRANSDUCER_ID)).thenReturn(Optional.empty());
        String transducerId = TestConstants.TRANSDUCER_ID.toString();
        Assertions.assertThatThrownBy(() -> transducerService.updateTransducer(transducerId, transducerDto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldGetAllTransducers() {
        TransducerEntity transducerEntity = Instancio.create(TransducerEntity.class);
        Mockito.when(transducerRepository.countAll()).thenReturn(1L);
        Page<TransducerEntity> page = Mockito.mock(Page.class);
        Mockito.when(page.getContent()).thenReturn(List.of(transducerEntity));
        Mockito.when(transducerRepository.getAll(ArgumentMatchers.any(Pageable.class))).thenReturn(page);

        List<Transducer> all = transducerService.getAll(TestConstants.SORTING_PARAMS);

        Assertions.assertThat(all).hasSize(1);
    }

}