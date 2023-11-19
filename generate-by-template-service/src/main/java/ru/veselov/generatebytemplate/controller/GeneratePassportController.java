package ru.veselov.generatebytemplate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.UUID;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.veselov.generatebytemplate.dto.GeneratePassportsDto;
import ru.veselov.generatebytemplate.service.PassportService;
import ru.veselov.generatebytemplate.service.ResultFileService;
import ru.veselov.generatebytemplate.utils.AppConstants;

@RestController
@RequestMapping("api/v1/generate")
@Validated
@RequiredArgsConstructor
@Slf4j
public class GeneratePassportController {

    private final PassportService passportService;

    private final ResultFileService resultFileService;

    @PostMapping("/{taskId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void createPassportsPdf(@RequestHeader(value = AppConstants.SERVICE_USERNAME_HEADER)
                                   @NotEmpty String username,
                                   @RequestBody @Valid GeneratePassportsDto generatePassportsDto,
                                   @PathVariable @UUID String taskId) {
        passportService.createPassportsPdf(generatePassportsDto, taskId, username);
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
