package ru.veselov.transducersmanagingservice.service.impl;

import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.veselov.transducersmanagingservice.TestUtils;
import ru.veselov.transducersmanagingservice.dto.GeneratePassportsDto;
import ru.veselov.transducersmanagingservice.dto.SerialNumberDto;
import ru.veselov.transducersmanagingservice.entity.PassportEntity;
import ru.veselov.transducersmanagingservice.entity.SerialNumberEntity;
import ru.veselov.transducersmanagingservice.entity.TemplateEntity;
import ru.veselov.transducersmanagingservice.repository.PassportRepository;
import ru.veselov.transducersmanagingservice.repository.SerialNumberRepository;
import ru.veselov.transducersmanagingservice.repository.TemplateRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class PassportSavingServiceImplTest {

    @Mock
    TemplateRepository templateRepository;

    @Mock
    SerialNumberRepository serialNumberRepository;

    @Mock
    PassportRepository passportRepository;

    @InjectMocks
    PassportSavingServiceImpl passportSavingService;

    @Captor
    ArgumentCaptor<List<PassportEntity>> argumentCaptor;

    @Test
    void shouldSavePassportsWithFilledTemplateIdAndSerialNumber() {
        GeneratePassportsDto generatePassportDto = TestUtils.getGeneratePassportDtoWithRandomSerials();
        TemplateEntity templateEntity = Instancio.create(TemplateEntity.class);
        Mockito.when(templateRepository.findById(UUID.fromString(generatePassportDto.getTemplateId())))
                .thenReturn(Optional.of(templateEntity));
        List<SerialNumberDto> serials = generatePassportDto.getSerials();
        SerialNumberDto serialNumberDto1 = serials.get(0);
        SerialNumberDto serialNumberDto2 = serials.get(1);
        SerialNumberEntity serialNumberEntity1 = new SerialNumberEntity();
        serialNumberEntity1.setNumber(serialNumberDto1.getSerial());
        serialNumberEntity1.setId(UUID.fromString(serialNumberDto1.getSerialId()));
        SerialNumberEntity serialNumberEntity2 = new SerialNumberEntity();
        serialNumberEntity2.setNumber(serialNumberDto2.getSerial());
        serialNumberEntity2.setId(UUID.fromString(serialNumberDto2.getSerialId()));
        Mockito.when(serialNumberRepository.findById(UUID.fromString(serialNumberDto1.getSerialId())))
                .thenReturn(Optional.of(serialNumberEntity1));
        Mockito.when(serialNumberRepository.findById(UUID.fromString(serialNumberDto2.getSerialId())))
                .thenReturn(Optional.of(serialNumberEntity2));

        passportSavingService.save(generatePassportDto);

        Mockito.verify(passportRepository, Mockito.times(1)).saveAll(argumentCaptor.capture());
        List<PassportEntity> captured = argumentCaptor.getValue();
        PassportEntity passportEntity1 =
                new PassportEntity(serialNumberEntity1, templateEntity, generatePassportDto.getPrintDate());
        PassportEntity passportEntity2 =
                new PassportEntity(serialNumberEntity2, templateEntity, generatePassportDto.getPrintDate());
        Assertions.assertThat(captured).hasSize(2).contains(passportEntity1, passportEntity2);
    }

    @Test
    void shouldNotSavePassportsWithNullTemplateIdAndNullSerialNumber() {
        GeneratePassportsDto generatePassportDto = TestUtils.getGeneratePassportDtoWithRandomSerials();
        Mockito.when(templateRepository.findById(UUID.fromString(generatePassportDto.getTemplateId())))
                .thenReturn(Optional.empty());
        List<SerialNumberDto> serials = generatePassportDto.getSerials();
        SerialNumberDto serialNumberDto1 = serials.get(0);
        SerialNumberDto serialNumberDto2 = serials.get(1);
        Mockito.when(serialNumberRepository.findById(UUID.fromString(serialNumberDto1.getSerialId())))
                .thenReturn(Optional.empty());
        Mockito.when(serialNumberRepository.findById(UUID.fromString(serialNumberDto2.getSerialId())))
                .thenReturn(Optional.empty());

        passportSavingService.save(generatePassportDto);

        Mockito.verify(passportRepository, Mockito.times(1)).saveAll(argumentCaptor.capture());
        List<PassportEntity> captured = argumentCaptor.getValue();
        Assertions.assertThat(captured).isEmpty();
    }

    @Test
    void shouldSavePassportsWithNullTemplateIdAndExistingSerialNumber() {
        GeneratePassportsDto generatePassportDto = TestUtils.getGeneratePassportDtoWithRandomSerials();
        Mockito.when(templateRepository.findById(UUID.fromString(generatePassportDto.getTemplateId())))
                .thenReturn(Optional.empty());
        List<SerialNumberDto> serials = generatePassportDto.getSerials();
        SerialNumberDto serialNumberDto1 = serials.get(0);
        SerialNumberDto serialNumberDto2 = serials.get(1);
        SerialNumberEntity serialNumberEntity1 = new SerialNumberEntity();
        serialNumberEntity1.setNumber(serialNumberDto1.getSerial());
        serialNumberEntity1.setId(UUID.fromString(serialNumberDto1.getSerialId()));
        Mockito.when(serialNumberRepository.findById(UUID.fromString(serialNumberDto1.getSerialId())))
                .thenReturn(Optional.of(serialNumberEntity1));
        Mockito.when(serialNumberRepository.findById(UUID.fromString(serialNumberDto2.getSerialId())))
                .thenReturn(Optional.empty());

        passportSavingService.save(generatePassportDto);

        PassportEntity passportEntity1 =
                new PassportEntity(serialNumberEntity1, null, generatePassportDto.getPrintDate());
        Mockito.verify(passportRepository, Mockito.times(1)).saveAll(argumentCaptor.capture());
        List<PassportEntity> captured = argumentCaptor.getValue();
        Assertions.assertThat(captured).hasSize(1).contains(passportEntity1);
    }


}