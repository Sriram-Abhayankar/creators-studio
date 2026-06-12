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
public class FabricPurchaseRequest {
    @NotBlank(message = "Fabric name is required")
    private String fabricName;

    @NotBlank(message = "Fabric type is required")
    private String fabricType;

    @NotNull(message = "Fabric items list cannot be null")
    @Valid
    private List<FabricItemRequest> fabricItems;
}
