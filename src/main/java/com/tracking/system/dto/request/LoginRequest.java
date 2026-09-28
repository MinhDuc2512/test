package com.tracking.system.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Phone number or email is required")
    private String phoneOrEmail;

    @NotBlank(message = "Password is required")
    private String password;
}
