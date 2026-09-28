package com.tracking.system.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusCodeResponse {

    private String code;
    private String nameVi;
    private String nameEn;
    private Boolean isFinal;
    private Integer sortOrder;
}
