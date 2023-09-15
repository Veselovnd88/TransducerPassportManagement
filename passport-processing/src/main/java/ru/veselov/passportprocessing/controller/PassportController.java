package ru.veselov.passportprocessing.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.passportprocessing.dto.GeneratePassportsDto;
import ru.veselov.passportprocessing.service.PassportService;

import java.time.Instant;

@RestController
@RequestMapping("api/v1/passport")
@Validated
@RequiredArgsConstructor
@Slf4j
public class PassportController {

    private final PassportService passportService;



    @PostMapping(value = "/generate", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getPassportsPdf(@RequestBody @Valid GeneratePassportsDto generatePassportsDto) {
        byte[] pdfBytes = passportService.createPassportsPdf(generatePassportsDto);
        HttpHeaders headers = createHeaders(generatePassportsDto, pdfBytes.length);
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    private HttpHeaders createHeaders(GeneratePassportsDto generatePassportsDto, int contentLength) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "passports-" + generatePassportsDto.getTemplateId()
                + "-" + Instant.now().toEpochMilli() + ".pdf");
        headers.setContentLength(contentLength);
        return headers;
    }

}
