package com.tracking.system.service;

import com.tracking.system.dto.request.CreateComplaintRequest;
import com.tracking.system.dto.response.ComplaintResponse;

import java.util.List;

public interface ComplaintService {
    ComplaintResponse create(CreateComplaintRequest request);
    List<ComplaintResponse> getByTrackingCode(String trackingCode);
}
