package com.creatorsstudio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {
    private long totalFabricPurchases;
    private long totalAccessoryPurchases;
    private BigDecimal fabricExpense;
    private BigDecimal accessoryExpense;
    private BigDecimal totalExpense;
    private List<FabricListResponse> recentFabrics;
    private List<AccessoryListResponse> recentAccessories;
}
