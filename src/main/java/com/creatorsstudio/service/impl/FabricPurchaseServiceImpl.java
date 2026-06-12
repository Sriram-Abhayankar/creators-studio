package com.creatorsstudio.service.impl;

import com.creatorsstudio.dto.request.FabricItemRequest;
import com.creatorsstudio.dto.request.FabricPurchaseRequest;
import com.creatorsstudio.dto.response.FabricDetailResponse;
import com.creatorsstudio.dto.response.FabricItemResponse;
import com.creatorsstudio.dto.response.FabricListResponse;
import com.creatorsstudio.entity.Fabric;
import com.creatorsstudio.entity.FabricItem;
import com.creatorsstudio.exception.ResourceNotFoundException;
import com.creatorsstudio.repository.FabricRepository;
import com.creatorsstudio.service.FabricPurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FabricPurchaseServiceImpl implements FabricPurchaseService {

    private final FabricRepository fabricRepository;
    // FabricItemRepository removed — items are saved via Fabric cascade

    @Override
    @Transactional
    public void createFabricPurchase(FabricPurchaseRequest request) {
        Fabric fabric = Fabric.builder()
                .fabricName(request.getFabricName())
                .fabricType(request.getFabricType())
                .purchaseDate(LocalDateTime.now())
                .fabricItems(new ArrayList<>())
                .build();

        BigDecimal totalPrice = buildFabricItems(fabric, request);
        fabric.setTotalPrice(totalPrice);
        fabricRepository.save(fabric);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FabricListResponse> getAllFabricPurchases(String name, String type, String startDate, String endDate, String sortBy) {
        java.time.LocalDateTime start = null;
        java.time.LocalDateTime end = null;
        if (startDate != null && !startDate.trim().isEmpty()) {
            start = java.time.LocalDate.parse(startDate).atStartOfDay();
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            end = java.time.LocalDate.parse(endDate).atTime(23, 59, 59, 999999999);
        }

        if (start != null && end != null && start.isAfter(end)) {
            throw new com.creatorsstudio.exception.ValidationException("Start date cannot be after end date");
        }

        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "purchaseDate");
        if ("oldest".equalsIgnoreCase(sortBy)) {
            sort = org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "purchaseDate");
        }

        String queryName = (name != null && !name.trim().isEmpty()) ? name.trim() : null;
        String queryType = (type != null && !type.trim().isEmpty()) ? type.trim() : null;

        return fabricRepository.searchFabrics(queryName, queryType, start, end, sort).stream()
                .map(fabric -> FabricListResponse.builder()
                        .id(fabric.getId())
                        .fabricName(fabric.getFabricName())
                        .fabricType(fabric.getFabricType())
                        .totalPrice(fabric.getTotalPrice())
                        .purchaseDate(fabric.getPurchaseDate())
                        .build())
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public FabricDetailResponse getFabricPurchaseById(Long id) {
        Fabric fabric = fabricRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fabric purchase not found with id: " + id));

        List<FabricItemResponse> itemResponses = new ArrayList<>();
        if (fabric.getFabricItems() != null) {
            itemResponses = fabric.getFabricItems().stream()
                    .map(item -> FabricItemResponse.builder()
                            .id(item.getId())
                            .colour(item.getColour())
                            .gsm(item.getGsm())
                            .weight(item.getWeight())
                            .rib(item.getRib())
                            .pricePerKg(item.getPricePerKg())
                            .rowTotal(item.getRowTotal())
                            .build())
                    .collect(Collectors.toList());
        }

        return FabricDetailResponse.builder()
                .id(fabric.getId())
                .fabricName(fabric.getFabricName())
                .fabricType(fabric.getFabricType())
                .totalPrice(fabric.getTotalPrice())
                .purchaseDate(fabric.getPurchaseDate())
                .fabricItems(itemResponses)
                .build();
    }

    @Override
    @Transactional
    public void updateFabricPurchase(Long id, FabricPurchaseRequest request) {
        Fabric fabric = fabricRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fabric purchase not found with id: " + id));

        // Update header fields — purchaseDate is preserved (original purchase date stays)
        fabric.setFabricName(request.getFabricName());
        fabric.setFabricType(request.getFabricType());

        // Full replacement strategy: clear existing items (orphanRemoval triggers delete)
        fabric.getFabricItems().clear();

        // Recreate items from request and recalculate totals
        BigDecimal totalPrice = buildFabricItems(fabric, request);
        fabric.setTotalPrice(totalPrice);

        fabricRepository.save(fabric);
    }

    @Override
    @Transactional
    public void deleteFabricPurchase(Long id) {
        if (!fabricRepository.existsById(id)) {
            throw new ResourceNotFoundException("Fabric purchase not found with id: " + id);
        }
        fabricRepository.deleteById(id);
    }

    /**
     * Shared helper: builds FabricItem entities from request, links them to the Fabric,
     * and returns the calculated totalPrice. Does NOT modify purchaseDate.
     */
    private BigDecimal buildFabricItems(Fabric fabric, FabricPurchaseRequest request) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        if (request.getFabricItems() != null) {
            for (FabricItemRequest itemRequest : request.getFabricItems()) {
                BigDecimal rowTotal = itemRequest.getWeight().multiply(itemRequest.getPricePerKg());
                totalPrice = totalPrice.add(rowTotal);

                FabricItem item = FabricItem.builder()
                        .fabric(fabric)
                        .colour(itemRequest.getColour())
                        .gsm(itemRequest.getGsm())
                        .weight(itemRequest.getWeight())
                        .rib(itemRequest.getRib())
                        .pricePerKg(itemRequest.getPricePerKg())
                        .rowTotal(rowTotal)
                        .build();

                fabric.getFabricItems().add(item);
            }
        }
        return totalPrice;
    }
}
