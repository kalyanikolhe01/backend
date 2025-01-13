package com.dhundhoo.acendMarketing.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SessionRequestDTO {
    @NotNull(message = "Access code cannot be null")
    @NotBlank(message = "Access code cannot blank")
    @Pattern(regexp = "^[A-Za-z0-9-_\\.]+$", message = "Invalid access code format.")
    private String accessCode;
}
