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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.transducersmanagingservice.TestConstants;
import ru.veselov.transducersmanagingservice.dto.SerialsDto;
import ru.veselov.transducersmanagingservice.entity.CustomerEntity;
import ru.veselov.transducersmanagingservice.entity.SerialNumberEntity;
import ru.veselov.transducersmanagingservice.entity.TransducerEntity;
import ru.veselov.transducersmanagingservice.mapper.SerialNumberMapper;
import ru.veselov.transducersmanagingservice.mapper.SerialNumberMapperImpl;
import ru.veselov.transducersmanagingservice.model.SerialNumber;
import ru.veselov.transducersmanagingservice.repository.CustomerRepository;
import ru.veselov.transducersmanagingservice.repository.SerialNumberRepository;
import ru.veselov.transducersmanagingservice.repository.TransducerRepository;
import ru.veselov.transducersmanagingservice.service.XlsxParseService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"rawtypes", "unchecked"})
class SerialNumberServiceImplTest {

    @Mock
    SerialNumberRepository serialNumberRepository;

    @Mock
    TransducerRepository transducerRepository;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    XlsxParseService xlsxParseService;

    @InjectMocks
    SerialNumberServiceImpl serialNumberService;

    @Captor
    ArgumentCaptor<List<SerialNumberEntity>> serialListArgumentCaptor;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(serialNumberService, "serialsPerPage", 100, int.class);
        ReflectionTestUtils.setField(serialNumberService, "serialNumberMapper", new SerialNumberMapperImpl(),
                SerialNumberMapper.class);

    }

    @Test
    void shouldFindSerialByNumber() {
        String number = "anyNumber";
        SerialNumberEntity serialNumberEntity = getBaseSerialNumberEntity();
        Mockito.when(serialNumberRepository.findAllByNumber(number)).thenReturn(List.of(serialNumberEntity));
        List<SerialNumber> numbers = serialNumberService.findByNumber(number);

        Assertions.assertThat(numbers.get(0)).isNotNull();
        Assertions.assertThat(numbers.get(0).getNumber()).isEqualTo(serialNumberEntity.getNumber());
        Assertions.assertThat(numbers.get(0).getPtArt()).isEqualTo(serialNumberEntity.getPtArt());
        Assertions.assertThat(numbers.get(0).getCustomer()).isEqualTo(serialNumberEntity.getCustomer().getName());
        Mockito.verify(serialNumberRepository, Mockito.times(1)).findAllByNumber(number);
    }

    @Test
    void shouldFindSerialById() {
        String serialIdString = TestConstants.SERIAL_ID.toString();
        SerialNumberEntity serialNumberEntity = getBaseSerialNumberEntity();
        Mockito.when(serialNumberRepository.findById(TestConstants.SERIAL_ID)).thenReturn(Optional.of(serialNumberEntity));

        SerialNumber serialNumber = serialNumberService.findById(serialIdString);

        Assertions.assertThat(serialNumber.getNumber()).isEqualTo(serialNumberEntity.getNumber());
        Assertions.assertThat(serialNumber.getCustomer()).isEqualTo(serialNumberEntity.getCustomer().getName());
        Mockito.verify(serialNumberRepository, Mockito.times(1)).findById(TestConstants.SERIAL_ID);
    }

    @Test
    void shouldThrowExceptionIfNoSerialNumberWithSuchId() {
        String serialIdString = TestConstants.SERIAL_ID.toString();
        Mockito.when(serialNumberRepository.findById(TestConstants.SERIAL_ID)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> serialNumberService.findById(serialIdString))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldSerialsFindByArt() {
        Mockito.when(serialNumberRepository.countAllByPtArt(TestConstants.PT_ART)).thenReturn(1L);
        Page page = Mockito.mock(Page.class);
        Mockito.when(page.getContent()).thenReturn(List.of(getBaseSerialNumberEntity()));
        Mockito.when(serialNumberRepository.findAllByPtArt(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenReturn(page);

        List<SerialNumber> foundSerials = serialNumberService.findByArt(TestConstants.SORTING_PARAMS, TestConstants.PT_ART);

        Assertions.assertThat(foundSerials).hasSize(1);
        Mockito.verify(serialNumberRepository, Mockito.times(1)).countAllByPtArt(TestConstants.PT_ART);
        Mockito.verify(serialNumberRepository, Mockito.times(1))
                .findAllByPtArt(ArgumentMatchers.anyString(), ArgumentMatchers.any(Pageable.class));
    }

    @Test
    void shouldFindSerialsBetweenDates() {
        Mockito.when(serialNumberRepository.countAllBetweenDates(TestConstants.DATE_AFTER, TestConstants.DATE_BEFORE))
                .thenReturn(1L);
        Page page = Mockito.mock(Page.class);
        Mockito.when(page.getContent()).thenReturn(List.of(getBaseSerialNumberEntity()));
        Mockito.when(serialNumberRepository.findAllBetweenDates(
                        ArgumentMatchers.any(),
                        ArgumentMatchers.any(),
                        ArgumentMatchers.any()))
                .thenReturn(page);

        List<SerialNumber> foundSerials = serialNumberService
                .findBetweenDates(TestConstants.SORTING_PARAMS, TestConstants.DATE_PARAMS);

        Assertions.assertThat(foundSerials).hasSize(1);
        Mockito.verify(serialNumberRepository, Mockito.times(1))
                .countAllBetweenDates(TestConstants.DATE_AFTER, TestConstants.DATE_BEFORE);
        Mockito.verify(serialNumberRepository, Mockito.times(1))
                .findAllBetweenDates(
                        ArgumentMatchers.any(LocalDate.class),
                        ArgumentMatchers.any(LocalDate.class),
                        ArgumentMatchers.any(Pageable.class));
    }

    @Test
    void findSerialsByPtArtBetweenDates() {
        Mockito.when(serialNumberRepository.countAllByPtArtBetweenDates(
                        TestConstants.PT_ART,
                        TestConstants.DATE_AFTER,
                        TestConstants.DATE_BEFORE))
                .thenReturn(1L);
        Page page = Mockito.mock(Page.class);
        Mockito.when(page.getContent()).thenReturn(List.of(getBaseSerialNumberEntity()));
        Mockito.when(serialNumberRepository.findAllByPtArtBetweenDates(
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.any(),
                        ArgumentMatchers.any(),
                        ArgumentMatchers.any()))
                .thenReturn(page);

        List<SerialNumber> foundSerials = serialNumberService
                .findByPtArtBetweenDates(TestConstants.SORTING_PARAMS, TestConstants.PT_ART, TestConstants.DATE_PARAMS);

        Assertions.assertThat(foundSerials).hasSize(1);
        Mockito.verify(serialNumberRepository, Mockito.times(1))
                .countAllByPtArtBetweenDates(TestConstants.PT_ART, TestConstants.DATE_AFTER, TestConstants.DATE_BEFORE);
        Mockito.verify(serialNumberRepository, Mockito.times(1))
                .findAllByPtArtBetweenDates(
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.any(LocalDate.class),
                        ArgumentMatchers.any(LocalDate.class),
                        ArgumentMatchers.any(Pageable.class));
    }

    @Test
    void findSerialsByArtAndCustomerBetweenDates() {
        Mockito.when(serialNumberRepository.countAllByPtArtAnCustomerBetweenDates(
                        TestConstants.PT_ART,
                        TestConstants.CUSTOMER_ID,
                        TestConstants.DATE_AFTER,
                        TestConstants.DATE_BEFORE))
                .thenReturn(1L);
        Page page = Mockito.mock(Page.class);
        Mockito.when(page.getContent()).thenReturn(List.of(getBaseSerialNumberEntity()));
        Mockito.when(serialNumberRepository.findAllByPtArtAndCustomerBetweenDates(
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.any(),
                        ArgumentMatchers.any(),
                        ArgumentMatchers.any(),
                        ArgumentMatchers.any()))
                .thenReturn(page);

        List<SerialNumber> foundSerials = serialNumberService
                .findByArtAndCustomerBetweenDates(TestConstants.SORTING_PARAMS, TestConstants.PT_ART,
                        TestConstants.CUSTOMER_ID.toString(), TestConstants.DATE_PARAMS);

        Assertions.assertThat(foundSerials).hasSize(1);
        Mockito.verify(serialNumberRepository, Mockito.times(1))
                .countAllByPtArtAnCustomerBetweenDates(
                        TestConstants.PT_ART,
                        TestConstants.CUSTOMER_ID,
                        TestConstants.DATE_AFTER,
                        TestConstants.DATE_BEFORE);
        Mockito.verify(serialNumberRepository, Mockito.times(1))
                .findAllByPtArtAndCustomerBetweenDates(
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.any(),
                        ArgumentMatchers.any(LocalDate.class),
                        ArgumentMatchers.any(LocalDate.class),
                        ArgumentMatchers.any(Pageable.class));
    }

    @Test
    void deleteSerial() {
        serialNumberService.deleteSerial(TestConstants.SERIAL_ID.toString());

        Mockito.verify(serialNumberRepository, Mockito.times(1)).deleteById(TestConstants.SERIAL_ID);
    }

    @Test
    void shouldSaveSerials() {
        SerialsDto serialsDto = Instancio.of(SerialsDto.class)
                .set(Select.field("customerId"), TestConstants.CUSTOMER_ID.toString())
                .set(Select.field("ptArt"), TestConstants.PT_ART)
                .create();
        MockMultipartFile multipartFile = new MockMultipartFile("file", new byte[]{1, 2, 3});
        TransducerEntity transducerEntity = Instancio.of(TransducerEntity.class)
                .set(Select.field("art"), TestConstants.PT_ART)
                .create();
        CustomerEntity customerEntity = Instancio.of(CustomerEntity.class).create();
        Mockito.when(transducerRepository.findByArt(TestConstants.PT_ART)).thenReturn(Optional.of(transducerEntity));
        Mockito.when(customerRepository.findById(TestConstants.CUSTOMER_ID)).thenReturn(Optional.of(customerEntity));
        Mockito.when(xlsxParseService.parseSerials(multipartFile)).thenReturn(List.of("123", "456", "789"));

        serialNumberService.saveSerials(serialsDto, multipartFile);

        Mockito.verify(serialNumberRepository, Mockito.times(1)).saveAll(serialListArgumentCaptor.capture());
        Mockito.verify(transducerRepository, Mockito.times(1)).findByArt(TestConstants.PT_ART);
        Mockito.verify(customerRepository, Mockito.times(1)).findById(TestConstants.CUSTOMER_ID);
        Mockito.verify(xlsxParseService, Mockito.times(1)).parseSerials(multipartFile);
        List<SerialNumberEntity> captured = serialListArgumentCaptor.getValue();
        Assertions.assertThat(captured).hasSize(3);
        Assertions.assertThat(captured.get(0)).isNotNull();
        SerialNumberEntity serialNumberEntity = captured.get(0);
        Assertions.assertThat(serialNumberEntity.getCustomer()).isEqualTo(customerEntity);
        Assertions.assertThat(serialNumberEntity.getTransducer()).isEqualTo(transducerEntity);
        Assertions.assertThat(serialNumberEntity.getPtArt()).isEqualTo(transducerEntity.getArt());
    }

    @Test
    void shouldThrowExceptionIfTransducerNotFound() {
        SerialsDto serialsDto = Instancio.of(SerialsDto.class)
                .set(Select.field("customerId"), TestConstants.CUSTOMER_ID.toString())
                .set(Select.field("ptArt"), TestConstants.PT_ART)
                .create();
        MockMultipartFile multipartFile = new MockMultipartFile("file", new byte[]{1, 2, 3});
        Mockito.when(transducerRepository.findByArt(TestConstants.PT_ART)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> serialNumberService.saveSerials(serialsDto, multipartFile))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionIfCustomerNotFound() {
        SerialsDto serialsDto = Instancio.of(SerialsDto.class)
                .set(Select.field("customerId"), TestConstants.CUSTOMER_ID.toString())
                .set(Select.field("ptArt"), TestConstants.PT_ART)
                .create();
        MockMultipartFile multipartFile = new MockMultipartFile("file", new byte[]{1, 2, 3});
        Mockito.when(transducerRepository.findByArt(TestConstants.PT_ART))
                .thenReturn(Optional.of(Instancio.create(TransducerEntity.class)));
        Mockito.when(customerRepository.findById(TestConstants.CUSTOMER_ID)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> serialNumberService.saveSerials(serialsDto, multipartFile))
                .isInstanceOf(EntityNotFoundException.class);
    }

    private SerialNumberEntity getBaseSerialNumberEntity() {
        SerialNumberEntity serialNumberEntity = Instancio.of(SerialNumberEntity.class)
                .ignore(Select.field("customer"))
                .create();
        CustomerEntity customer = CustomerEntity.builder().name("name").inn("inn").build();
        serialNumberEntity.setCustomer(customer);
        return serialNumberEntity;
    }
}