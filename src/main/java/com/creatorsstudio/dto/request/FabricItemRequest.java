package com.creatorsstudio.dto.request;

import jakarta.validation.constraints.Min;
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
public class FabricItemRequest {
    @NotBlank(message = "Colour is required")
    private String colour;

    @Min(value = 1, message = "GSM must be at least 1")
    private Integer gsm;

    @Positive(message = "Weight must be positive")
    private BigDecimal weight;

    @Min(value = 0, message = "Rib must be non-negative")
    private BigDecimal rib;

    @Positive(message = "Price per kg must be positive")
    private BigDecimal pricePerKg;
}
