package com.dhundhoo.acendMarketing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class LoginRequest {
    @NotNull(message = "Identifier is required")
    @NotBlank(message = "Identifier cannot be blank")
    private String identifier;

    @NotNull(message = "Password is required")
    @NotBlank(message = "Password cannot be blank")
    private String password;

}