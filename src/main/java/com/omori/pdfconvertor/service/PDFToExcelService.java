package com.omori.pdfconvertor.service;

import com.omori.pdfconvertor.model.PDFData;
import com.omori.pdfconvertor.util.RegexExtractor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class PDFToExcelService {
    private static final Logger logger = LoggerFactory.getLogger(PDFToExcelService.class);
    
    private static final String[] HEADERS = {
        "Tên File", "Tên kinh doanh", "Địa chỉ", "Số serial", "Loại máy",
        "Mã máy", "Ghi chú", "MID", "TID", "TID 00"
    };

    // PDFTextStripper is not thread-safe, so we'll create instances as needed
    
    public PDFToExcelService() {
        // Constructor simplified since we create PDFTextStripper instances per thread
    }

    /**
     * Direct PDF to Excel conversion with parallel PDF processing
     * @param folder Folder containing PDF files
     * @param progressCallback Called every 5 PDFs processed (for batched UI updates)
     * @return Success message
     */
    public String convertFolderToExcel(File folder, Consumer<Integer> progressCallback) {
        if (!folder.exists() || !folder.isDirectory()) {
            return "Thư mục không hợp lệ: " + folder.getName();
        }

        File[] pdfFiles = Optional.ofNullable(
            folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"))
        ).orElse(new File[0]);

        if (pdfFiles.length == 0) {
            return "Không tìm thấy file PDF trong thư mục: " + folder.getName();
        }

        Arrays.sort(pdfFiles); // Consistent processing order
        String excelPath = folder.getAbsolutePath() + File.separator + folder.getName() + ".xlsx";

        // Use 4 threads for parallel PDF processing within the folder
        ExecutorService pdfExecutor = Executors.newFixedThreadPool(4);
        ConcurrentLinkedQueue<PDFResult> results = new ConcurrentLinkedQueue<>();
        AtomicInteger processedCount = new AtomicInteger(0);
        
        try {
            // Submit all PDF processing tasks
            for (int i = 0; i < pdfFiles.length; i++) {
                final File pdfFile = pdfFiles[i];
                final int index = i; // For maintaining order
                
                pdfExecutor.submit(() -> {
                    try {
                        PDFData data = extractDataFromPDF(pdfFile);
                        if (data != null) {
                            results.offer(new PDFResult(index, data, pdfFile.getName()));
                        }
                        
                        int completed = processedCount.incrementAndGet();
                        
                        // Batch UI updates every 5 files for performance
                        if (completed % 5 == 0 || completed == pdfFiles.length) {
                            progressCallback.accept(completed);
                        }
                        
                    } catch (Exception e) {
                        logger.error("Error processing PDF: {}", pdfFile.getName(), e);
                    }
                });
            }
            
            // Wait for all PDFs to be processed
            pdfExecutor.shutdown();
            pdfExecutor.awaitTermination(60, TimeUnit.SECONDS);
            
            // Create Excel with results (sorted by original order)
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("POS Data");
                createHeaders(sheet);
                
                // Sort results by original file order
                PDFResult[] sortedResults = results.stream()
                    .sorted((a, b) -> Integer.compare(a.index, b.index))
                    .toArray(PDFResult[]::new);
                
                int rowNum = 1;
                for (PDFResult result : sortedResults) {
                    writeDataRow(sheet, rowNum++, result.data, result.fileName);
                }

                // Auto-size columns
                for (int i = 0; i < HEADERS.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Write Excel file
                try (FileOutputStream outputStream = new FileOutputStream(excelPath)) {
                    workbook.write(outputStream);
                }

                return String.format("Đã xử lý %d PDFs thành công trong thư mục: %s (song song)", 
                                    sortedResults.length, folder.getName());
            }

        } catch (Exception e) {
            logger.error("Error in parallel PDF processing: {}", e.getMessage(), e);
            return "Lỗi xử lý song song: " + e.getMessage();
        } finally {
            if (!pdfExecutor.isShutdown()) {
                pdfExecutor.shutdownNow();
            }
        }
    }
    
    // Helper class to maintain order of results
    private static class PDFResult {
        final int index;
        final PDFData data;
        final String fileName;
        
        PDFResult(int index, PDFData data, String fileName) {
            this.index = index;
            this.data = data;
            this.fileName = fileName;
        }
    }

    private PDFData extractDataFromPDF(File pdfFile) {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            // Create optimized PDFTextStripper per thread for thread safety
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(false); // Faster text extraction
            stripper.setSuppressDuplicateOverlappingText(true); // Remove duplicates
            
            String text = stripper.getText(document);
            String extractedData = RegexExtractor.extractSpecificData(text);
            
            if (!extractedData.isEmpty()) {
                return parseExtractedData(extractedData);
            }
        } catch (IOException e) {
            logger.error("Error reading PDF: {}", pdfFile.getName(), e);
        }
        return null;
    }

    private PDFData parseExtractedData(String extractedData) {
        PDFData data = new PDFData();
        String[] lines = extractedData.split("\n");
        
        for (String line : lines) {
            if (line.startsWith("Tên kinh doanh: ")) {
                data.setBusinessName(line.substring("Tên kinh doanh: ".length()));
            } else if (line.startsWith("Địa chỉ: ")) {
                data.setAddress(line.substring("Địa chỉ: ".length()));
            } else if (line.startsWith("Số serial: ")) {
                data.setSerialNumber(line.substring("Số serial: ".length()));
            } else if (line.startsWith("Loại máy: ")) {
                data.setPosDevice(line.substring("Loại máy: ".length()));
            } else if (line.startsWith("Mã máy: ")) {
                data.setGroupName(line.substring("Mã máy: ".length()));
            } else if (line.startsWith("Ghi chú: ")) {
                data.setNotes(line.substring("Ghi chú: ".length()));
            } else if (line.startsWith("MID: ")) {
                data.setMerchantId(line.substring("MID: ".length()));
            } else if (line.startsWith("TID: ")) {
                data.setTerminalId(line.substring("TID: ".length()));
            } else if (line.startsWith("TID 00: ")) {
                data.setTerminalId00(line.substring("TID 00: ".length()));
            } else if (line.startsWith("TID V-TOP: ")) {
                data.setTerminalVtopId(line.substring("TID V-TOP: ".length()));
            } else if (line.startsWith("POS_V-TOP: ")) {
                data.setPosVtop(line.substring("POS_V-TOP: ".length()));
            }
        }
        return data;
    }

    private void createHeaders(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        Font headerFont = sheet.getWorkbook().createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void writeDataRow(Sheet sheet, int rowNum, PDFData data, String fileName) {
        Row row = sheet.createRow(rowNum);
        int colIndex = 0;
        
        row.createCell(colIndex++).setCellValue(fileName);
        row.createCell(colIndex++).setCellValue(data.getBusinessName());
        row.createCell(colIndex++).setCellValue(data.getAddress());
        row.createCell(colIndex++).setCellValue(data.getSerialNumber());
        row.createCell(colIndex++).setCellValue(data.getPosDevice());
        row.createCell(colIndex++).setCellValue(data.getGroupName());
        row.createCell(colIndex++).setCellValue(data.getNotes());
        row.createCell(colIndex++).setCellValue(data.getMerchantId());
        row.createCell(colIndex++).setCellValue(data.getTerminalId());
        row.createCell(colIndex).setCellValue(data.getTerminalId00());
        // Removed: TID V-TOP and POS V-TOP columns
    }
}