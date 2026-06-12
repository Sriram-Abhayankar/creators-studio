package com.creatorsstudio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FabricDetailResponse {
    private Long id;
    private String fabricName;
    private String fabricType;
    private BigDecimal totalPrice;
    private LocalDateTime purchaseDate;
    private List<FabricItemResponse> fabricItems;
}
