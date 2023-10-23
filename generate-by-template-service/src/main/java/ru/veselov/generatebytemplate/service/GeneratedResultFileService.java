package ru.veselov.generatebytemplate.service;

import org.springframework.core.io.ByteArrayResource;
import ru.veselov.generatebytemplate.model.GeneratedResultFile;

public interface GeneratedResultFileService {

    GeneratedResultFile save(ByteArrayResource pdfBytes, GeneratedResultFile generatedResultFile);

    ByteArrayResource getResultFile(String resultFileId);

}
