package ru.veselov.generatebytemplate.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import ru.veselov.generatebytemplate.model.GeneratedResultFile;

public interface GeneratedResultFileMinioService {

    String saveResult(Resource resource, GeneratedResultFile resultFile);

    ByteArrayResource loadResultFile(GeneratedResultFile resultFile);

}
