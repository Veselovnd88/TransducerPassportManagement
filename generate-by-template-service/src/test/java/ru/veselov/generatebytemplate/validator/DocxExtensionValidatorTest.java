package ru.veselov.generatebytemplate.validator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import ru.veselov.generatebytemplate.TestUtils;
import ru.veselov.generatebytemplate.extension.DocxExtensionValidatorParamResolver;

import java.util.stream.Stream;

@ExtendWith(DocxExtensionValidatorParamResolver.class)
//needed for DI to JUnit classes, here just for test
class DocxExtensionValidatorTest {

    private static final String FILE = "file";

    DocxExtensionValidator docxExtensionValidator;

    @BeforeEach
    void init(DocxExtensionValidator docxExtensionValidator) {
        this.docxExtensionValidator = docxExtensionValidator;
    }

    @ParameterizedTest
    @ValueSource(strings = {".docx", ".DOCX", ".DoCx"})
    void shouldReturnTrueIfCorrectExtension(String extension) {
        MockMultipartFile multipartFile = new MockMultipartFile(
                TestUtils.MULTIPART_FILE,
                FILE + extension, MediaType.MULTIPART_FORM_DATA_VALUE, TestUtils.SOURCE_BYTES
        );
        boolean isValid = docxExtensionValidator.isValid(multipartFile, null);
        Assertions.assertThat(isValid).isTrue();
    }

    @Test
    void shouldReturnFalseIfNoFilename() {
        MockMultipartFile multipartFile = new MockMultipartFile(
                TestUtils.MULTIPART_FILE, null, MediaType.MULTIPART_FORM_DATA_VALUE, TestUtils.SOURCE_BYTES
        );
        boolean isValid = docxExtensionValidator.isValid(multipartFile, null);
        Assertions.assertThat(isValid).isFalse();
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForFileExtensions")
    void shouldReturnFalseIfWrongExtension(String extension) {
        MockMultipartFile multipartFile = new MockMultipartFile(
                TestUtils.MULTIPART_FILE,
                FILE + extension, MediaType.MULTIPART_FORM_DATA_VALUE, TestUtils.SOURCE_BYTES
        );
        boolean isValid = docxExtensionValidator.isValid(multipartFile, null);
        Assertions.assertThat(isValid).isFalse();
    }

    public static Stream<Arguments> getArgumentsForFileExtensions() {
        return Stream.of(
                Arguments.of("docx"),
                Arguments.of(".doc"),
                Arguments.of("."));
    }

}
