package com.tracking.system.service.impl;

import com.tracking.system.dto.request.CalculateTariffRequest;
import com.tracking.system.dto.response.TariffCalculationResponse;
import com.tracking.system.entity.PricingZone;
import com.tracking.system.entity.ServiceType;
import com.tracking.system.entity.Tariff;
import com.tracking.system.enums.ZoneTier;
import com.tracking.system.repository.PricingZoneRepository;
import com.tracking.system.repository.ServiceTypeRepository;
import com.tracking.system.repository.TariffRepository;
import com.tracking.system.service.TariffService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TariffServiceImpl implements TariffService {

    private final PricingZoneRepository pricingZoneRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final TariffRepository tariffRepository;

    private static final BigDecimal FLAT_RATE = new BigDecimal("25000");
    private static final BigDecimal FLAT_RATE_EXTRA = new BigDecimal("5000");

    @Override
    public TariffCalculationResponse calculate(CalculateTariffRequest request) {
        String serviceCode = request.getServiceCode() == null ? "STD" : request.getServiceCode().toUpperCase();
        BigDecimal weight = request.getWeightKg();

        // Lấy service type
        ServiceType serviceType = serviceTypeRepository.findByCode(serviceCode)
                .orElse(null);

        // Xác định zone
        Optional<PricingZone> zoneOpt = pricingZoneRepository
                .findByFromProvinceAndToProvince(request.getFromProvince(), request.getToProvince());

        if (zoneOpt.isEmpty()) {
            // Thử chiều ngược lại
            zoneOpt = pricingZoneRepository
                    .findByFromProvinceAndToProvince(request.getToProvince(), request.getFromProvince());
        }

        BigDecimal estimatedFee;
        ZoneTier zoneTier = null;
        int slaHours = "EXPRESS".equals(serviceCode) ? 24 : 72;
        String serviceName = serviceType != null ? serviceType.getName() : serviceCode;

        if (zoneOpt.isPresent() && serviceType != null) {
            zoneTier = zoneOpt.get().getZoneTier();
            Optional<Tariff> tariffOpt = tariffRepository
                    .findFirstByServiceTypeIdAndZoneTierAndWeightFromKgLessThanEqualAndWeightToKgGreaterThanEqual(
                            serviceType.getId(), zoneTier, weight, weight);

            if (tariffOpt.isPresent()) {
                Tariff tariff = tariffOpt.get();
                BigDecimal extraKg = weight.subtract(tariff.getWeightFromKg()).max(BigDecimal.ZERO);
                estimatedFee = tariff.getBasePrice()
                        .add(extraKg.multiply(tariff.getPricePerExtraKg()).setScale(0, RoundingMode.CEILING));
            } else {
                // Fallback: tính theo đơn giá flat
                estimatedFee = calculateFlatRate(weight, serviceCode);
            }

            if (serviceType.getSlaHours() != null) {
                slaHours = serviceType.getSlaHours();
            }
        } else {
            // Fallback hoàn toàn: không tìm thấy zone/service
            estimatedFee = calculateFlatRate(weight, serviceCode);
        }

        // Tính ngày dự kiến
        LocalDate deliveryDate = LocalDate.now().plusDays((slaHours / 24) + 1);
        String estimatedDelivery = deliveryDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        return TariffCalculationResponse.builder()
                .fromProvince(request.getFromProvince())
                .toProvince(request.getToProvince())
                .zoneTier(zoneTier)
                .serviceCode(serviceCode)
                .serviceName(serviceName)
                .weightKg(weight)
                .estimatedFee(estimatedFee)
                .estimatedSlaHours(slaHours)
                .estimatedDeliveryDate(estimatedDelivery)
                .build();
    }

    private BigDecimal calculateFlatRate(BigDecimal weight, String serviceCode) {
        // Giá nền 25,000đ + 5,000đ/kg vượt
        BigDecimal base = FLAT_RATE;
        BigDecimal extra = weight.subtract(BigDecimal.ONE).max(BigDecimal.ZERO)
                .multiply(FLAT_RATE_EXTRA).setScale(0, RoundingMode.CEILING);
        BigDecimal total = base.add(extra);
        // EXPRESS đắt gấp 1.5
        if ("EXPRESS".equals(serviceCode)) {
            total = total.multiply(new BigDecimal("1.5")).setScale(0, RoundingMode.CEILING);
        }
        return total;
    }
}
