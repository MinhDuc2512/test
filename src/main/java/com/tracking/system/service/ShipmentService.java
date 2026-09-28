package com.tracking.system.service;

import com.tracking.system.dto.request.CreateShipmentRequest;
import com.tracking.system.dto.response.ShipmentResponse;

import java.util.List;

public interface ShipmentService {
    ShipmentResponse createShipment(CreateShipmentRequest request);
    ShipmentResponse getShipmentByTrackingCode(String trackingCode);
    List<ShipmentResponse> getAllShipments();
}
