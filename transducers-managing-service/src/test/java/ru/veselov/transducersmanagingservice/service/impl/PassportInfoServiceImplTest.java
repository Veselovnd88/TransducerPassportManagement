package ru.veselov.transducersmanagingservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.transducersmanagingservice.TestConstants;
import ru.veselov.transducersmanagingservice.entity.PassportEntity;
import ru.veselov.transducersmanagingservice.mapper.PassportMapper;
import ru.veselov.transducersmanagingservice.mapper.PassportMapperImpl;
import ru.veselov.transducersmanagingservice.model.Passport;
import ru.veselov.transducersmanagingservice.repository.PassportRepository;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"rawtypes", "unchecked"})
class PassportInfoServiceImplTest {

    @Mock
    PassportRepository passportRepository;

    @InjectMocks
    PassportInfoServiceImpl passportInfoService;

    PassportMapper passportMapper;

    @BeforeEach
    void init() {
        passportMapper = new PassportMapperImpl();
        ReflectionTestUtils.setField(passportInfoService, "passportMapper", passportMapper, PassportMapper.class);
        ReflectionTestUtils.setField(passportInfoService, "passportsPerPage", 10, int.class);
    }

    @Test
    void shouldGetById() {
        PassportEntity passportEntity = Instancio.create(PassportEntity.class);
        Mockito.when(passportRepository.findById(TestConstants.PASSPORT_ID)).thenReturn(Optional.of(passportEntity));

        Passport foundPassport = passportInfoService.getById(TestConstants.PASSPORT_ID.toString());

        Passport passport = passportMapper.toModel(passportEntity);
        Mockito.verify(passportRepository, Mockito.times(1)).findById(TestConstants.PASSPORT_ID);
        Assertions.assertThat(foundPassport).isEqualTo(passport);
    }

    @Test
    void shouldThrowExceptionIfNoPassportNotFoundById() {
        Mockito.when(passportRepository.findById(TestConstants.PASSPORT_ID)).thenReturn(Optional.empty());
        String uuid = TestConstants.PASSPORT_ID.toString();

        Assertions.assertThatThrownBy(() -> passportInfoService.getById(uuid))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getAllBetweenDates() {
        Mockito.when(passportRepository.countAllBetweenDates(TestConstants.DATE_AFTER, TestConstants.DATE_BEFORE))
                .thenReturn(1L);
        Page page = Mockito.mock(Page.class);
        List<PassportEntity> basicPassportEntities = getBasicPassportEntities();
        Mockito.when(page.getContent()).thenReturn(basicPassportEntities);
        Mockito.when(passportRepository.findAllBetweenDates(ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any())).thenReturn(page);

        List<Passport> allBetweenDates = passportInfoService
                .getAllBetweenDates(TestConstants.SORTING_PARAMS, TestConstants.DATE_PARAMS);
        List<Passport> passports = passportMapper.toModels(basicPassportEntities);

        Assertions.assertThat(allBetweenDates).isEqualTo(passports);
        Mockito.verify(passportRepository, Mockito.times(1)).countAllBetweenDates(
                TestConstants.DATE_AFTER, TestConstants.DATE_BEFORE);
        Mockito.verify(passportRepository, Mockito.times(1)).findAllBetweenDates(ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void getAllForSerialBetweenDates() {
        Mockito.when(passportRepository.countBySerialAllBetweenDates(
                TestConstants.NUMBER,
                TestConstants.DATE_AFTER,
                TestConstants.DATE_BEFORE)).thenReturn(1L);
        Page page = Mockito.mock(Page.class);
        List<PassportEntity> basicPassportEntities = getBasicPassportEntities();
        Mockito.when(page.getContent()).thenReturn(basicPassportEntities);
        Mockito.when(passportRepository.findAllBySerialBetweenDates(ArgumentMatchers.anyString(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any())).thenReturn(page);

        List<Passport> allBetweenDates = passportInfoService.getAllForSerialBetweenDates(
                TestConstants.NUMBER,
                TestConstants.SORTING_PARAMS,
                TestConstants.DATE_PARAMS);
        List<Passport> passports = passportMapper.toModels(basicPassportEntities);

        Assertions.assertThat(allBetweenDates).isEqualTo(passports);
        Mockito.verify(passportRepository, Mockito.times(1)).countBySerialAllBetweenDates(TestConstants.NUMBER,
                TestConstants.DATE_AFTER, TestConstants.DATE_BEFORE);
        Mockito.verify(passportRepository, Mockito.times(1)).findAllBySerialBetweenDates(ArgumentMatchers.anyString(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any());
    }

    @Test
    void getAllForPtArtBetweenDates() {
        Mockito.when(passportRepository.countByPtArtAllBetweenDates(
                TestConstants.PT_ART,
                TestConstants.DATE_AFTER,
                TestConstants.DATE_BEFORE)).thenReturn(1L);
        Page page = Mockito.mock(Page.class);
        List<PassportEntity> basicPassportEntities = getBasicPassportEntities();
        Mockito.when(page.getContent()).thenReturn(basicPassportEntities);
        Mockito.when(passportRepository.findAllByPtArtBetweenDates(ArgumentMatchers.anyString(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any())).thenReturn(page);

        List<Passport> allBetweenDates = passportInfoService.getAllForPtArtBetweenDates(
                TestConstants.PT_ART,
                TestConstants.SORTING_PARAMS,
                TestConstants.DATE_PARAMS);
        List<Passport> passports = passportMapper.toModels(basicPassportEntities);

        Assertions.assertThat(allBetweenDates).isEqualTo(passports);
        Mockito.verify(passportRepository, Mockito.times(1)).countByPtArtAllBetweenDates(TestConstants.PT_ART,
                TestConstants.DATE_AFTER, TestConstants.DATE_BEFORE);
        Mockito.verify(passportRepository, Mockito.times(1)).findAllByPtArtBetweenDates(ArgumentMatchers.anyString(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any());
    }

    @Test
    void shouldDeletePassportById() {
        PassportEntity passportEntity = Instancio.create(PassportEntity.class);
        Mockito.when(passportRepository.findById(TestConstants.PASSPORT_ID)).thenReturn(Optional.of(passportEntity));

        passportInfoService.deleteById(TestConstants.PASSPORT_ID.toString());

        Mockito.verify(passportRepository, Mockito.times(1)).delete(passportEntity);
    }

    @Test
    void shouldThrowExceptionIfNoPassportNotFoundByIdForDeleting() {
        Mockito.when(passportRepository.findById(TestConstants.PASSPORT_ID)).thenReturn(Optional.empty());
        String uuid = TestConstants.PASSPORT_ID.toString();

        Assertions.assertThatThrownBy(() -> passportInfoService.deleteById(uuid))
                .isInstanceOf(EntityNotFoundException.class);
    }

    private List<PassportEntity> getBasicPassportEntities() {
        return List.of(Instancio.create(PassportEntity.class),
                Instancio.create(PassportEntity.class));
    }

}