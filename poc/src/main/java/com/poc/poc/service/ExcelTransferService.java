package com.poc.poc.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

@Service
public class ExcelTransferService {

    private static final String EXCEL_FILE_PATH = "transfers.xlsx";
    private final Random random = new Random();

    @Scheduled(fixedRate = 1000)
    public void insertRandomTransferDetailsIntoExcel() {
        try (Workbook workbook = getWorkbook(EXCEL_FILE_PATH)) {
            Sheet sheet = getOrCreateSheet(workbook, "Transfers");
            int lastRowNum = sheet.getLastRowNum();
            Row row = sheet.createRow(lastRowNum + 1);
            String sourceAccount = "ACC" + (random.nextInt(9));
            String destinationAccount = "ACC" + (1000 + random.nextInt(9));
            double amount = random.nextInt(10000) + random.nextDouble();
            row.createCell(0).setCellValue(sourceAccount);
            row.createCell(1).setCellValue(destinationAccount);
            row.createCell(2).setCellValue(amount);
            try (FileOutputStream fos = new FileOutputStream(EXCEL_FILE_PATH)) {
                workbook.write(fos);
            }
            System.out.println("New transfer row added: "
                    + sourceAccount + " -> " + destinationAccount
                    + " | Amount: " + amount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Workbook getWorkbook(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                return new XSSFWorkbook(fis);
            }
        } else {
            return new XSSFWorkbook();
        }
    }

    private Sheet getOrCreateSheet(Workbook workbook, String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Source Account");
            header.createCell(1).setCellValue("Destination Account");
            header.createCell(2).setCellValue("Amount");
        }
        return sheet;
    }
}
