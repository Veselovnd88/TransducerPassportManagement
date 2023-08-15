package ru.veselov.miniotemplateservice.controller;

import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.miniotemplateservice.model.Template;
import ru.veselov.miniotemplateservice.service.TemplateStorageService;

@RestController
@RequestMapping("/api/v1/template/info")
@Validated
@RequiredArgsConstructor
public class TemplateInfoController {

    private final TemplateStorageService templateStorageService;

    @GetMapping("/{templateId}")
    public ResponseEntity<Template> getTemplateInfo(@PathVariable("templateId") @UUID String templateId) {
        Template foundTemplate = templateStorageService.findTemplateById(templateId);
        return new ResponseEntity<>(foundTemplate, HttpStatus.OK);
    }


}
