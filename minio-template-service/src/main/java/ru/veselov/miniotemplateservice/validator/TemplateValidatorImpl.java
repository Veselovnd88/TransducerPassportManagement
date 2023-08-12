package ru.veselov.miniotemplateservice.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.miniotemplateservice.repository.TemplateRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateValidatorImpl implements TemplateValidator {

    private final TemplateRepository templateRepository;

    @Override
    public void validateFilename(String filename) {



    }
}
