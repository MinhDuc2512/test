package com.tracking.system.entity;

import com.tracking.system.enums.ZoneTier;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tariffs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_type_id", nullable = false)
    private ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "zone_tier", nullable = false)
    private ZoneTier zoneTier;

    @Column(name = "weight_from_kg", nullable = false, precision = 8, scale = 2)
    private BigDecimal weightFromKg;

    @Column(name = "weight_to_kg", nullable = false, precision = 8, scale = 2)
    private BigDecimal weightToKg;

    @Column(name = "base_price", nullable = false, precision = 12, scale = 0)
    private BigDecimal basePrice;

    @Column(name = "price_per_extra_kg", nullable = false, precision = 12, scale = 0)
    private BigDecimal pricePerExtraKg;
}
