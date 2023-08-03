package ru.veselov.passportprocessing.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.passportprocessing.service.PassportService;
import ru.veselov.passportprocessing.service.impl.PassportServiceImpl;

@RestController
@RequestMapping("api/v1/passport")
@RequiredArgsConstructor
@Slf4j
public class PassportController {

    private final PassportServiceImpl passportService;


    @PostMapping
    public void getPassportsPdf() {
        passportService.createPassportsPdf();
    }
}
