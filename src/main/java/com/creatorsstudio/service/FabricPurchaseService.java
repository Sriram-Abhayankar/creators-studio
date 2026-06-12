package com.creatorsstudio.service;

import com.creatorsstudio.dto.request.FabricPurchaseRequest;
import com.creatorsstudio.dto.response.FabricDetailResponse;
import com.creatorsstudio.dto.response.FabricListResponse;

import java.util.List;

public interface FabricPurchaseService {
    void createFabricPurchase(FabricPurchaseRequest request);
    List<FabricListResponse> getAllFabricPurchases(String name, String type, String startDate, String endDate, String sortBy);
    FabricDetailResponse getFabricPurchaseById(Long id);
    void updateFabricPurchase(Long id, FabricPurchaseRequest request);
    void deleteFabricPurchase(Long id);
}
