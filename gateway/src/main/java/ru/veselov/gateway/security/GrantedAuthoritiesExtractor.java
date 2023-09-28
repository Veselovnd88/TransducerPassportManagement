package ru.veselov.gateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class GrantedAuthoritiesExtractor implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
        log.trace("Extracting authorities from Jwt in custom AuthoritiesExtractor");
        JwtGrantedAuthoritiesConverter basicJwtAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        Collection<GrantedAuthority> convertedScopes = basicJwtAuthoritiesConverter.convert(jwt);
        List<String> springRoles = jwt.getClaimAsStringList("spring_roles");
        if (springRoles == null) {
            log.warn("No roles from claim [sprint_roles] found");
            springRoles = Collections.emptyList();
        }
        List<GrantedAuthority> actualRoles = Stream.concat(convertedScopes.stream(),
                springRoles.stream().filter(role -> role.startsWith("ROLE_"))
                        .map(SimpleGrantedAuthority::new)
                        .map(GrantedAuthority.class::cast)).toList();
        log.trace("Roles and scopes: {}", actualRoles);
        return actualRoles;
    }

}
