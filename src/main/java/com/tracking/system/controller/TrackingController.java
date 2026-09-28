package com.tracking.system.controller;

import com.tracking.system.common.ApiResponse;
import com.tracking.system.dto.request.PublicTrackRequest;
import com.tracking.system.dto.response.PublicTrackingResponse;
import com.tracking.system.service.TrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tracking")
@RequiredArgsConstructor
@Tag(name = "Public Tracking Portal", description = "Cổng Tra Cứu Vận Đơn Công Khai (Kiểu J&T Express)")
public class TrackingController {

    private final TrackingService trackingService;

    @GetMapping("/{trackingCode}")
    @Operation(summary = "Tra cứu đơn lẻ", description = "Tra cứu thông tin 1 mã vận đơn. Nếu truyền phoneLast4 khớp với SĐT gửi/nhận thì sẽ hiển thị thông tin đầy đủ, ngược lại sẽ che thông tin cá nhân.")
    public ResponseEntity<ApiResponse<PublicTrackingResponse>> trackSingle(
            @PathVariable String trackingCode,
            @RequestParam(required = false) String phoneLast4) {
        PublicTrackingResponse response = trackingService.trackSingle(trackingCode, phoneLast4);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/batch")
    @Operation(summary = "Tra cứu nhiều mã cùng lúc", description = "Tra cứu đồng thời tối đa N mã vận đơn với xác thực 4 số cuối SĐT")
    public ResponseEntity<ApiResponse<List<PublicTrackingResponse>>> trackBatch(@Valid @RequestBody PublicTrackRequest request) {
        List<PublicTrackingResponse> responseList = trackingService.trackBatch(request);
        return ResponseEntity.ok(ApiResponse.success(responseList));
    }
}
