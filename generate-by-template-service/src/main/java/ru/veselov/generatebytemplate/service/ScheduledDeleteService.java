package ru.veselov.generatebytemplate.service;

public interface ScheduledDeleteService {

    void deleteUnSynchronizedTemplates();

    void deleteUnSynchronizedGenerateResultFiles();

    void deleteGeneratedResultFilesWithNullTemplates();

}
