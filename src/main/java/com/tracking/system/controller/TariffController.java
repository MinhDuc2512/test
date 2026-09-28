package com.tracking.system.controller;

import com.tracking.system.common.ApiResponse;
import com.tracking.system.dto.request.CalculateTariffRequest;
import com.tracking.system.dto.response.TariffCalculationResponse;
import com.tracking.system.service.TariffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tariffs")
@RequiredArgsConstructor
@Tag(name = "Tariff Calculator", description = "Tính cước phí vận chuyển")
public class TariffController {

    private final TariffService tariffService;

    @PostMapping("/calculate")
    @Operation(summary = "Tính cước phí", description = "Tính cước phí dựa trên tỉnh gửi/nhận, cân nặng và loại dịch vụ (PUBLIC)")
    public ResponseEntity<ApiResponse<TariffCalculationResponse>> calculate(
            @Valid @RequestBody CalculateTariffRequest request) {
        TariffCalculationResponse result = tariffService.calculate(request);
        return ResponseEntity.ok(ApiResponse.success("Tính cước phí thành công", result));
    }
}
