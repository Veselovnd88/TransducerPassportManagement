package ru.veselov.authservice.security.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

public class JwtToUserConverter implements Converter<Jwt, JwtToken> {

}
