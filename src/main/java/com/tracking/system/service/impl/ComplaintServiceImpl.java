package com.tracking.system.service.impl;

import com.tracking.system.common.ResourceNotFoundException;
import com.tracking.system.dto.request.CreateComplaintRequest;
import com.tracking.system.dto.response.ComplaintResponse;
import com.tracking.system.entity.Complaint;
import com.tracking.system.entity.Shipment;
import com.tracking.system.entity.User;
import com.tracking.system.repository.ComplaintRepository;
import com.tracking.system.repository.ShipmentRepository;
import com.tracking.system.repository.UserRepository;
import com.tracking.system.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ShipmentRepository shipmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ComplaintResponse create(CreateComplaintRequest request) {
        Shipment shipment = shipmentRepository.findByTrackingCode(request.getTrackingCode())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng: " + request.getTrackingCode()));

        User creator = null;
        if (request.getCreatedByUserId() != null) {
            creator = userRepository.findById(request.getCreatedByUserId()).orElse(null);
        }

        // Nếu không có user, lấy user mặc định (customer demo) để tránh null constraint
        if (creator == null) {
            creator = userRepository.findByPhone("0977777777").orElse(null);
        }

        Complaint complaint = Complaint.builder()
                .shipment(shipment)
                .createdByUser(creator)
                .type(request.getType())
                .description(request.getDescription())
                .build();

        Complaint saved = complaintRepository.save(complaint);
        return toResponse(saved);
    }

    @Override
    public List<ComplaintResponse> getByTrackingCode(String trackingCode) {
        return complaintRepository.findByShipmentTrackingCode(trackingCode)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ComplaintResponse toResponse(Complaint c) {
        return ComplaintResponse.builder()
                .id(c.getId())
                .trackingCode(c.getShipment().getTrackingCode())
                .type(c.getType())
                .description(c.getDescription())
                .status(c.getStatus())
                .resolvedNote(c.getResolutionNote())
                .createdAt(c.getCreatedAt())
                .resolvedAt(c.getResolvedAt())
                .createdByUserId(c.getCreatedByUser() != null ? c.getCreatedByUser().getId() : null)
                .build();
    }
}
