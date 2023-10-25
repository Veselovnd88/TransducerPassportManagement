package ru.veselov.generatebytemplate.service;

import org.springframework.core.io.ByteArrayResource;
import ru.veselov.generatebytemplate.model.ResultFile;

public interface ResultFileService {

    ResultFile save(ByteArrayResource pdfBytes, ResultFile resultFile);

    ByteArrayResource getResultFile(String resultFileId);

}
