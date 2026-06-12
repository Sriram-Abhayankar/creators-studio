package com.creatorsstudio.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConeItemRequest {
    @NotBlank(message = "Colour name is required")
    private String colourName;

    @NotBlank(message = "Colour code is required")
    private String colourCode;

    @Positive(message = "Unit must be positive")
    private Integer unit;

    @Positive(message = "Price per unit must be positive")
    private BigDecimal pricePerUnit;
}
