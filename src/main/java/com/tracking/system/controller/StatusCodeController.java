package com.tracking.system.controller;

import com.tracking.system.common.ApiResponse;
import com.tracking.system.dto.response.StatusCodeResponse;
import com.tracking.system.repository.StatusCodeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/status-codes")
@RequiredArgsConstructor
@Tag(name = "Status Codes Master Data", description = "Danh mục Trạng thái Vận đơn Lõi")
public class StatusCodeController {

    private final StatusCodeRepository statusCodeRepository;

    @GetMapping
    @Operation(summary = "Danh sách danh mục trạng thái", description = "Lấy tất cả mã trạng thái hệ thống theo thứ tự quy trình vận chuyển")
    public ResponseEntity<ApiResponse<List<StatusCodeResponse>>> getAllStatusCodes() {
        List<StatusCodeResponse> list = statusCodeRepository.findAllByOrderBySortOrderAsc().stream()
                .map(s -> StatusCodeResponse.builder()
                        .code(s.getCode())
                        .nameVi(s.getNameVi())
                        .nameEn(s.getNameEn())
                        .isFinal(s.getIsFinal())
                        .sortOrder(s.getSortOrder())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
