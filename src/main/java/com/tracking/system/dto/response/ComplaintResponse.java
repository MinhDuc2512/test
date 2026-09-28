package com.tracking.system.dto.response;

import com.tracking.system.enums.ComplaintStatus;
import com.tracking.system.enums.ComplaintType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplaintResponse {
    private Long id;
    private String trackingCode;
    private ComplaintType type;
    private String description;
    private ComplaintStatus status;
    private String resolvedNote;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private Long createdByUserId;
}
