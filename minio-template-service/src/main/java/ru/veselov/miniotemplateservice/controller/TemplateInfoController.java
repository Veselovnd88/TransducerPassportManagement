package ru.veselov.miniotemplateservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.miniotemplateservice.annotation.SortingParam;
import ru.veselov.miniotemplateservice.dto.SortingParams;
import ru.veselov.miniotemplateservice.model.Template;
import ru.veselov.miniotemplateservice.service.TemplateStorageService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/template/info")
@Validated
@RequiredArgsConstructor
public class TemplateInfoController {

    private final TemplateStorageService templateStorageService;

    @GetMapping("/id/{templateId}")
    public ResponseEntity<Template> getTemplateInfo(@PathVariable("templateId") @UUID String templateId) {
        Template foundTemplate = templateStorageService.findTemplateById(templateId);
        return new ResponseEntity<>(foundTemplate, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Template>> getTemplates(@Valid @SortingParam SortingParams sortingParams) {
        List<Template> templates = templateStorageService.findAll(sortingParams);
        return new ResponseEntity<>(templates, HttpStatus.OK);
    }

    @GetMapping("/all/ptArt/{ptArt}")
    public ResponseEntity<List<Template>> getTemplatesByPtArt(@PathVariable("ptArt") String ptArt,
                                                              @Valid @SortingParam SortingParams sortingParams) {
        List<Template> templates = templateStorageService.findAllByPtArt(ptArt, sortingParams);
        return new ResponseEntity<>(templates, HttpStatus.OK);
    }

}
