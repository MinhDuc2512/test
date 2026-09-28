package com.tracking.system.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicTrackRequest {

    @NotEmpty(message = "Tracking codes list must not be empty")
    private List<String> trackingCodes;

    // Last 4 digits of phone number for verification (security feature from J&T survey)
    private String phoneLast4Digits;
}
