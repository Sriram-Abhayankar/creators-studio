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
public class ConeItemResponse {
    private Long id;
    private String colourName;
    private String colourCode;
    private BigDecimal unit;
    private BigDecimal pricePerUnit;
    private BigDecimal rowTotal;
}
