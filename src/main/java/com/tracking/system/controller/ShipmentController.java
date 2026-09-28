package com.tracking.system.controller;

import com.tracking.system.common.ApiResponse;
import com.tracking.system.dto.request.CreateShipmentRequest;
import com.tracking.system.dto.request.UpdateShipmentStatusRequest;
import com.tracking.system.dto.response.ShipmentResponse;
import com.tracking.system.dto.response.ShipmentStatusHistoryResponse;
import com.tracking.system.service.ShipmentService;
import com.tracking.system.service.ShipmentStatusHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
@Tag(name = "Shipment Management", description = "APIs cho quản lý đơn hàng & vận đơn nội bộ")
public class ShipmentController {

    private final ShipmentService shipmentService;
    private final ShipmentStatusHistoryService statusHistoryService;

    @PostMapping
    @Operation(summary = "Tạo vận đơn mới", description = "Tạo vận đơn mới và tự động sinh mã tracking_code cùng mốc trạng thái CREATED")
    public ResponseEntity<ApiResponse<ShipmentResponse>> createShipment(@Valid @RequestBody CreateShipmentRequest request) {
        ShipmentResponse response = shipmentService.createShipment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo vận đơn thành công", response));
    }

    @GetMapping("/{trackingCode}")
    @Operation(summary = "Lấy thông tin chi tiết vận đơn", description = "Truy vấn đầy đủ thông tin vận đơn và lịch sử bằng tracking code")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getShipmentByTrackingCode(@PathVariable String trackingCode) {
        ShipmentResponse response = shipmentService.getShipmentByTrackingCode(trackingCode);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Danh sách tất cả vận đơn", description = "Lấy danh sách tất cả vận đơn trong hệ thống")
    public ResponseEntity<ApiResponse<List<ShipmentResponse>>> getAllShipments() {
        List<ShipmentResponse> list = shipmentService.getAllShipments();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/status")
    @Operation(summary = "Cập nhật trạng thái vận đơn (Append-only)", description = "Cập nhật mốc trạng thái mới vào lịch sử vận chuyển và cập nhật trạng thái hiện tại")
    public ResponseEntity<ApiResponse<ShipmentStatusHistoryResponse>> updateStatus(@Valid @RequestBody UpdateShipmentStatusRequest request) {
        ShipmentStatusHistoryResponse response = statusHistoryService.updateStatus(request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái thành công", response));
    }

    @GetMapping("/{trackingCode}/history")
    @Operation(summary = "Lịch sử trạng thái vận đơn", description = "Lấy toàn bộ lịch sử (timeline) cập nhật trạng thái của 1 vận đơn")
    public ResponseEntity<ApiResponse<List<ShipmentStatusHistoryResponse>>> getStatusHistory(@PathVariable String trackingCode) {
        List<ShipmentStatusHistoryResponse> history = statusHistoryService.getHistoryByTrackingCode(trackingCode);
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}
