package ru.veselov.authservice.service;

import ru.veselov.authservice.dto.LoginDto;
import ru.veselov.authservice.dto.RefreshTokenDto;
import ru.veselov.authservice.dto.SignUpDto;
import ru.veselov.authservice.dto.TokenDto;

public interface AuthService {

    TokenDto register(SignUpDto signUpDto);

    TokenDto login(LoginDto loginDto);

    TokenDto refreshToken(RefreshTokenDto refreshTokenDto);
}
