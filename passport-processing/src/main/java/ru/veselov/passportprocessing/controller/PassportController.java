package ru.veselov.passportprocessing.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/passport")
@RequiredArgsConstructor
@Slf4j
public class PassportController {


    @PostMapping
    public void getPassportsPdf() {

    }
}
