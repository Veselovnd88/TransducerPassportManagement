package ru.veselov.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.veselov.authservice.entity.UserEntity;
import ru.veselov.authservice.repository.UserRepository;
import ru.veselov.authservice.security.SecurityUser;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);
        UserEntity userEntity = optionalUserEntity.orElseThrow(() -> {
            log.error("User with [email: {}] not found", email);
            throw new UsernameNotFoundException("User with [email: %s] not found".formatted(email));
        });
        log.info("User with [email: {}] found, go to provider", email);
        return createSecurityUser(userEntity);
    }

    private SecurityUser createSecurityUser(UserEntity userEntity) {
        List<GrantedAuthority> roles = userEntity.getRoles().stream()
                .map(x -> new SimpleGrantedAuthority(x.getRole())).collect(Collectors.toList());
        log.debug("Converting to userDetails object");
        return SecurityUser.builder()
                .roles(roles)
                .password(userEntity.getPassword())
                .companyName(userEntity.getCompanyName())
                .firstname(userEntity.getFirstName())
                .lastname(userEntity.getLastName())
                .email(userEntity.getEmail())
                .build();
    }

}
