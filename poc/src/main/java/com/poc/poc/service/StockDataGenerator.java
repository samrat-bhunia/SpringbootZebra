package com.poc.poc.service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class StockDataGenerator {

    private static final String[] STOCK_NAMES = {"AAPL", "GOOG", "MSFT", "AMZN", "TSLA"};
    private static final String FILE_PATH = "stock_data.xlsx";

    @Scheduled(fixedRate = 100) // Run every 10 seconds
    public void generateAndWriteStockData() {
        try (Workbook workbook = new XSSFWorkbook()) {
            for (String stockName : STOCK_NAMES) {
                Sheet sheet = workbook.createSheet(stockName);
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Timestamp");
                headerRow.createCell(1).setCellValue("Price");

                // Generate random stock price
                double price = new Random().nextDouble() * 100; 

                // Append data to the sheet
                Row dataRow = sheet.createRow(sheet.getLastRowNum() + 1);
                dataRow.createCell(0).setCellValue(java.time.LocalDateTime.now().toString());
                dataRow.createCell(1).setCellValue(price);
            }

            // Write data to the Excel file
            try (FileOutputStream outputStream = new FileOutputStream(new File(FILE_PATH))) {
                workbook.write(outputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}