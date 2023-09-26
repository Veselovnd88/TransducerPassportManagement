package ru.veselov.gateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
public class GrantedAuthoritiesExtractor implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        log.debug("Extracting authorities from Jwt");
        Collection<?> authorities = (Collection<?>)
                jwt.getClaims().getOrDefault("authorities", Collections.emptyList());
        return authorities.stream()
                .map(Object::toString)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
