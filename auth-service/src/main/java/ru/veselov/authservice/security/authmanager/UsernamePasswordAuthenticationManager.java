package ru.veselov.authservice.security.authmanager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UsernamePasswordAuthenticationManager implements AuthenticationManager {

    private final DaoAuthenticationProvider daoAuthenticationProvider;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.debug("UsernamePassword auth manager starting");
        Authentication checkedAuthentication
                = daoAuthenticationProvider.authenticate(authentication);
        if (checkedAuthentication.isAuthenticated()) {
            log.info("Credentials are ok");
            return checkedAuthentication;
        }
        log.error("Credentials are invalid");
        throw new AuthenticationCredentialsNotFoundException("Credentials are invalid");
    }

}
