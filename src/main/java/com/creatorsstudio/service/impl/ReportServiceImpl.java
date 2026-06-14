package com.creatorsstudio.service.impl;

import com.creatorsstudio.dto.response.CategoryExpenseResponse;
import com.creatorsstudio.dto.response.ExpenseSummaryResponse;
import com.creatorsstudio.dto.response.MonthlyExpenseResponse;
import com.creatorsstudio.repository.AccessoryRepository;
import com.creatorsstudio.repository.FabricRepository;
import com.creatorsstudio.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final FabricRepository fabricRepository;
    private final AccessoryRepository accessoryRepository;

    @Override
    @Transactional(readOnly = true)
    public ExpenseSummaryResponse getExpenseSummary() {
        BigDecimal fabricExpense = fabricRepository.sumTotalPrice();
        BigDecimal accessoryExpense = accessoryRepository.sumTotalPrice();

        if (fabricExpense == null) fabricExpense = BigDecimal.ZERO;
        if (accessoryExpense == null) accessoryExpense = BigDecimal.ZERO;

        BigDecimal totalExpense = fabricExpense.add(accessoryExpense);

        return ExpenseSummaryResponse.builder()
                .fabricExpense(fabricExpense)
                .accessoryExpense(accessoryExpense)
                .totalExpense(totalExpense)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryExpenseResponse> getCategoryExpenses() {
        List<CategoryExpenseResponse> results = new ArrayList<>();

        // 1. Get Fabric category expenses grouped by fabricType
        List<Object[]> fabricTypes = fabricRepository.getFabricExpensesGroupedByType();
        if (fabricTypes != null) {
            for (Object[] row : fabricTypes) {
                String typeName = (String) row[0];
                BigDecimal amount = (BigDecimal) row[1];
                if (amount == null) amount = BigDecimal.ZERO;
                results.add(new CategoryExpenseResponse(typeName, amount));
            }
        }

        // 2. Get Accessory category expenses individually
        BigDecimal coneSum = accessoryRepository.sumConeExpenses();
        BigDecimal sizePatternSum = accessoryRepository.sumSizePatternExpenses();
        BigDecimal othersSum = accessoryRepository.sumOthersExpenses();

        if (coneSum != null && coneSum.compareTo(BigDecimal.ZERO) > 0) {
            results.add(new CategoryExpenseResponse("Cone", coneSum));
        }
        if (sizePatternSum != null && sizePatternSum.compareTo(BigDecimal.ZERO) > 0) {
            results.add(new CategoryExpenseResponse("Size Pattern", sizePatternSum));
        }
        if (othersSum != null && othersSum.compareTo(BigDecimal.ZERO) > 0) {
            results.add(new CategoryExpenseResponse("Others", othersSum));
        }

        // Sort by amount descending
        results.sort((r1, r2) -> r2.getTotalAmount().compareTo(r1.getTotalAmount()));

        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonthlyExpenseResponse> getMonthlyExpenses() {
        // Map to store combined monthly expenses. Key: "YYYY-MM"
        Map<String, MonthlyExpenseResponse> monthlyMap = new HashMap<>();

        // 1. Process Fabric monthly expenses
        List<Object[]> fabricMonths = fabricRepository.getFabricExpensesGroupedByMonth();
        if (fabricMonths != null) {
            for (Object[] row : fabricMonths) {
                if (row[0] == null || row[1] == null) continue;
                int year = ((Number) row[0]).intValue();
                int month = ((Number) row[1]).intValue();
                BigDecimal amount = (BigDecimal) row[2];
                if (amount == null) amount = BigDecimal.ZERO;

                String key = String.format("%04d-%02d", year, month);
                monthlyMap.put(key, MonthlyExpenseResponse.builder()
                        .year(year)
                        .month(month)
                        .fabricExpense(amount)
                        .accessoryExpense(BigDecimal.ZERO)
                        .totalExpense(amount)
                        .build());
            }
        }

        // 2. Process ALL Accessory monthly expenses
        // All accessory types (Cone, Size Pattern, Others) now have purchaseDate on the Accessory entity
        List<Object[]> accessoryMonths = accessoryRepository.getAccessoryExpensesGroupedByMonth();
        if (accessoryMonths != null) {
            for (Object[] row : accessoryMonths) {
                if (row[0] == null || row[1] == null) continue;
                int year = ((Number) row[0]).intValue();
                int month = ((Number) row[1]).intValue();
                BigDecimal amount = (BigDecimal) row[2];
                if (amount == null) amount = BigDecimal.ZERO;

                String key = String.format("%04d-%02d", year, month);
                if (monthlyMap.containsKey(key)) {
                    MonthlyExpenseResponse existing = monthlyMap.get(key);
                    existing.setAccessoryExpense(amount);
                    existing.setTotalExpense(existing.getFabricExpense().add(amount));
                } else {
                    monthlyMap.put(key, MonthlyExpenseResponse.builder()
                            .year(year)
                            .month(month)
                            .fabricExpense(BigDecimal.ZERO)
                            .accessoryExpense(amount)
                            .totalExpense(amount)
                            .build());
                }
            }
        }

        // Convert map values to list and sort by year DESC, month DESC
        List<MonthlyExpenseResponse> sortedList = new ArrayList<>(monthlyMap.values());
        sortedList.sort((m1, m2) -> {
            if (m1.getYear() != m2.getYear()) {
                return Integer.compare(m2.getYear(), m1.getYear());
            }
            return Integer.compare(m2.getMonth(), m1.getMonth());
        });

        return sortedList;
    }
}
