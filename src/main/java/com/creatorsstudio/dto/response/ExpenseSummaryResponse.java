package com.creatorsstudio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseSummaryResponse {
    private BigDecimal fabricExpense;
    private BigDecimal accessoryExpense;
    private BigDecimal totalExpense;
}
