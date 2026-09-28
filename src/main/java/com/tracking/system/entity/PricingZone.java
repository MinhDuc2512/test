package com.tracking.system.entity;

import com.tracking.system.enums.ZoneTier;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pricing_zones", uniqueConstraints = {
    @UniqueConstraint(name = "uq_zone", columnNames = {"from_province", "to_province"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_province", nullable = false, length = 100)
    private String fromProvince;

    @Column(name = "to_province", nullable = false, length = 100)
    private String toProvince;

    @Enumerated(EnumType.STRING)
    @Column(name = "zone_tier", nullable = false)
    private ZoneTier zoneTier;
}
