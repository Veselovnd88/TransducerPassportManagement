package ru.veselov.miniotemplateservice.validator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

class DocxExtensionValidatorTest {

    private static final byte[] BYTES = new byte[]{1, 2, 3};

    DocxExtensionValidator docxExtensionValidator;


    @BeforeEach
    void init() {
        docxExtensionValidator = new DocxExtensionValidator();


    }

    @ParameterizedTest
    @ValueSource(strings = {".docx", ".DOCX", ".DoCx"})
    void shouldReturnTrue(String extension) {
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file", "file" + extension, MediaType.MULTIPART_FORM_DATA_VALUE, BYTES
        );
        boolean isValid = docxExtensionValidator.isValid(multipartFile, null);
        Assertions.assertThat(isValid).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"docx", ".doc", "."})
    void shouldReturnFalse(String extension) {
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file", "file" + extension, MediaType.MULTIPART_FORM_DATA_VALUE, BYTES
        );
        boolean isValid = docxExtensionValidator.isValid(multipartFile, null);
        Assertions.assertThat(isValid).isFalse();
    }

    @Test
    void shouldReturnFalseIfNoFilename() {
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file", null, MediaType.MULTIPART_FORM_DATA_VALUE, BYTES
        );
        boolean isValid = docxExtensionValidator.isValid(multipartFile, null);
        Assertions.assertThat(isValid).isFalse();
    }

}
