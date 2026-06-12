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
public class OthersPurchaseRequest {
    @NotBlank(message = "Accessory name is required")
    private String accessoryName;

    @NotBlank(message = "Items name is required")
    private String itemsName;

    @Positive(message = "Unit must be positive")
    private Integer unit;

    @Positive(message = "Price must be positive")
    private BigDecimal price;
}
