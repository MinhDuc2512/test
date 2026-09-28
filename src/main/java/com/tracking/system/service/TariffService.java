package com.tracking.system.service;

import com.tracking.system.dto.request.CalculateTariffRequest;
import com.tracking.system.dto.response.TariffCalculationResponse;

public interface TariffService {
    TariffCalculationResponse calculate(CalculateTariffRequest request);
}
