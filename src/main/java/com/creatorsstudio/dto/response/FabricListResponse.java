package com.creatorsstudio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FabricListResponse {
    private Long id;
    private String fabricName;
    private String fabricType;
    private BigDecimal totalPrice;
    private LocalDateTime purchaseDate;
}
