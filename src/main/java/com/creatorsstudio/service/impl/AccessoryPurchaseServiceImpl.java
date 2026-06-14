package com.creatorsstudio.service.impl;

import com.creatorsstudio.dto.request.ConeItemRequest;
import com.creatorsstudio.dto.request.ConePurchaseRequest;
import com.creatorsstudio.dto.request.OthersPurchaseRequest;
import com.creatorsstudio.dto.request.SizePatternPurchaseRequest;
import com.creatorsstudio.dto.response.*;
import com.creatorsstudio.entity.*;
import com.creatorsstudio.exception.ResourceNotFoundException;
import com.creatorsstudio.exception.ValidationException;
import com.creatorsstudio.repository.AccessoryRepository;
import com.creatorsstudio.service.AccessoryPurchaseService;
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
public class AccessoryPurchaseServiceImpl implements AccessoryPurchaseService {

    private final AccessoryRepository accessoryRepository;

    // =========================================================
    // CREATE OPERATIONS
    // =========================================================

    @Override
    @Transactional
    public void createConePurchase(ConePurchaseRequest request) {
        Accessory accessory = new Accessory();
        accessory.setAccessoryName(request.getAccessoryName());
        accessory.setPurchaseDate(LocalDateTime.now());

        Cone cone = new Cone();
        cone.setAccessory(accessory);
        accessory.setCone(cone);

        if (cone.getConeItems() == null) {
            cone.setConeItems(new ArrayList<>());
        }

        BigDecimal totalPrice = buildConeItems(cone, request.getConeItems());
        accessory.setTotalPrice(totalPrice);
        accessoryRepository.save(accessory);
    }

    @Override
    @Transactional
    public void createSizePatternPurchase(SizePatternPurchaseRequest request) {
        Accessory accessory = new Accessory();
        accessory.setAccessoryName(request.getAccessoryName());
        accessory.setTotalPrice(request.getPrice());
        accessory.setPurchaseDate(LocalDateTime.now());

        SizePattern sizePattern = new SizePattern();
        sizePattern.setBrandName(request.getBrandName());
        sizePattern.setStyleNumber(request.getStyleNumber());
        sizePattern.setStyleName(request.getStyleName());
        sizePattern.setPrice(request.getPrice());
        sizePattern.setAccessory(accessory);
        accessory.setSizePattern(sizePattern);

        accessoryRepository.save(accessory);
    }

    @Override
    @Transactional
    public void createOthersPurchase(OthersPurchaseRequest request) {
        Accessory accessory = new Accessory();
        accessory.setAccessoryName(request.getAccessoryName());
        // BUSINESS RULE: totalPrice = price (NOT unit × price)
        accessory.setTotalPrice(request.getPrice());
        accessory.setPurchaseDate(LocalDateTime.now());

        Others others = new Others();
        others.setItemsName(request.getItemsName());
        others.setUnit(BigDecimal.valueOf(request.getUnit()));
        others.setPrice(request.getPrice());
        others.setAccessory(accessory);
        accessory.setOthers(others);

        accessoryRepository.save(accessory);
    }

    // =========================================================
    // READ OPERATIONS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<AccessoryListResponse> getAllAccessoryPurchases(String name, String type, String startDate, String endDate, String sortBy) {
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

        String queryName = (name != null && !name.trim().isEmpty()) ? name.trim() : null;
        String queryType = (type != null && !type.trim().isEmpty()) ? type.trim() : null;

        List<Accessory> accessories;

        if ("oldest".equalsIgnoreCase(sortBy)) {
            accessories = accessoryRepository.searchAccessoriesOrderByPurchaseDateAsc(queryName, queryType, start, end);
        } else {
            accessories = accessoryRepository.searchAccessoriesOrderByPurchaseDateDesc(queryName, queryType, start, end);
        }

        return accessories.stream().map(a -> {
            String resolvedType = resolveType(a);
            return AccessoryListResponse.builder()
                    .id(a.getId())
                    .accessoryName(a.getAccessoryName())
                    .totalPrice(a.getTotalPrice())
                    .type(resolvedType)
                    .purchaseDate(a.getPurchaseDate())
                    .build();
        }).collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public AccessoryDetailResponse getAccessoryPurchaseById(Long id) {
        Accessory a = accessoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Accessory purchase not found with id: " + id));
        return buildDetailResponse(a);
    }

    // =========================================================
    // UPDATE OPERATIONS
    // =========================================================

    @Override
    @Transactional
    public void updateConePurchase(Long id, ConePurchaseRequest request) {
        Accessory accessory = accessoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Accessory purchase not found with id: " + id));

        if (accessory.getCone() == null) {
            throw new ValidationException("This accessory record is not a Cone type and cannot be updated as one.");
        }

        // Update accessory name; purchaseDate on Cone is preserved
        accessory.setAccessoryName(request.getAccessoryName());

        // Full replacement of cone items
        Cone cone = accessory.getCone();
        cone.getConeItems().clear();

        BigDecimal totalPrice = buildConeItems(cone, request.getConeItems());
        accessory.setTotalPrice(totalPrice);
        accessoryRepository.save(accessory);
    }

