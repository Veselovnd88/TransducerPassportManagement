package ru.veselov.transducersmanagingservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface XlsxParseService {

    List<String> parseSerials(MultipartFile multipartFile);

}
