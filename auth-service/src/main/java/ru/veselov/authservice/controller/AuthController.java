package ru.veselov.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.authservice.dto.SignUpDto;
import ru.veselov.authservice.dto.TokenDto;
import ru.veselov.authservice.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public TokenDto register(@RequestBody SignUpDto signUpDto) {
        return authService.register(signUpDto);
    }

}
