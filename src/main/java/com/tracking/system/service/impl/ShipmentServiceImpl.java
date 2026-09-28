package com.tracking.system.service.impl;

import com.tracking.system.common.BadRequestException;
import com.tracking.system.common.ResourceNotFoundException;
import com.tracking.system.dto.request.CreateShipmentRequest;
import com.tracking.system.dto.response.ShipmentResponse;
import com.tracking.system.dto.response.ShipmentStatusHistoryResponse;
import com.tracking.system.entity.Shipment;
import com.tracking.system.entity.ShipmentStatusHistory;
import com.tracking.system.entity.StatusCode;
import com.tracking.system.entity.User;
import com.tracking.system.enums.UpdateSource;
import com.tracking.system.repository.ShipmentRepository;
import com.tracking.system.repository.ShipmentStatusHistoryRepository;
import com.tracking.system.repository.StatusCodeRepository;
import com.tracking.system.repository.UserRepository;
import com.tracking.system.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentStatusHistoryRepository historyRepository;
    private final StatusCodeRepository statusCodeRepository;
    private final UserRepository userRepository;

    private final Random random = new Random();

    @Override
    @Transactional
    public ShipmentResponse createShipment(CreateShipmentRequest request) {
        String trackingCode = generateTrackingCode();

        User sender = null;
        if (request.getSenderId() != null) {
            sender = userRepository.findById(request.getSenderId()).orElse(null);
        }

        BigDecimal insuranceFee = request.getInsuranceFee() != null ? request.getInsuranceFee() : BigDecimal.ZERO;
        BigDecimal totalFee = request.getShippingFee().add(insuranceFee);

        Shipment shipment = Shipment.builder()
                .trackingCode(trackingCode)
                .sender(sender)
                .senderName(request.getSenderName())
                .senderPhone(request.getSenderPhone())
                .senderAddress(request.getSenderAddress())
                .senderProvince(request.getSenderProvince())
                .senderDistrict(request.getSenderDistrict())
                .senderWard(request.getSenderWard())
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .receiverAddress(request.getReceiverAddress())
                .receiverProvince(request.getReceiverProvince())
                .receiverDistrict(request.getReceiverDistrict())
                .receiverWard(request.getReceiverWard())
                .serviceTypeId(request.getServiceTypeId())
                .weightKg(request.getWeightKg())
                .lengthCm(request.getLengthCm())
                .widthCm(request.getWidthCm())
                .heightCm(request.getHeightCm())
                .declaredValue(request.getDeclaredValue() != null ? request.getDeclaredValue() : BigDecimal.ZERO)
                .itemNote(request.getItemNote())
                .codAmount(request.getCodAmount() != null ? request.getCodAmount() : BigDecimal.ZERO)
                .codPayer(request.getCodPayer())
                .shippingFee(request.getShippingFee())
                .insuranceFee(insuranceFee)
                .totalFee(totalFee)
                .currentStatusCode("CREATED")
                .source(request.getSource())
                .partnerOrderId(request.getPartnerOrderId())
                .build();

        Shipment savedShipment = shipmentRepository.save(shipment);

        // Record initial status history
        StatusCode createdStatus = statusCodeRepository.findById("CREATED")
                .orElseThrow(() -> new ResourceNotFoundException("Status code CREATED not found"));

        ShipmentStatusHistory initialHistory = ShipmentStatusHistory.builder()
                .shipment(savedShipment)
                .statusCode(createdStatus)
                .note("Đơn hàng đã được tạo thành công")
                .updateSource(UpdateSource.SYSTEM)
                .build();

        historyRepository.save(initialHistory);

        return mapToResponse(savedShipment);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentResponse getShipmentByTrackingCode(String trackingCode) {
        Shipment shipment = shipmentRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Vận đơn không tồn tại với mã: " + trackingCode));

        return mapToResponse(shipment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentResponse> getAllShipments() {
        return shipmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private String generateTrackingCode() {
        String prefix = "VN";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        String suffix = String.format("%04d", random.nextInt(10000));
        String code = prefix + timestamp + suffix;

        while (shipmentRepository.existsByTrackingCode(code)) {
            suffix = String.format("%04d", random.nextInt(10000));
            code = prefix + timestamp + suffix;
        }

        return code;
    }

    private ShipmentResponse mapToResponse(Shipment shipment) {
        List<ShipmentStatusHistoryResponse> history = historyRepository.findByShipmentIdOrderByCreatedAtAsc(shipment.getId())
                .stream()
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

        return ShipmentResponse.builder()
                .id(shipment.getId())
                .trackingCode(shipment.getTrackingCode())
                .senderId(shipment.getSender() != null ? shipment.getSender().getId() : null)
                .senderName(shipment.getSenderName())
                .senderPhone(shipment.getSenderPhone())
                .senderAddress(shipment.getSenderAddress())
                .senderProvince(shipment.getSenderProvince())
                .senderDistrict(shipment.getSenderDistrict())
                .senderWard(shipment.getSenderWard())
                .receiverName(shipment.getReceiverName())
                .receiverPhone(shipment.getReceiverPhone())
                .receiverAddress(shipment.getReceiverAddress())
                .receiverProvince(shipment.getReceiverProvince())
                .receiverDistrict(shipment.getReceiverDistrict())
                .receiverWard(shipment.getReceiverWard())
                .serviceTypeId(shipment.getServiceTypeId())
                .weightKg(shipment.getWeightKg())
                .lengthCm(shipment.getLengthCm())
                .widthCm(shipment.getWidthCm())
                .heightCm(shipment.getHeightCm())
                .declaredValue(shipment.getDeclaredValue())
                .itemNote(shipment.getItemNote())
                .codAmount(shipment.getCodAmount())
                .codPayer(shipment.getCodPayer())
                .shippingFee(shipment.getShippingFee())
                .insuranceFee(shipment.getInsuranceFee())
                .totalFee(shipment.getTotalFee())
                .currentStatusCode(shipment.getCurrentStatusCode())
                .currentPostOfficeId(shipment.getCurrentPostOfficeId())
                .originPostOfficeId(shipment.getOriginPostOfficeId())
                .destinationPostOfficeId(shipment.getDestinationPostOfficeId())
                .assignedShipperId(shipment.getAssignedShipper() != null ? shipment.getAssignedShipper().getId() : null)
                .source(shipment.getSource())
                .partnerOrderId(shipment.getPartnerOrderId())
                .createdAt(shipment.getCreatedAt())
                .updatedAt(shipment.getUpdatedAt())
                .history(history)
                .build();
    }
}
