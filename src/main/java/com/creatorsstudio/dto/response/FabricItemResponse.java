package com.creatorsstudio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FabricItemResponse {
    private Long id;
    private String colour;
    private Integer gsm;
    private BigDecimal weight;
    private BigDecimal rib;
    private BigDecimal pricePerKg;
    private BigDecimal rowTotal;
}
