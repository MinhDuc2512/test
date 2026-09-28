package com.tracking.system.config;

import com.tracking.system.entity.*;
import com.tracking.system.enums.UserRole;
import com.tracking.system.enums.UserStatus;
import com.tracking.system.enums.ZoneTier;
import com.tracking.system.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final StatusCodeRepository statusCodeRepository;
    private final UserRepository userRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final PricingZoneRepository pricingZoneRepository;
    private final TariffRepository tariffRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedStatusCodes();
        seedUsers();
        seedServiceTypes();
        seedPricingZonesAndTariffs();
    }

    // ─────────────────────────────────────────────
    // 1. Trạng thái vận đơn (12 mã chuẩn)
    // ─────────────────────────────────────────────
    private void seedStatusCodes() {
        if (statusCodeRepository.count() > 0) return;
        log.info("Khởi tạo dữ liệu mẫu danh mục Trạng thái vận đơn...");

        List<StatusCode> defaultStatuses = Arrays.asList(
                new StatusCode("CREATED",            "Đã tạo đơn",                 "Order created",             false, 1),
                new StatusCode("PICKUP_PENDING",     "Chờ lấy hàng",               "Pending pickup",            false, 2),
                new StatusCode("PICKED_UP",          "Đã lấy hàng",                "Picked up",                 false, 3),
                new StatusCode("AT_SORT_HUB",        "Đã nhập kho phân loại",      "At sorting hub",            false, 4),
                new StatusCode("IN_TRANSIT",         "Đang trung chuyển",           "In transit",               false, 5),
                new StatusCode("ARRIVED_DEST_HUB",  "Đã đến bưu cục khu vực",     "Arrived at destination hub",false, 6),
                new StatusCode("OUT_FOR_DELIVERY",   "Đang giao hàng",             "Out for delivery",          false, 7),
                new StatusCode("DELIVERED",          "Giao hàng thành công",        "Delivered",                 true,  8),
                new StatusCode("DELIVERY_FAILED",    "Giao hàng thất bại",          "Delivery failed",           false, 9),
                new StatusCode("RETURNING",          "Đang hoàn hàng",             "Returning to sender",       false, 10),
                new StatusCode("RETURNED",           "Đã hoàn hàng",               "Returned to sender",        true,  11),
                new StatusCode("CANCELLED",          "Đã hủy đơn",                 "Cancelled",                 true,  12)
        );

        statusCodeRepository.saveAll(defaultStatuses);
        log.info("✅ Đã khởi tạo 12 trạng thái vận đơn chuẩn");
    }

    // ─────────────────────────────────────────────
    // 2. Tài khoản người dùng mặc định
    // ─────────────────────────────────────────────
    private void seedUsers() {
        if (userRepository.count() > 0) return;
        log.info("Khởi tạo các tài khoản người dùng mặc định...");

        userRepository.saveAll(List.of(
                User.builder()
                        .fullName("Quản Trị Viên Hệ Thống")
                        .phone("0999999999")
                        .email("admin@tracking.com")
                        .passwordHash(passwordEncoder.encode("admin123"))
                        .role(UserRole.ADMIN)
                        .status(UserStatus.ACTIVE)
                        .build(),
                User.builder()
                        .fullName("Shipper Nguyễn Văn Giao")
                        .phone("0988888888")
                        .email("shipper@tracking.com")
                        .passwordHash(passwordEncoder.encode("shipper123"))
                        .role(UserRole.SHIPPER)
                        .status(UserStatus.ACTIVE)
                        .build(),
                User.builder()
                        .fullName("Khách Hàng Trần Thị Khách")
                        .phone("0977777777")
                        .email("customer@tracking.com")
                        .passwordHash(passwordEncoder.encode("customer123"))
                        .role(UserRole.CUSTOMER)
                        .status(UserStatus.ACTIVE)
                        .build()
        ));

        log.info("✅ Tài khoản mẫu: Admin (0999999999/admin123) | Shipper (0988888888/shipper123) | Customer (0977777777/customer123)");
    }

    // ─────────────────────────────────────────────
    // 3. Loại dịch vụ (STD, EXPRESS)
    // ─────────────────────────────────────────────
    private void seedServiceTypes() {
        if (serviceTypeRepository.count() > 0) return;
        log.info("Khởi tạo danh mục loại dịch vụ...");

        serviceTypeRepository.saveAll(List.of(
                ServiceType.builder()
                        .code("STD")
                        .name("Tiêu chuẩn")
                        .maxWeightKg(new BigDecimal("30"))
                        .slaHours(72)
                        .description("Giao hàng tiêu chuẩn 2-3 ngày")
                        .build(),
                ServiceType.builder()
                        .code("EXPRESS")
                        .name("Hỏa tốc")
                        .maxWeightKg(new BigDecimal("10"))
                        .slaHours(24)
                        .description("Giao hàng hỏa tốc trong ngày/24h")
                        .build()
        ));

        log.info("✅ Đã khởi tạo 2 loại dịch vụ: STD, EXPRESS");
    }

    // ─────────────────────────────────────────────
    // 4. Vùng giá & Bảng cước
    // ─────────────────────────────────────────────
    private void seedPricingZonesAndTariffs() {
        if (pricingZoneRepository.count() > 0) return;
        log.info("Khởi tạo bảng vùng giá và cước phí...");

        // Seed pricing zones (nội thành / liên tỉnh lân cận / xuyên vùng)
        List<PricingZone> zones = List.of(
                // Nội thành HCM
                PricingZone.builder().fromProvince("Hồ Chí Minh").toProvince("Hồ Chí Minh").zoneTier(ZoneTier.INNER_CITY).build(),
                // Nội thành HN
                PricingZone.builder().fromProvince("Hà Nội").toProvince("Hà Nội").zoneTier(ZoneTier.INNER_CITY).build(),
                // HCM → tỉnh lân cận
                PricingZone.builder().fromProvince("Hồ Chí Minh").toProvince("Bình Dương").zoneTier(ZoneTier.SAME_REGION).build(),
                PricingZone.builder().fromProvince("Hồ Chí Minh").toProvince("Đồng Nai").zoneTier(ZoneTier.SAME_REGION).build(),
                PricingZone.builder().fromProvince("Hồ Chí Minh").toProvince("Long An").zoneTier(ZoneTier.SAME_REGION).build(),
                PricingZone.builder().fromProvince("Hồ Chí Minh").toProvince("Tiền Giang").zoneTier(ZoneTier.SAME_REGION).build(),
                // HCM → miền Trung
                PricingZone.builder().fromProvince("Hồ Chí Minh").toProvince("Đà Nẵng").zoneTier(ZoneTier.CROSS_REGION).build(),
                PricingZone.builder().fromProvince("Hồ Chí Minh").toProvince("Khánh Hòa").zoneTier(ZoneTier.CROSS_REGION).build(),
                // HCM → HN (xuyên vùng)
                PricingZone.builder().fromProvince("Hồ Chí Minh").toProvince("Hà Nội").zoneTier(ZoneTier.REMOTE).build(),
                PricingZone.builder().fromProvince("Hà Nội").toProvince("Hồ Chí Minh").zoneTier(ZoneTier.REMOTE).build(),
                // HN → miền Trung
                PricingZone.builder().fromProvince("Hà Nội").toProvince("Đà Nẵng").zoneTier(ZoneTier.CROSS_REGION).build(),
                PricingZone.builder().fromProvince("Hà Nội").toProvince("Nghệ An").zoneTier(ZoneTier.SAME_REGION).build(),
                PricingZone.builder().fromProvince("Hà Nội").toProvince("Hải Phòng").zoneTier(ZoneTier.INNER_CITY).build()
        );
        pricingZoneRepository.saveAll(zones);

        // Lấy service types vừa tạo
        ServiceType std = serviceTypeRepository.findByCode("STD").orElseThrow();
        ServiceType express = serviceTypeRepository.findByCode("EXPRESS").orElseThrow();

        // Seed tariffs: (serviceType, zone, weightFrom, weightTo, basePrice, pricePerExtraKg)
        // Đơn vị: VNĐ, kg
        List<Tariff> tariffs = List.of(
                // ─── STD ZONE_1 (nội thành) ───
                Tariff.builder().serviceType(std).zoneTier(ZoneTier.INNER_CITY).weightFromKg(bd("0")).weightToKg(bd("1")).basePrice(bd("15000")).pricePerExtraKg(bd("3000")).build(),
                Tariff.builder().serviceType(std).zoneTier(ZoneTier.INNER_CITY).weightFromKg(bd("1")).weightToKg(bd("5")).basePrice(bd("18000")).pricePerExtraKg(bd("3000")).build(),
                Tariff.builder().serviceType(std).zoneTier(ZoneTier.INNER_CITY).weightFromKg(bd("5")).weightToKg(bd("30")).basePrice(bd("30000")).pricePerExtraKg(bd("2500")).build(),
                // ─── STD ZONE_2 (liên tỉnh lân cận) ───
                Tariff.builder().serviceType(std).zoneTier(ZoneTier.SAME_REGION).weightFromKg(bd("0")).weightToKg(bd("1")).basePrice(bd("22000")).pricePerExtraKg(bd("4000")).build(),
                Tariff.builder().serviceType(std).zoneTier(ZoneTier.SAME_REGION).weightFromKg(bd("1")).weightToKg(bd("5")).basePrice(bd("26000")).pricePerExtraKg(bd("4000")).build(),
                Tariff.builder().serviceType(std).zoneTier(ZoneTier.SAME_REGION).weightFromKg(bd("5")).weightToKg(bd("30")).basePrice(bd("42000")).pricePerExtraKg(bd("3500")).build(),
                // ─── STD ZONE_3 (liên vùng) ───
                Tariff.builder().serviceType(std).zoneTier(ZoneTier.CROSS_REGION).weightFromKg(bd("0")).weightToKg(bd("1")).basePrice(bd("30000")).pricePerExtraKg(bd("5000")).build(),
                Tariff.builder().serviceType(std).zoneTier(ZoneTier.CROSS_REGION).weightFromKg(bd("1")).weightToKg(bd("5")).basePrice(bd("36000")).pricePerExtraKg(bd("5000")).build(),
                Tariff.builder().serviceType(std).zoneTier(ZoneTier.CROSS_REGION).weightFromKg(bd("5")).weightToKg(bd("30")).basePrice(bd("56000")).pricePerExtraKg(bd("4500")).build(),
                // ─── STD ZONE_4 (xuyên Bắc-Nam) ───
                Tariff.builder().serviceType(std).zoneTier(ZoneTier.REMOTE).weightFromKg(bd("0")).weightToKg(bd("1")).basePrice(bd("40000")).pricePerExtraKg(bd("6000")).build(),
                Tariff.builder().serviceType(std).zoneTier(ZoneTier.REMOTE).weightFromKg(bd("1")).weightToKg(bd("5")).basePrice(bd("48000")).pricePerExtraKg(bd("6000")).build(),
                Tariff.builder().serviceType(std).zoneTier(ZoneTier.REMOTE).weightFromKg(bd("5")).weightToKg(bd("30")).basePrice(bd("72000")).pricePerExtraKg(bd("5500")).build(),
                // ─── EXPRESS ZONE_1 (nội thành) ───
                Tariff.builder().serviceType(express).zoneTier(ZoneTier.INNER_CITY).weightFromKg(bd("0")).weightToKg(bd("1")).basePrice(bd("25000")).pricePerExtraKg(bd("5000")).build(),
                Tariff.builder().serviceType(express).zoneTier(ZoneTier.INNER_CITY).weightFromKg(bd("1")).weightToKg(bd("10")).basePrice(bd("30000")).pricePerExtraKg(bd("5000")).build(),
                // ─── EXPRESS ZONE_2 ───
                Tariff.builder().serviceType(express).zoneTier(ZoneTier.SAME_REGION).weightFromKg(bd("0")).weightToKg(bd("1")).basePrice(bd("35000")).pricePerExtraKg(bd("7000")).build(),
                Tariff.builder().serviceType(express).zoneTier(ZoneTier.SAME_REGION).weightFromKg(bd("1")).weightToKg(bd("10")).basePrice(bd("42000")).pricePerExtraKg(bd("7000")).build(),
                // ─── EXPRESS ZONE_3 ───
                Tariff.builder().serviceType(express).zoneTier(ZoneTier.CROSS_REGION).weightFromKg(bd("0")).weightToKg(bd("1")).basePrice(bd("50000")).pricePerExtraKg(bd("9000")).build(),
                Tariff.builder().serviceType(express).zoneTier(ZoneTier.CROSS_REGION).weightFromKg(bd("1")).weightToKg(bd("10")).basePrice(bd("60000")).pricePerExtraKg(bd("9000")).build(),
                // ─── EXPRESS ZONE_4 ───
                Tariff.builder().serviceType(express).zoneTier(ZoneTier.REMOTE).weightFromKg(bd("0")).weightToKg(bd("1")).basePrice(bd("65000")).pricePerExtraKg(bd("11000")).build(),
                Tariff.builder().serviceType(express).zoneTier(ZoneTier.REMOTE).weightFromKg(bd("1")).weightToKg(bd("10")).basePrice(bd("78000")).pricePerExtraKg(bd("11000")).build()
        );
        tariffRepository.saveAll(tariffs);

        log.info("✅ Đã khởi tạo {} vùng giá và {} mức cước", zones.size(), tariffs.size());
    }

    private BigDecimal bd(String val) {
        return new BigDecimal(val);
    }
}
