package ru.veselov.generatebytemplate.service;

import java.util.UUID;

public interface ResultFileStorageService {

    ResultFileEntity saveUnSynced(ResultFile resultFile);

    void syncResultFile(UUID resultFileId);

    ResultFile findById(String resultFileId);

    void delete(String resultFileId);

}
