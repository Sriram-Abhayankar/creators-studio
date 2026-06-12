package com.creatorsstudio.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class SizePatternPurchaseRequest {
    @NotBlank(message = "Accessory name is required")
    private String accessoryName;

    @NotBlank(message = "Brand name is required")
    private String brandName;

    @NotNull(message = "Style number cannot be null")
    private Integer styleNumber;

    @NotBlank(message = "Style name is required")
    private String styleName;

    @Positive(message = "Price must be positive")
    private BigDecimal price;
}
