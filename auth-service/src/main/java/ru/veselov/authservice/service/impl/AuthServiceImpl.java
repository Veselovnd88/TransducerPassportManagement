package ru.veselov.authservice.service.impl;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.veselov.authservice.dto.LoginDto;
import ru.veselov.authservice.dto.RefreshTokenDto;
import ru.veselov.authservice.dto.SignUpDto;
import ru.veselov.authservice.dto.TokenDto;
import ru.veselov.authservice.entity.RoleEntity;
import ru.veselov.authservice.entity.UserEntity;
import ru.veselov.authservice.mapper.UserMapper;
import ru.veselov.authservice.repository.RoleRepository;
import ru.veselov.authservice.repository.UserRepository;
import ru.veselov.authservice.service.AuthService;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final UserMapper userMapper;

    @Override
    public TokenDto register(SignUpDto signUpDto) {
        String email = signUpDto.getEmail();
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            log.error("User with such [email: {}] already exists", email);
            throw new EntityExistsException("User with such [email: %s] already exists".formatted(email));
        }
        UserEntity userEntity = userMapper.signUpDtoToEntity(signUpDto);
        userEntity.setPassword(bCryptPasswordEncoder.encode(signUpDto.getPassword()));
        String role = signUpDto.getRole();
        Optional<RoleEntity> optionalRole = roleRepository.findByRoleName(role);
        RoleEntity roleEntity = optionalRole.orElseThrow(() -> {
            log.error("This [role: {}] not found", role);
            throw new EntityNotFoundException("This role: %s is not found".formatted(role));
        });
        userEntity.setRoles(Set.of(roleEntity));
        userRepository.save(userEntity);
        //tokenGenerator
        return null;
    }

    @Override
    public TokenDto login(LoginDto loginDto) {
        return null;
    }

    @Override
    public TokenDto refreshToken(RefreshTokenDto refreshTokenDto) {
        return null;
    }

}
