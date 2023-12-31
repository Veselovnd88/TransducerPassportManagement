package ru.veselov.generatebytemplate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.veselov.generatebytemplate.annotation.Docx;
import ru.veselov.generatebytemplate.dto.TemplateDto;
import ru.veselov.generatebytemplate.service.PassportTemplateService;

@RestController
@RequestMapping("api/v1/template")
@Validated
@RequiredArgsConstructor
public class TemplateController {

    private final PassportTemplateService passportTemplateService;

    @GetMapping(value = "/source/id/{templateId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
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

    @PutMapping(value = "/update/upload/id/{templateId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadUpdateTemplate(@PathVariable("templateId") @UUID String templateId,
                                                     @RequestPart("file") @Docx MultipartFile file) {
        passportTemplateService.updateTemplate(file, templateId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/delete/id/{templateId}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable("templateId") @UUID String templateId) {
        passportTemplateService.deleteTemplate(templateId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
