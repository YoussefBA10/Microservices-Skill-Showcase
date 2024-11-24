package com.esprit.security.Form;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserRegisterForm {
    @NotEmpty(message="First Name can't be empty")
    @NotBlank(message="First Name can't be blank")
    private String firstname;
    @NotEmpty(message="Last Name can't be empty")
    @NotBlank(message="Last Name can't be blank")
    private String lastname;
    @Email(message="Email is not formatted")
    @NotEmpty(message="Email Name can't be empty")
    @NotBlank(message="Email can't be blank")
    private String email;
    @NotEmpty(message="Email Name can't be empty")
    @NotBlank(message="Email can't be blank")
    private String username;
    @NotEmpty(message="Password can't be empty")
    @NotBlank(message="Password can't be blank")
    @Size(min = 8, message = "Password must be more than 8 characters")
    private String password;

}
