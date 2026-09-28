package com.tracking.system.service.impl;

import com.tracking.system.common.ResourceNotFoundException;
import com.tracking.system.dto.request.UpdateShipmentStatusRequest;
import com.tracking.system.dto.response.ShipmentStatusHistoryResponse;
import com.tracking.system.entity.Shipment;
import com.tracking.system.entity.ShipmentStatusHistory;
import com.tracking.system.entity.StatusCode;
import com.tracking.system.entity.User;
import com.tracking.system.repository.ShipmentRepository;
import com.tracking.system.repository.ShipmentStatusHistoryRepository;
import com.tracking.system.repository.StatusCodeRepository;
import com.tracking.system.repository.UserRepository;
import com.tracking.system.service.ShipmentStatusHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipmentStatusHistoryServiceImpl implements ShipmentStatusHistoryService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentStatusHistoryRepository historyRepository;
    private final StatusCodeRepository statusCodeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ShipmentStatusHistoryResponse updateStatus(UpdateShipmentStatusRequest request) {
        Shipment shipment = shipmentRepository.findByTrackingCode(request.getTrackingCode())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vận đơn: " + request.getTrackingCode()));

        StatusCode statusCode = statusCodeRepository.findById(request.getStatusCode())
                .orElseThrow(() -> new ResourceNotFoundException("Mã trạng thái không hợp lệ: " + request.getStatusCode()));

        User updatedByUser = null;
        if (request.getUpdatedByUserId() != null) {
            updatedByUser = userRepository.findById(request.getUpdatedByUserId()).orElse(null);
        }

        // 1. Create append-only history log
        ShipmentStatusHistory history = ShipmentStatusHistory.builder()
                .shipment(shipment)
                .statusCode(statusCode)
                .locationPostOfficeId(request.getLocationPostOfficeId())
                .lat(request.getLat())
                .lng(request.getLng())
                .note(request.getNote())
                .updatedByUser(updatedByUser)
                .updateSource(request.getUpdateSource())
                .build();

        ShipmentStatusHistory savedHistory = historyRepository.save(history);

        // 2. Update current_status_code cache on shipment
        shipment.setCurrentStatusCode(statusCode.getCode());
        if (request.getLocationPostOfficeId() != null) {
            shipment.setCurrentPostOfficeId(request.getLocationPostOfficeId());
        }
        shipmentRepository.save(shipment);

        return mapToResponse(savedHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentStatusHistoryResponse> getHistoryByTrackingCode(String trackingCode) {
        if (!shipmentRepository.existsByTrackingCode(trackingCode)) {
            throw new ResourceNotFoundException("Không tìm thấy vận đơn với mã: " + trackingCode);
        }

        return historyRepository.findByShipmentTrackingCodeOrderByCreatedAtAsc(trackingCode)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ShipmentStatusHistoryResponse mapToResponse(ShipmentStatusHistory h) {
        return ShipmentStatusHistoryResponse.builder()
                .id(h.getId())
                .statusCode(h.getStatusCode().getCode())
                .statusNameVi(h.getStatusCode().getNameVi())
                .statusNameEn(h.getStatusCode().getNameEn())
                .locationPostOfficeId(h.getLocationPostOfficeId())
                .lat(h.getLat())
                .lng(h.getLng())
                .note(h.getNote())
                .updatedByUserId(h.getUpdatedByUser() != null ? h.getUpdatedByUser().getId() : null)
                .updateSource(h.getUpdateSource())
                .createdAt(h.getCreatedAt())
                .build();
    }
}
