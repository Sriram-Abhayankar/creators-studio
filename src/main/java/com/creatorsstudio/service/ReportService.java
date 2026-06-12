package com.creatorsstudio.service;

import com.creatorsstudio.dto.response.CategoryExpenseResponse;
import com.creatorsstudio.dto.response.ExpenseSummaryResponse;
import com.creatorsstudio.dto.response.MonthlyExpenseResponse;

import java.util.List;

public interface ReportService {
    ExpenseSummaryResponse getExpenseSummary();
    List<CategoryExpenseResponse> getCategoryExpenses();
    List<MonthlyExpenseResponse> getMonthlyExpenses();
}
