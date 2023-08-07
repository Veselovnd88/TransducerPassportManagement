package ru.veselov.passportprocessing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlRuntimeException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.springframework.stereotype.Service;
import ru.veselov.passportprocessing.exception.DocxProcessingException;
import ru.veselov.passportprocessing.service.PassportGeneratorService;
import ru.veselov.passportprocessing.service.PlaceholderProperties;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassportGeneratorServiceImpl implements PassportGeneratorService {

    private final PlaceholderProperties placeholderProperties;

    @Override
    public byte[] generatePassports(List<String> serials, InputStream templateInputStream, String date) {
        log.info("Starting generate [{} passports] on [date {}]", serials.size(), date);
        ByteArrayOutputStream templateOutputStream = getByteArrayOutputStream(templateInputStream);
        try (XWPFDocument mainDoc = new XWPFDocument(new ByteArrayInputStream(templateOutputStream.toByteArray()))) {
            //First time we load doc as template and change fields here for saving first page.
            List<XWPFParagraph> pageOneParagraphs = getParagraphs(mainDoc);
            //Every new page we load template, then replace placeholders and add to mainDoc.
            XWPFDocument docToEdit = null;
            for (int i = 0; i < serials.size(); i++) {
                if (i < 2) {//first 2 serials added to first template page.
                    replacePlaceholders(serials, date, pageOneParagraphs, i);
                } else {
                    if (i % 2 == 0) {//creating new template every 3rd serial.
                        docToEdit = new XWPFDocument(new ByteArrayInputStream(templateOutputStream.toByteArray()));
                    }
                    //Replacing placeholders
                    List<XWPFParagraph> paragraphsToEdit = getParagraphs(docToEdit);
                    replacePlaceholders(serials, date, paragraphsToEdit, i);
                    appendGeneratedPage(serials, mainDoc, docToEdit, i);
                }
            }
            //Create baos for writing a doc, and then return ByteArray for sending as MultiPart file.
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mainDoc.write(baos);
            log.info("Byte array of source .docx document successfully created");
            return baos.toByteArray();
        } catch (IOException | NotOfficeXmlFileException e) {
            log.error("Error occurred during opening processing input and output streams");
            throw new DocxProcessingException(e.getMessage(), e);
        }
    }

    //Get baos for further creating InputStream for independent processing.
    private ByteArrayOutputStream getByteArrayOutputStream(InputStream templateInputStream) {
        ByteArrayOutputStream templateOutputStream = new ByteArrayOutputStream();
        try {
            templateInputStream.transferTo(templateOutputStream);
        } catch (IOException e) {
            throw new DocxProcessingException(e.getMessage(), e);
        }
        return templateOutputStream;
    }

    private void appendGeneratedPage(List<String> serials, XWPFDocument mainDoc, XWPFDocument docToEdit, int i) {
        if (i % 2 != 0 || (i == serials.size() - 1)) {
            CTBody mainBody = mainDoc.getDocument().getBody();
            if (docToEdit == null) {
                throw new DocxProcessingException("Document to edit wasn't created for generating passport");
            }
            CTBody bodyToAdd = docToEdit.getDocument().getBody();
            appendBody(mainBody, bodyToAdd);
            log.debug("New page added to passports");
        }
    }

    private List<XWPFParagraph> getParagraphs(XWPFDocument mainDoc) {
        XWPFWordExtractor extractor = new XWPFWordExtractor(mainDoc);
        return extractor.getDocument().getParagraphs();
    }

    private void replacePlaceholders(List<String> serials, String date, List<XWPFParagraph> paragraphs, int number) {
        log.debug("Replacing placeholder for [serial {}]", serials.get(number));
        for (XWPFParagraph paragraph : paragraphs) {
            List<XWPFRun> runs = paragraph.getRuns();
            replacePlaceholdersInRuns(serials, date, number, runs);
        }
    }

    private void replacePlaceholdersInRuns(List<String> serials, String date, int number, List<XWPFRun> runs) {
        for (XWPFRun run : runs) {
            if (run != null) {
                String text = run.getText(0);
                if (number % 2 == 0 && text != null && text.contains(placeholderProperties.getUpperSerial())) {
                    replacePlaceHolder(run, placeholderProperties.getUpperSerial(), serials.get(number));
                }
                if (number % 2 != 0 && text != null && text.contains(placeholderProperties.getBottomSerial())) {
                    replacePlaceHolder(run, placeholderProperties.getBottomSerial(), serials.get(number));
                }
                if (text != null && text.contains(placeholderProperties.getDate())) {
                    replacePlaceHolder(run, placeholderProperties.getDate(), date);
                }
            }
        }
    }

    private void replacePlaceHolder(XWPFRun run, String placeHolder, String text) {
        String replacedText = text.replace(placeHolder, text);
        run.setText(replacedText, 0);
    }

    //Method added body of second doc in the end of first
    private void appendBody(CTBody src, CTBody append) {
        XmlOptions optionsOuter = new XmlOptions();
        optionsOuter.setSaveOuter();
        String appendString = append.xmlText(optionsOuter);
        String srcString = src.xmlText();
        String prefix = srcString.substring(0, srcString.indexOf(">") + 1);
        String mainPart = srcString.substring(srcString.indexOf(">") + 1, srcString.lastIndexOf("<"));
        String suffix = srcString.substring(srcString.lastIndexOf("<"));
        String addPart = appendString.substring(appendString.indexOf(">") + 1, appendString.lastIndexOf("<"));
        CTBody makeBody;
        try {
            makeBody = CTBody.Factory.parse(prefix + mainPart + addPart + suffix);
        } catch (XmlException e) {
            log.error("Error occurred during appending new doc to existing:[{}]", e.getMessage());
            throw new XmlRuntimeException(
                    "Error occurred during appending new doc to existing:[%s]".formatted(e.getMessage()));
        }
        src.set(makeBody);
    }

}
