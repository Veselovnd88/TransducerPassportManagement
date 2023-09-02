package ru.veselov.transducersmanagingservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.veselov.transducersmanagingservice.service.XlsxParseService;

import java.util.List;

@Service
@Slf4j
public class XlsxParseServiceImpl implements XlsxParseService {
    @Override
    public List<String> parseSerials(MultipartFile multipartFile) {
        return null;
    }
}
