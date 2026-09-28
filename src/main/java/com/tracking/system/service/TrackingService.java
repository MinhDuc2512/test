package com.tracking.system.service;

import com.tracking.system.dto.request.PublicTrackRequest;
import com.tracking.system.dto.response.PublicTrackingResponse;

import java.util.List;

public interface TrackingService {
    PublicTrackingResponse trackSingle(String trackingCode, String phoneLast4Digits);
    List<PublicTrackingResponse> trackBatch(PublicTrackRequest request);
}
