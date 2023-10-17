package ru.veselov.generatebytemplate.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.generatebytemplate.mapper.GeneratedResultFileMapper;
import ru.veselov.generatebytemplate.mapper.GeneratedResultFileMapperImpl;
import ru.veselov.generatebytemplate.repository.GeneratedResultFileRepository;
import ru.veselov.generatebytemplate.repository.TemplateRepository;

@ExtendWith(MockitoExtension.class)
class GeneratedResultFileStorageServiceImplTest {

    @Mock
    GeneratedResultFileRepository generatedResultFileRepository;

    @Mock
    TemplateRepository templateRepository;

    @InjectMocks
    GeneratedResultFileStorageServiceImpl generatedResultFileStorageService;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(generatedResultFileStorageService,
                "generatedResultFileMapper", new GeneratedResultFileMapperImpl(), GeneratedResultFileMapper.class);
    }

    @Test
    void shouldSaveUnSyncedResultFile() {

    }
}