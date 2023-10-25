package ru.veselov.generatebytemplate.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.generatebytemplate.TestUtils;
import ru.veselov.generatebytemplate.dto.GeneratePassportsDto;
import ru.veselov.generatebytemplate.service.DocxGeneratorService;
import ru.veselov.generatebytemplate.service.PassportTemplateService;

@ExtendWith(MockitoExtension.class)
class DocxPassportServiceImplTest {

    public static final String DATE_FORMAT = "dd-MM-yyyy";

    public static ByteArrayResource byteArrayResource = new ByteArrayResource(TestUtils.SOURCE_BYTES);

    @Mock
    DocxGeneratorService docxGeneratorService;

    @Mock
    PassportTemplateService passportTemplateService;

    @InjectMocks
    DocxPassportServiceImpl docxPassportService;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(docxPassportService, "dateFormat", DATE_FORMAT, String.class);
    }

    @Test
    void shouldCreateByteArrayResourceFromTemplate() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();
        Mockito.when(passportTemplateService.getTemplate(generatePassportsDto.getTemplateId())).thenReturn(byteArrayResource);
        Mockito.when(docxGeneratorService.generateDocx(TestUtils.SERIALS,
                        byteArrayResource,
                        TestUtils.DTF.format(TestUtils.DATE)))
                .thenReturn(TestUtils.SOURCE_BYTES);

        docxPassportService.createDocxPassports(generatePassportsDto);

        Mockito.verify(passportTemplateService, Mockito.times(1)).getTemplate(generatePassportsDto.getTemplateId());
        Mockito.verify(docxGeneratorService, Mockito.times(1)).generateDocx(
                TestUtils.SERIALS, byteArrayResource, TestUtils.DTF.format(TestUtils.DATE));
    }

}
