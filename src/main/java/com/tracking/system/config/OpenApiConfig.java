package com.tracking.system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI trackingSystemOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Hệ Thống Tra Cứu & Theo Dõi Vận Đơn")
                        .description("RESTful APIs cho Hệ thống Tra cứu & Theo dõi Vận đơn (Shipment Tracking System) mô phỏng J&T Express")
                        .version("v1.0.0")
                        .contact(new Contact().name("Development Team").email("dev@tracking.com"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }
}
