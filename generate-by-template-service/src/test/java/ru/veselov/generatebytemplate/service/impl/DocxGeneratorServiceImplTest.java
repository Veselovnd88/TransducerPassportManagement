package ru.veselov.generatebytemplate.service.impl;

import lombok.SneakyThrows;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import ru.veselov.generatebytemplate.exception.DocxProcessingException;
import ru.veselov.generatebytemplate.service.PlaceholderProperties;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

class DocxGeneratorServiceImplTest {

    private static final String NUMBERUP = "NUMBERUP";

    private static final String NUMBERDOWN = "NUMBERDOWN";

    private static final String DATE = "DATE";

    private static final String RESOURCE_FILE_DOCX = "file.docx";

    DocxGeneratorServiceImpl passportGeneratorService;

    private static final List<String> SERIALS = List.of("one", "two", "three", "four", "five", "six");

    @BeforeEach
    void init() {
        PlaceholderProperties placeholderProperties = new PlaceholderProperties();
        placeholderProperties.setUpperSerial(NUMBERUP);
        placeholderProperties.setBottomSerial(NUMBERDOWN);
        placeholderProperties.setDate(DATE);
        passportGeneratorService = new DocxGeneratorServiceImpl(placeholderProperties);
    }

    @Test
    @SneakyThrows
    void shouldGenerateByteArrayWithReplacingOfPlaceholders() {
        //given
        InputStream templateInputStream = getClass().getClassLoader().getResourceAsStream(RESOURCE_FILE_DOCX);
        assert templateInputStream != null;
        ByteArrayResource templateByteArrayResource = new ByteArrayResource(templateInputStream.readAllBytes());
        templateInputStream.close();

        //when
        byte[] bytes = passportGeneratorService.generateDocx(SERIALS, templateByteArrayResource, LocalDate.now().toString());

        //then
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        XWPFDocument generatedDoc = new XWPFDocument(bais);
        List<XWPFParagraph> generatedParagraphs = getParagraphs(generatedDoc);
        int placeholdersCount = countPlaceholdersInDoc(generatedParagraphs);
        Assertions.assertThat(placeholdersCount).isZero(); //all placeholders replaced
        InputStream templateIS = getClass().getClassLoader().getResourceAsStream(RESOURCE_FILE_DOCX);
        assert templateIS != null;
        XWPFDocument sourceDoc = new XWPFDocument(templateIS);
        List<XWPFParagraph> sourceParagraphs = getParagraphs(sourceDoc);
        int placeholdersCountSource = countPlaceholdersInDoc(sourceParagraphs);
        Assertions.assertThat(placeholdersCountSource).isEqualTo(4);
        generatedDoc.close();
        sourceDoc.close();
        bais.close();
    }

    @Test
    void shouldThrowDocxProcessingException() {
        ByteArrayResource templateByteArrayResource = new ByteArrayResource(new byte[]{1, 2, 3, 4, 5});
        Assertions.assertThatThrownBy(() -> passportGeneratorService
                .generateDocx(SERIALS, templateByteArrayResource, "15.01.2023")
        ).isInstanceOf(DocxProcessingException.class);
    }

    private static int countPlaceholdersInDoc(List<XWPFParagraph> paragraphs) {
        int countPlaceholders = 0;
        for (XWPFParagraph paragraph : paragraphs) {
            List<XWPFRun> runs = paragraph.getRuns();
            for (XWPFRun run : runs) {
                if (run != null) {
                    String text = run.getText(0);
                    if (text != null) {
                        if (text.contains(NUMBERUP) ||
                                text.contains(NUMBERDOWN) ||
                                text.contains(DATE)) {
                            countPlaceholders++;
                        }
                    }
                }
            }
        }
        return countPlaceholders;
    }

    private List<XWPFParagraph> getParagraphs(XWPFDocument mainDoc) {
        XWPFWordExtractor extractor = new XWPFWordExtractor(mainDoc);
        return extractor.getDocument().getParagraphs();
    }

}
