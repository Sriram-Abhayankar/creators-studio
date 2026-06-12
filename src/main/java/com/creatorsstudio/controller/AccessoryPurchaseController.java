package com.creatorsstudio.controller;

import com.creatorsstudio.dto.request.ConePurchaseRequest;
import com.creatorsstudio.dto.request.OthersPurchaseRequest;
import com.creatorsstudio.dto.request.SizePatternPurchaseRequest;
import com.creatorsstudio.dto.response.AccessoryDetailResponse;
import com.creatorsstudio.dto.response.AccessoryListResponse;
import com.creatorsstudio.exception.ApiResponse;
import com.creatorsstudio.service.AccessoryPurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases/accessories")
@RequiredArgsConstructor
public class AccessoryPurchaseController {

    private final AccessoryPurchaseService accessoryPurchaseService;

    // ===================== CREATE =====================

    @PostMapping("/cone")
    public ResponseEntity<ApiResponse<Void>> createConePurchase(@Valid @RequestBody ConePurchaseRequest request) {
        accessoryPurchaseService.createConePurchase(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Cone purchase created successfully", null));
    }

    @PostMapping("/size-pattern")
    public ResponseEntity<ApiResponse<Void>> createSizePatternPurchase(@Valid @RequestBody SizePatternPurchaseRequest request) {
        accessoryPurchaseService.createSizePatternPurchase(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Size pattern purchase created successfully", null));
    }

    @PostMapping("/others")
    public ResponseEntity<ApiResponse<Void>> createOthersPurchase(@Valid @RequestBody OthersPurchaseRequest request) {
        accessoryPurchaseService.createOthersPurchase(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Others purchase created successfully", null));
    }

    // ===================== READ =====================

    @GetMapping
    public ResponseEntity<ApiResponse<List<AccessoryListResponse>>> getAllAccessoryPurchases(
            @RequestParam(required = false) String accessoryName,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String sortBy) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Accessory purchases retrieved successfully",
                accessoryPurchaseService.getAllAccessoryPurchases(accessoryName, type, startDate, endDate, sortBy)));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccessoryDetailResponse>> getAccessoryPurchaseById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Accessory purchase retrieved successfully",
                accessoryPurchaseService.getAccessoryPurchaseById(id)));
    }

    // ===================== UPDATE =====================

    @PutMapping("/cone/{id}")
    public ResponseEntity<ApiResponse<Void>> updateConePurchase(
            @PathVariable Long id, @Valid @RequestBody ConePurchaseRequest request) {
        accessoryPurchaseService.updateConePurchase(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cone purchase updated successfully", null));
    }

    @PutMapping("/size-pattern/{id}")
    public ResponseEntity<ApiResponse<Void>> updateSizePatternPurchase(
            @PathVariable Long id, @Valid @RequestBody SizePatternPurchaseRequest request) {
        accessoryPurchaseService.updateSizePatternPurchase(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Size pattern purchase updated successfully", null));
    }

    @PutMapping("/others/{id}")
    public ResponseEntity<ApiResponse<Void>> updateOthersPurchase(
            @PathVariable Long id, @Valid @RequestBody OthersPurchaseRequest request) {
        accessoryPurchaseService.updateOthersPurchase(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Others purchase updated successfully", null));
    }

    // ===================== DELETE =====================

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAccessoryPurchase(@PathVariable Long id) {
        accessoryPurchaseService.deleteAccessoryPurchase(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Accessory purchase deleted successfully", null));
    }
}
