package ru.veselov.miniotemplateservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.veselov.miniotemplateservice.dto.TemplateDto;
import ru.veselov.miniotemplateservice.service.PassportTemplateService;
import ru.veselov.miniotemplateservice.service.TemplateMinioService;

@RestController
@RequestMapping("api/v1/template")
@Validated
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateMinioService templateMinioService;

    private final PassportTemplateService passportTemplateService;

    @GetMapping(value = "/{templateId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getTemplate(@PathVariable("templateId") String templateId) {
        ByteArrayResource templateByName = templateMinioService.getTemplateByName(templateId + ".docx");//FIXME
        return new ResponseEntity<>(templateByName.getByteArray(), HttpStatus.OK);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadTemplate(@RequestPart("template-info") @Valid TemplateDto templateInfo,
                                               @RequestPart("file") MultipartFile file) {
        passportTemplateService.saveTemplate(file, templateInfo);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}
