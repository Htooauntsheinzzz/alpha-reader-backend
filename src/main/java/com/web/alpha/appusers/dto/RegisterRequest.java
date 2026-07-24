package com.web.alpha.appusers.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record RegisterRequest(
    @NotBlank
    @NotBlank
    String name,

    @NotEmpty
    @Email
    String email,

    @NotBlank
    String password,

    @NotBlank
    String confirmPassword

){

}
