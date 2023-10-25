package ru.veselov.generatebytemplate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.UUID;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.generatebytemplate.dto.GeneratePassportsDto;
import ru.veselov.generatebytemplate.service.ResultFileService;
import ru.veselov.generatebytemplate.service.PassportService;

@RestController
@RequestMapping("api/v1/generate")
@Validated
@RequiredArgsConstructor
@Slf4j
public class GeneratePassportController {

    private final PassportService passportService;

    private final ResultFileService resultFileService;

    @PostMapping(produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void createPassportsPdf(@RequestBody @Valid GeneratePassportsDto generatePassportsDto) {
        passportService.createPassportsPdf(generatePassportsDto);
    }

    @GetMapping(value = "/result/{resultId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getResult(@PathVariable @UUID String resultId) {
        ByteArrayResource pdfResult = resultFileService.getResultFile(resultId);
        HttpHeaders headers = createHeaders(pdfResult, resultId);
        return new ResponseEntity<>(pdfResult.getByteArray(), headers, HttpStatus.OK);
    }

    private HttpHeaders createHeaders(ByteArrayResource byteArrayResource, String resultId) {
        long contentLength = byteArrayResource.contentLength();
        String filename = byteArrayResource.getFilename();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        ContentDisposition attachment = ContentDisposition.formData().name("attachment")
                .filename("passports-" + filename + "-" + resultId + ".pdf").build();
        headers.setContentDisposition(attachment);
        headers.setContentLength(contentLength);
        return headers;
    }

}
