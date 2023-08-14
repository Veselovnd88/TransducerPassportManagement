package ru.veselov.miniotemplateservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
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
import ru.veselov.miniotemplateservice.annotation.Docx;
import ru.veselov.miniotemplateservice.dto.TemplateDto;
import ru.veselov.miniotemplateservice.service.PassportTemplateService;

@RestController
@RequestMapping("api/v1/template")
@Validated
@RequiredArgsConstructor
public class TemplateController {
    //TODO TEST ALL
    private final PassportTemplateService passportTemplateService;

    @GetMapping(value = "/source/{templateId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getTemplateSource(@PathVariable("templateId") @UUID String templateId) {
        ByteArrayResource template = passportTemplateService.getTemplate(templateId);
        return new ResponseEntity<>(template.getByteArray(), HttpStatus.OK);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadTemplate(@RequestPart("template-info") @Valid TemplateDto templateInfo,
                                               @RequestPart("file") @Docx MultipartFile file) {
        passportTemplateService.saveTemplate(file, templateInfo);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}
