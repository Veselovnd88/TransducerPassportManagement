package ru.veselov.passportprocessing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.springframework.stereotype.Service;
import ru.veselov.passportprocessing.service.PassportGeneratorService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassportGeneratorServiceImpl implements PassportGeneratorService {
    @Override
    public byte[] generatePassports(List<String> serials, String templateId, String date) {
        log.info("Starting generate [{} passports] from [template {}] on [date {}]", serials.size(), templateId, date);
        Path file = Path.of(templateId);
        try (XWPFDocument mainDoc = new XWPFDocument(Files.newInputStream(file))) {
            //First time we load doc as template and change fields here for saving first page
            List<XWPFParagraph> pageOneParagraphs = getParagraphs(mainDoc);
            //Every new page we load template, then replace placeholders and add to mainDoc
            XWPFDocument docToEdit = null;
            for (int i = 0; i < serials.size(); i++) {
                if (i < 2) {//first 2 serials added to first template page
                    replacePlaceholders(serials, date, pageOneParagraphs, i);
                } else {
                    if (i % 2 == 0) {//creating new template every 3rd serial
                        docToEdit = new XWPFDocument(Files.newInputStream(file));
                    }
                    //Replacing placeholders
                    List<XWPFParagraph> paragraphsToEdit = getParagraphs(docToEdit);
                    replacePlaceholders(serials, date, paragraphsToEdit, i);
                    appendGeneratedPage(serials, mainDoc, docToEdit, i);
                }
            }
            //Create baos for writing a doc, and then return ByteArray for sending as MultiPart file
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mainDoc.write(baos);
            return baos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void appendGeneratedPage(List<String> serials, XWPFDocument mainDoc, XWPFDocument docToEdit, int i) {
        if (i % 2 != 0 || (i == serials.size() - 1)) {
            CTBody mainBody = mainDoc.getDocument().getBody();
            if (docToEdit == null) {
                throw new RuntimeException();
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

    private void replacePlaceholders(List<String> serials, String date, List<XWPFParagraph> paragraphs, int i) {
        log.debug("Replacing placeholder for [serial {}]", serials.get(i));
        for (XWPFParagraph paragraph : paragraphs) {
            List<XWPFRun> runs = paragraph.getRuns();
            replacePlaceholdersInRuns(serials, date, i, runs);
        }
    }

    private void replacePlaceholdersInRuns(List<String> serials, String date, int i, List<XWPFRun> runs) {
        for (XWPFRun run : runs) {
            if (run != null) {
                String text = run.getText(0);
                if (i % 2 == 0 && text != null && text.contains("NUMBERUP")) {
                    replacePlaceHolder(run, "NUMBERUP", serials.get(i));
                }
                if (i % 2 != 0 && text != null && text.contains("NUMBERDOWN")) {
                    replacePlaceHolder(run, "NUMBERDOWN", serials.get(i));
                }
                if (text != null && text.contains("DATE")) {
                    replacePlaceHolder(run, "DATE", date);
                }
            }
        }
    }

    private void replacePlaceHolder(XWPFRun run, String placeHolder, String text) {
        text = text.replace(placeHolder, text);
        run.setText(text, 0);
    }

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
            e.printStackTrace();
            throw new RuntimeException("", e); //FIXME
        }
        src.set(makeBody);
    }
}
