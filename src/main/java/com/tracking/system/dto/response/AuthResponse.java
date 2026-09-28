package com.tracking.system.dto.response;

import com.tracking.system.enums.UserRole;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private Long userId;
    private String fullName;
    private String phone;
    private String email;
    private UserRole role;
}
