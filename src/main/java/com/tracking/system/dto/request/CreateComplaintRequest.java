package com.tracking.system.dto.request;

import com.tracking.system.enums.ComplaintType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateComplaintRequest {

    @NotBlank(message = "Tracking code is required")
    private String trackingCode;

    @NotNull(message = "Complaint type is required")
    private ComplaintType type;

    private String description;

    private Long createdByUserId;
}
