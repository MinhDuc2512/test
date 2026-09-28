package com.tracking.system.service;

import com.tracking.system.dto.request.UpdateShipmentStatusRequest;
import com.tracking.system.dto.response.ShipmentStatusHistoryResponse;

import java.util.List;

public interface ShipmentStatusHistoryService {
    ShipmentStatusHistoryResponse updateStatus(UpdateShipmentStatusRequest request);
    List<ShipmentStatusHistoryResponse> getHistoryByTrackingCode(String trackingCode);
}
