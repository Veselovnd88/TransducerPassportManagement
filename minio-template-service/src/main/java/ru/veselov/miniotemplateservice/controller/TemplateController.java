package ru.veselov.miniotemplateservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.miniotemplateservice.service.TemplateService;

@RestController
@RequestMapping("api/v1/template")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    @GetMapping(value = "/{templateId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getTemplate(@PathVariable("templateId") String templateId) {
        ByteArrayResource templateByName = templateService.getTemplateByName(templateId+".docx");
        return new ResponseEntity<>(templateByName.getByteArray(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> uploadTemplate() {
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}
