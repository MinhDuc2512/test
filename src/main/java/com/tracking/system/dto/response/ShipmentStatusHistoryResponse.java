package com.tracking.system.dto.response;

import com.tracking.system.enums.UpdateSource;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentStatusHistoryResponse {

    private Long id;
    private String statusCode;
    private String statusNameVi;
    private String statusNameEn;
    private Long locationPostOfficeId;
    private BigDecimal lat;
    private BigDecimal lng;
    private String note;
    private Long updatedByUserId;
    private UpdateSource updateSource;
    private LocalDateTime createdAt;
}
