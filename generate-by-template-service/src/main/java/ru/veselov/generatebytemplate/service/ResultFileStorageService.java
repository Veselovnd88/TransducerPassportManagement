package ru.veselov.generatebytemplate.service;

import ru.veselov.generatebytemplate.entity.ResultFileEntity;
import ru.veselov.generatebytemplate.model.ResultFile;

import java.util.UUID;

public interface ResultFileStorageService {

    ResultFileEntity saveUnSynced(ResultFile resultFile);

    ResultFile syncResultFile(UUID resultFileId);

    ResultFile findById(String resultFileId);

}
