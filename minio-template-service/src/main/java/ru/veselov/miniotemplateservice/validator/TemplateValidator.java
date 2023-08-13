package ru.veselov.miniotemplateservice.validator;

public interface TemplateValidator {

    void validateTemplateName(String templateName);

    void validateFileExtension(String filename);

}
