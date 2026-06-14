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
public class AccessoryDetailResponse {
    private Long id;
    private String accessoryName;
    private BigDecimal totalPrice;
    private String type;
    private LocalDateTime purchaseDate;
    private LocalDateTime createdAt;
    private ConeDetailResponse cone;
    private SizePatternDetailResponse sizePattern;
    private OthersDetailResponse others;
}