    @Override
    @Transactional
    public void updateSizePatternPurchase(Long id, SizePatternPurchaseRequest request) {
        Accessory accessory = accessoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Accessory purchase not found with id: " + id));

        if (accessory.getSizePattern() == null) {
            throw new ValidationException("This accessory record is not a Size Pattern type and cannot be updated as one.");
        }

        accessory.setAccessoryName(request.getAccessoryName());
        accessory.setTotalPrice(request.getPrice());

        SizePattern sp = accessory.getSizePattern();
        sp.setBrandName(request.getBrandName());
        sp.setStyleNumber(request.getStyleNumber());
        sp.setStyleName(request.getStyleName());
        sp.setPrice(request.getPrice());

        accessoryRepository.save(accessory);
    }

    @Override
    @Transactional
    public void updateOthersPurchase(Long id, OthersPurchaseRequest request) {
        Accessory accessory = accessoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Accessory purchase not found with id: " + id));

        if (accessory.getOthers() == null) {
            throw new ValidationException("This accessory record is not an Others type and cannot be updated as one.");
        }

        accessory.setAccessoryName(request.getAccessoryName());
        // BUSINESS RULE: totalPrice = price (NOT unit × price)
        accessory.setTotalPrice(request.getPrice());

        Others others = accessory.getOthers();
        others.setItemsName(request.getItemsName());
        others.setUnit(BigDecimal.valueOf(request.getUnit()));
        others.setPrice(request.getPrice());

        accessoryRepository.save(accessory);
    }

    // =========================================================
    // DELETE OPERATION
    // =========================================================

    @Override
    @Transactional
    public void deleteAccessoryPurchase(Long id) {
        if (!accessoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Accessory purchase not found with id: " + id);
        }
        accessoryRepository.deleteById(id);
    }

    // =========================================================
    // PRIVATE HELPERS
    // =========================================================

    /**
     * Determines the accessory type string based on which child entity is populated.
     */
    private String resolveType(Accessory a) {
        if (a.getCone() != null) return "CONE";
        if (a.getSizePattern() != null) return "SIZE_PATTERN";
        if (a.getOthers() != null) return "OTHERS";
        return "UNKNOWN";
    }

    /**
     * Builds ConeItem entities from request, links them to the cone, and returns totalPrice.
     */
    private BigDecimal buildConeItems(Cone cone, List<ConeItemRequest> itemRequests) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        if (itemRequests != null) {
            for (ConeItemRequest itemReq : itemRequests) {
                ConeItem coneItem = new ConeItem();
                coneItem.setColourName(itemReq.getColourName());
                coneItem.setColourCode(itemReq.getColourCode());

                BigDecimal unit = BigDecimal.valueOf(itemReq.getUnit());
                BigDecimal pricePerUnit = itemReq.getPricePerUnit();
                BigDecimal rowTotal = unit.multiply(pricePerUnit);

                coneItem.setUnit(unit);
                coneItem.setPricePerUnit(pricePerUnit);
                coneItem.setRowTotal(rowTotal);
                coneItem.setCone(cone);
                cone.getConeItems().add(coneItem);

                totalPrice = totalPrice.add(rowTotal);
            }
        }
        return totalPrice;
    }

    /**
     * Builds the full AccessoryDetailResponse from an Accessory entity.
     */
    private AccessoryDetailResponse buildDetailResponse(Accessory a) {
        String type = resolveType(a);
        ConeDetailResponse coneDetail = null;
        SizePatternDetailResponse spDetail = null;
        OthersDetailResponse othersDetail = null;

        if (a.getCone() != null) {
            List<ConeItemResponse> itemResponses = new ArrayList<>();
            if (a.getCone().getConeItems() != null) {
                itemResponses = a.getCone().getConeItems().stream()
                        .map(i -> ConeItemResponse.builder()
                                .id(i.getId())
                                .colourName(i.getColourName())
                                .colourCode(i.getColourCode())
                                .unit(i.getUnit())
                                .pricePerUnit(i.getPricePerUnit())
                                .rowTotal(i.getRowTotal())
                                .build())
                        .collect(Collectors.toList());
            }
            coneDetail = ConeDetailResponse.builder()
                    .coneItems(itemResponses)
                    .build();
        } else if (a.getSizePattern() != null) {
            spDetail = SizePatternDetailResponse.builder()
                    .brandName(a.getSizePattern().getBrandName())
                    .styleNumber(a.getSizePattern().getStyleNumber())
                    .styleName(a.getSizePattern().getStyleName())
                    .price(a.getSizePattern().getPrice())
                    .build();
        } else if (a.getOthers() != null) {
            othersDetail = OthersDetailResponse.builder()
                    .itemsName(a.getOthers().getItemsName())
                    .unit(a.getOthers().getUnit())
                    .price(a.getOthers().getPrice())
                    .build();
        }

        return AccessoryDetailResponse.builder()
                .id(a.getId())
                .accessoryName(a.getAccessoryName())
                .totalPrice(a.getTotalPrice())
                .type(type)
                .purchaseDate(a.getPurchaseDate())
                .createdAt(a.getCreatedAt())
                .cone(coneDetail)
                .sizePattern(spDetail)
                .others(othersDetail)
                .build();
    }
}
