package com.tracking.system.controller;

import com.tracking.system.common.ApiResponse;
import com.tracking.system.dto.request.CreateComplaintRequest;
import com.tracking.system.dto.response.ComplaintResponse;
import com.tracking.system.service.ComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/complaints")
@RequiredArgsConstructor
@Tag(name = "Complaint", description = "Quản lý khiếu nại & CSKH")
@SecurityRequirement(name = "bearerAuth")
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping
    @Operation(summary = "Tạo khiếu nại", description = "Gửi khiếu nại cho một đơn hàng (yêu cầu đăng nhập)")
    public ResponseEntity<ApiResponse<ComplaintResponse>> create(
            @Valid @RequestBody CreateComplaintRequest request) {
        ComplaintResponse result = complaintService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Khiếu nại đã được ghi nhận", result));
    }

    @GetMapping("/{trackingCode}")
    @Operation(summary = "Xem khiếu nại theo mã đơn")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> getByTrackingCode(
            @PathVariable String trackingCode) {
        List<ComplaintResponse> result = complaintService.getByTrackingCode(trackingCode);
        return ResponseEntity.ok(ApiResponse.success("Danh sách khiếu nại", result));
    }
}
