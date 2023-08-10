package ru.veselov.passportprocessing.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import ru.veselov.passportprocessing.service.TemplateStorageHttpClient;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class PassportTemplateServiceImplTest {

    private final static String TEMPLATE_ID = UUID.randomUUID().toString();

    @Mock
    TemplateStorageHttpClient storageHttpClient;

    @InjectMocks
    PassportTemplateServiceImpl passportTemplateService;

    @Test
    void shouldCallStorageHttpClient() {
        Mockito.when(storageHttpClient.sendRequestToGetTemplate(ArgumentMatchers.anyString()))
                .thenReturn(new ByteArrayResource(new byte[]{1, 2, 3, 4}));

        passportTemplateService.getTemplate(TEMPLATE_ID);

        Mockito.verify(storageHttpClient, Mockito.times(1)).sendRequestToGetTemplate(TEMPLATE_ID);
    }

}
