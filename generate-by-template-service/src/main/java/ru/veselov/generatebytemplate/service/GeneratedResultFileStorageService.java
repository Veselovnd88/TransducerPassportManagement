package ru.veselov.generatebytemplate.service;

import ru.veselov.generatebytemplate.entity.GeneratedResultFileEntity;
import ru.veselov.generatebytemplate.model.GeneratedResultFile;

import java.util.UUID;

public interface GeneratedResultFileStorageService {

    GeneratedResultFileEntity saveUnSynced(GeneratedResultFile resultFile);

    GeneratedResultFile syncResultFile(UUID resultFileId);

    GeneratedResultFile findById(String resultFileId);

}
