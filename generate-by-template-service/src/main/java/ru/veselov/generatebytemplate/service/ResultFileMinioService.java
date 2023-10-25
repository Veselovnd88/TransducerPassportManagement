package ru.veselov.generatebytemplate.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import ru.veselov.generatebytemplate.model.ResultFile;

public interface ResultFileMinioService {

    void saveResult(Resource resource, ResultFile resultFile);

    ByteArrayResource loadResultFile(ResultFile resultFile);

}
