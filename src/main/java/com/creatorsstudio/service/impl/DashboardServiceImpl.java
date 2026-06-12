package com.creatorsstudio.service.impl;

import com.creatorsstudio.dto.response.AccessoryListResponse;
import com.creatorsstudio.dto.response.DashboardResponse;
import com.creatorsstudio.dto.response.FabricListResponse;
import com.creatorsstudio.repository.AccessoryRepository;
import com.creatorsstudio.repository.FabricRepository;
import com.creatorsstudio.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final FabricRepository fabricRepository;
    private final AccessoryRepository accessoryRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getDashboardSummary() {
        long totalFabricPurchases = fabricRepository.count();
        long totalAccessoryPurchases = accessoryRepository.count();

        BigDecimal fabricExpense = fabricRepository.sumTotalPrice();
        BigDecimal accessoryExpense = accessoryRepository.sumTotalPrice();
        
        if (fabricExpense == null) fabricExpense = BigDecimal.ZERO;
        if (accessoryExpense == null) accessoryExpense = BigDecimal.ZERO;
        
        BigDecimal totalExpense = fabricExpense.add(accessoryExpense);

        // Fetch top 5 recent fabrics sorted by purchaseDate DESC
        List<FabricListResponse> recentFabrics = fabricRepository.findAll(
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "purchaseDate"))
        ).getContent().stream().map(fabric -> FabricListResponse.builder()
                .id(fabric.getId())
                .fabricName(fabric.getFabricName())
                .fabricType(fabric.getFabricType())
                .totalPrice(fabric.getTotalPrice())
                .purchaseDate(fabric.getPurchaseDate())
                .build()
        ).collect(Collectors.toList());

        // Fetch top 5 recent accessories sorted by id DESC
        List<AccessoryListResponse> recentAccessories = accessoryRepository.findAll(
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id"))
        ).getContent().stream().map(a -> {
            String type = "UNKNOWN";
            if (a.getCone() != null) type = "CONE";
            else if (a.getSizePattern() != null) type = "SIZE_PATTERN";
            else if (a.getOthers() != null) type = "OTHERS";

            return AccessoryListResponse.builder()
                    .id(a.getId())
                    .accessoryName(a.getAccessoryName())
                    .totalPrice(a.getTotalPrice())
                    .type(type)
                    .build();
        }).collect(Collectors.toList());

        return DashboardResponse.builder()
                .totalFabricPurchases(totalFabricPurchases)
                .totalAccessoryPurchases(totalAccessoryPurchases)
                .fabricExpense(fabricExpense)
                .accessoryExpense(accessoryExpense)
                .totalExpense(totalExpense)
                .recentFabrics(recentFabrics)
                .recentAccessories(recentAccessories)
                .build();
    }
}
