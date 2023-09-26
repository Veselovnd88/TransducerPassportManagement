package ru.veselov.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpDto {

    private String email;

    private String companyName;

    private String firstName;

    private String lastName;

    private String password;

    private String confirmPassword;

    private String role;
}
