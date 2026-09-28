package com.tracking.system.service.impl;

import com.tracking.system.common.ResourceNotFoundException;
import com.tracking.system.dto.request.PublicTrackRequest;
import com.tracking.system.dto.response.PublicTrackingResponse;
import com.tracking.system.dto.response.ShipmentStatusHistoryResponse;
import com.tracking.system.entity.Shipment;
import com.tracking.system.entity.ShipmentStatusHistory;
import com.tracking.system.repository.ShipmentRepository;
import com.tracking.system.repository.ShipmentStatusHistoryRepository;
import com.tracking.system.repository.StatusCodeRepository;
import com.tracking.system.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackingServiceImpl implements TrackingService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentStatusHistoryRepository historyRepository;
    private final StatusCodeRepository statusCodeRepository;

    @Override
    @Transactional(readOnly = true)
    public PublicTrackingResponse trackSingle(String trackingCode, String phoneLast4Digits) {
        Shipment shipment = shipmentRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vận đơn: " + trackingCode));

        return buildPublicResponse(shipment, phoneLast4Digits);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublicTrackingResponse> trackBatch(PublicTrackRequest request) {
        List<Shipment> shipments = shipmentRepository.findByTrackingCodeIn(request.getTrackingCodes());

        return shipments.stream()
                .map(shipment -> buildPublicResponse(shipment, request.getPhoneLast4Digits()))
                .collect(Collectors.toList());
    }

    private PublicTrackingResponse buildPublicResponse(Shipment shipment, String phoneLast4Digits) {
        boolean isVerified = verifyPhone(shipment, phoneLast4Digits);

        List<ShipmentStatusHistory> historyList = historyRepository.findByShipmentIdOrderByCreatedAtAsc(shipment.getId());
        List<ShipmentStatusHistoryResponse> timeline = historyList.stream()
                .map(h -> ShipmentStatusHistoryResponse.builder()
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
                        .build())
                .collect(Collectors.toList());

        // Lấy StatusCode 1 lần thay vì 2 lần riêng biệt
        var currentStatus = statusCodeRepository.findById(shipment.getCurrentStatusCode());
        String currentVi = currentStatus.map(s -> s.getNameVi()).orElse(shipment.getCurrentStatusCode());
        String currentEn = currentStatus.map(s -> s.getNameEn()).orElse(shipment.getCurrentStatusCode());

        return PublicTrackingResponse.builder()
                .trackingCode(shipment.getTrackingCode())
                .currentStatusCode(shipment.getCurrentStatusCode())
                .currentStatusNameVi(currentVi)
                .currentStatusNameEn(currentEn)
                .isVerified(isVerified)
                .senderName(isVerified ? shipment.getSenderName() : maskName(shipment.getSenderName()))
                .senderPhone(isVerified ? shipment.getSenderPhone() : maskPhone(shipment.getSenderPhone()))
                .senderProvince(shipment.getSenderProvince())
                .senderDistrict(shipment.getSenderDistrict())
                .senderAddress(isVerified ? shipment.getSenderAddress() : "*** (Bảo mật)")
                .receiverName(isVerified ? shipment.getReceiverName() : maskName(shipment.getReceiverName()))
                .receiverPhone(isVerified ? shipment.getReceiverPhone() : maskPhone(shipment.getReceiverPhone()))
                .receiverProvince(shipment.getReceiverProvince())
                .receiverDistrict(shipment.getReceiverDistrict())
                .receiverAddress(isVerified ? shipment.getReceiverAddress() : "*** (Bảo mật)")
                .weightKg(shipment.getWeightKg())
                .codAmount(shipment.getCodAmount())
                .createdAt(shipment.getCreatedAt())
                .timeline(timeline)
                .build();
    }

    private boolean verifyPhone(Shipment shipment, String phoneLast4Digits) {
        if (phoneLast4Digits == null || phoneLast4Digits.trim().length() != 4) {
            return false;
        }
        String cleanInput = phoneLast4Digits.trim();
        boolean matchesSender = shipment.getSenderPhone() != null && shipment.getSenderPhone().endsWith(cleanInput);
        boolean matchesReceiver = shipment.getReceiverPhone() != null && shipment.getReceiverPhone().endsWith(cleanInput);
        return matchesSender || matchesReceiver;
    }

    private String maskName(String name) {
        if (name == null || name.length() <= 2) return "***";
        return name.charAt(0) + "*****" + name.charAt(name.length() - 1);
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) return "*****";
        return "*****" + phone.substring(phone.length() - 4);
    }
}
