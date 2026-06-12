package com.creatorsstudio.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConePurchaseRequest {
    @NotBlank(message = "Accessory name is required")
    private String accessoryName;

    @NotNull(message = "Cone items list cannot be null")
    @Valid
    private List<ConeItemRequest> coneItems;
}
