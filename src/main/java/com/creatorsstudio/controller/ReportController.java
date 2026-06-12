package com.creatorsstudio.controller;

import com.creatorsstudio.dto.response.CategoryExpenseResponse;
import com.creatorsstudio.dto.response.ExpenseSummaryResponse;
import com.creatorsstudio.dto.response.MonthlyExpenseResponse;
import com.creatorsstudio.exception.ApiResponse;
import com.creatorsstudio.service.ExportService;
import com.creatorsstudio.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ExportService exportService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<ExpenseSummaryResponse>> getExpenseSummary() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Expense summary report retrieved successfully",
                reportService.getExpenseSummary()));
    }

    @GetMapping("/category")
    public ResponseEntity<ApiResponse<List<CategoryExpenseResponse>>> getCategoryExpenses() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Category expense report retrieved successfully",
                reportService.getCategoryExpenses()));
    }

    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<List<MonthlyExpenseResponse>>> getMonthlyExpenses() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Monthly expense report retrieved successfully",
                reportService.getMonthlyExpenses()));
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<ApiResponse<Void>> exportPdf() {
        return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_IMPLEMENTED)
                .body(new ApiResponse<>(false, "PDF Export functionality is planned for a future phase.", null));
    }

    @GetMapping("/export/excel")
    public ResponseEntity<ApiResponse<Void>> exportExcel() {
        return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_IMPLEMENTED)
                .body(new ApiResponse<>(false, "Excel Export functionality is planned for a future phase.", null));
    }
}
