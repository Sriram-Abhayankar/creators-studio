package com.creatorsstudio.controller;

import com.creatorsstudio.dto.request.FabricPurchaseRequest;
import com.creatorsstudio.dto.response.FabricDetailResponse;
import com.creatorsstudio.dto.response.FabricListResponse;
import com.creatorsstudio.exception.ApiResponse;
import com.creatorsstudio.service.FabricPurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases/fabrics")
@RequiredArgsConstructor
public class FabricPurchaseController {

    private final FabricPurchaseService fabricPurchaseService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createFabricPurchase(@Valid @RequestBody FabricPurchaseRequest request) {
        fabricPurchaseService.createFabricPurchase(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Fabric purchase created successfully", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FabricListResponse>>> getAllFabricPurchases(
            @RequestParam(required = false) String fabricName,
            @RequestParam(required = false) String fabricType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String sortBy) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Fabric purchases retrieved successfully",
                fabricPurchaseService.getAllFabricPurchases(fabricName, fabricType, startDate, endDate, sortBy)));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FabricDetailResponse>> getFabricPurchaseById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Fabric purchase retrieved successfully",
                fabricPurchaseService.getFabricPurchaseById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateFabricPurchase(
            @PathVariable Long id, @Valid @RequestBody FabricPurchaseRequest request) {
        fabricPurchaseService.updateFabricPurchase(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Fabric purchase updated successfully", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFabricPurchase(@PathVariable Long id) {
        fabricPurchaseService.deleteFabricPurchase(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Fabric purchase deleted successfully", null));
    }
}
