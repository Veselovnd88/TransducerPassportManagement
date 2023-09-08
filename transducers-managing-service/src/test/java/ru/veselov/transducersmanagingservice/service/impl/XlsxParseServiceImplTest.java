package ru.veselov.transducersmanagingservice.service.impl;

import lombok.SneakyThrows;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import ru.veselov.transducersmanagingservice.service.XlsxParseService;

import java.io.InputStream;
import java.util.List;

class XlsxParseServiceImplTest {

    XlsxParseService xlsxParseService = new XlsxParseServiceImpl();

    @Test
    @SneakyThrows
    void shouldReturnListOfSerials() {
        InputStream xlsxInputStream = getClass().getClassLoader().getResourceAsStream("test1.xlsx");
        MockMultipartFile multipartFile = new MockMultipartFile("file", xlsxInputStream);

        List<String> serials = xlsxParseService.parseSerials(multipartFile);

        Assertions.assertThat(serials).hasSize(4).contains("12F3", "124", "125", "126");
        assert xlsxInputStream != null;
        xlsxInputStream.close();
    }

    @Test
    void shouldThrowXlsxParseException() {
        MockMultipartFile multipartFile = new MockMultipartFile("file", new byte[]{1,2,3});

        Assertions.assertThatThrownBy(()-> xlsxParseService.parseSerials(multipartFile))
                .isInstanceOf(NotOfficeXmlFileException.class);
    }

}
