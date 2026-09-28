package com.tracking.system.entity;

import com.tracking.system.enums.CodPayer;
import com.tracking.system.enums.ShipmentSource;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipments", indexes = {
    @Index(name = "idx_shipments_status", columnList = "current_status_code"),
    @Index(name = "idx_shipments_shipper", columnList = "assigned_shipper_id"),
    @Index(name = "idx_shipments_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tracking_code", nullable = false, unique = true, length = 30)
    private String trackingCode;

    // Sender snapshot
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(name = "sender_name", nullable = false, length = 150)
    private String senderName;

    @Column(name = "sender_phone", nullable = false, length = 20)
    private String senderPhone;

    @Column(name = "sender_address", nullable = false)
    private String senderAddress;

    @Column(name = "sender_province", nullable = false, length = 100)
    private String senderProvince;

    @Column(name = "sender_district", nullable = false, length = 100)
    private String senderDistrict;

    @Column(name = "sender_ward", length = 100)
    private String senderWard;

    // Receiver snapshot
    @Column(name = "receiver_name", nullable = false, length = 150)
    private String receiverName;

    @Column(name = "receiver_phone", nullable = false, length = 20)
    private String receiverPhone;

    @Column(name = "receiver_address", nullable = false)
    private String receiverAddress;

    @Column(name = "receiver_province", nullable = false, length = 100)
    private String receiverProvince;

    @Column(name = "receiver_district", nullable = false, length = 100)
    private String receiverDistrict;

    @Column(name = "receiver_ward", length = 100)
    private String receiverWard;

    @Column(name = "service_type_id")
    private Long serviceTypeId;

    // Package dimensions & specs
    @Column(name = "weight_kg", nullable = false, precision = 8, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "length_cm", precision = 8, scale = 2)
    private BigDecimal lengthCm;

    @Column(name = "width_cm", precision = 8, scale = 2)
    private BigDecimal widthCm;

    @Column(name = "height_cm", precision = 8, scale = 2)
    private BigDecimal heightCm;

    @Column(name = "declared_value", precision = 14, scale = 0)
    private BigDecimal declaredValue;

    @Column(name = "item_note")
    private String itemNote;

    // Financials
    @Column(name = "cod_amount", precision = 14, scale = 0)
    private BigDecimal codAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "cod_payer")
    private CodPayer codPayer = CodPayer.RECEIVER;

    @Column(name = "shipping_fee", nullable = false, precision = 12, scale = 0)
    private BigDecimal shippingFee;

    @Column(name = "insurance_fee", precision = 12, scale = 0)
    private BigDecimal insuranceFee;

    @Column(name = "total_fee", nullable = false, precision = 12, scale = 0)
    private BigDecimal totalFee;

    // Current status cache
    @Column(name = "current_status_code", nullable = false, length = 30)
    private String currentStatusCode = "CREATED";

    @Column(name = "current_post_office_id")
    private Long currentPostOfficeId;

    @Column(name = "origin_post_office_id")
    private Long originPostOfficeId;

    @Column(name = "destination_post_office_id")
    private Long destinationPostOfficeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_shipper_id")
    private User assignedShipper;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentSource source = ShipmentSource.WEB;

    @Column(name = "partner_order_id", length = 100)
    private String partnerOrderId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
