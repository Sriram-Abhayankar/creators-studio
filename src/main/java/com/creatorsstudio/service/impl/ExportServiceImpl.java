package com.creatorsstudio.service.impl;

import com.creatorsstudio.service.ExportService;
import org.springframework.stereotype.Service;

@Service
public class ExportServiceImpl implements ExportService {

    @Override
    public byte[] exportToPdf() {
        throw new UnsupportedOperationException("PDF Export functionality is planned for a future phase.");
    }

    @Override
    public byte[] exportToExcel() {
        throw new UnsupportedOperationException("Excel Export functionality is planned for a future phase.");
    }
}
