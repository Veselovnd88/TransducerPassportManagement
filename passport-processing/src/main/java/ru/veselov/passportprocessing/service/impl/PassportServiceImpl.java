package ru.veselov.passportprocessing.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.veselov.passportprocessing.service.PassportService;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


@Service
@Slf4j
public class PassportServiceImpl implements PassportService {

    private final WebClient webClient = WebClient.create();

    private List<String> serials;

    public void createPassportsPdf() {
        serials = List.of("one", "two", "three", "four", "five");
        String path =
                "C:\\Users\\VeselovND\\git\\PTPassportProject\\document-processing\\document-processing\\src\\main\\resources\\file.docx";
        //  "/home/nikolay/git/PTPassportProject/document-processing/document-processing/src/main/resources/file.docx";
        Path file = Path.of(path);
        boolean exists = file.toFile().exists();
        String absolutePath = file.toString();
        log.info("exists {}, abs {}", exists, absolutePath);


        OutputStream os = null;
        try {
            os = new FileOutputStream("test.docx");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


        try (
                XWPFDocument doc = new XWPFDocument(Files.newInputStream(file))) {
            XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
            List<XWPFParagraph> paragraphs = extractor.getDocument().getParagraphs();
            for (int i = 0; i < serials.size(); i++) {
                for (XWPFParagraph paragraph : paragraphs) {
                    List<XWPFRun> runs = paragraph.getRuns();
                    for (XWPFRun run : runs) {
                        if (run != null) {
                            String text = run.getText(0);
                            if (text != null && text.contains("NUMBERUP")) {
                                text = text.replace("NUMBERUP", serials.get(i));
                                run.setText(text, 0);
                            }
                        }
                    }
                }
            }

            CTBody body = doc.getDocument().getBody();
            appendBody(body,body);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            doc.write(byteArrayOutputStream);
            os.flush();
            os.close();


            //  byte[] bytes = Files.readAllBytes(file);

            createPdf(byteArrayOutputStream.toByteArray());

        } catch (XmlException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void createPdf(byte[] bytes) throws IOException {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", bytes).header("Content-Disposition",
                "form-data; name=file").filename("file.docx");
        Mono<DataBuffer> dataBufferMono = webClient.post().uri("http://localhost:3000/forms/libreoffice/convert")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                .retrieve().bodyToMono(DataBuffer.class);

        //convert receivedByteArrayToPdfFile
        DataBuffer block = dataBufferMono.block();
        InputStream inputStream = block.asInputStream();
        OutputStream os = new FileOutputStream("sample.pdf");
        IOUtils.copy(inputStream, os);
        os.close();
    }

    private static void appendBody(CTBody src, CTBody append) throws XmlException {
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
            throw new XmlException("", e);
        }
        src.set(makeBody);
    }
}
