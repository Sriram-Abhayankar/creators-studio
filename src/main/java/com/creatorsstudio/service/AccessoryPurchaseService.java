package com.creatorsstudio.service;

import com.creatorsstudio.dto.request.ConePurchaseRequest;
import com.creatorsstudio.dto.request.OthersPurchaseRequest;
import com.creatorsstudio.dto.request.SizePatternPurchaseRequest;
import com.creatorsstudio.dto.response.AccessoryDetailResponse;
import com.creatorsstudio.dto.response.AccessoryListResponse;

import java.util.List;

public interface AccessoryPurchaseService {
    void createConePurchase(ConePurchaseRequest request);
    void createSizePatternPurchase(SizePatternPurchaseRequest request);
    void createOthersPurchase(OthersPurchaseRequest request);
    List<AccessoryListResponse> getAllAccessoryPurchases(String name, String type, String startDate, String endDate, String sortBy);
    AccessoryDetailResponse getAccessoryPurchaseById(Long id);
    void updateConePurchase(Long id, ConePurchaseRequest request);
    void updateSizePatternPurchase(Long id, SizePatternPurchaseRequest request);
    void updateOthersPurchase(Long id, OthersPurchaseRequest request);
    void deleteAccessoryPurchase(Long id);
}
