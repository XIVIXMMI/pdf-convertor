package com.omori.pdfconvertor;

import com.omori.pdfconvertor.service.PDFToExcelService;

import java.io.File;
import java.util.Scanner;

public class HeadlessMain {
    
    private PDFToExcelService pdfToExcelService;
    
    public HeadlessMain() {
        this.pdfToExcelService = new PDFToExcelService();
    }
    
    public static void main(String[] args) {
        HeadlessMain app = new HeadlessMain();
        
        if (args.length > 0) {
            // Command line mode
            app.processFolder(args[0]);
        } else {
            // Interactive mode
            app.runInteractive();
        }
    }
    
    private void runInteractive() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== Công Cụ Chuyển Đổi PDF (Headless Version) ===");
        System.out.println();
        
        while (true) {
            System.out.print("Nhập đường dẫn thư mục chứa PDF (hoặc 'exit' để thoát): ");
            String input = scanner.nextLine().trim();
            
            if ("exit".equalsIgnoreCase(input)) {
                System.out.println("Tạm biệt!");
                break;
            }
            
            if (input.isEmpty()) {
                System.out.println("Vui lòng nhập đường dẫn thư mục.");
                continue;
            }
            
            processFolder(input);
            System.out.println();
        }
        
        scanner.close();
    }
    
    private void processFolder(String folderPath) {
        File folder = new File(folderPath);
        
        if (!folder.exists()) {
            System.out.println("❌ Thư mục không tồn tại: " + folderPath);
            return;
        }
        
        if (!folder.isDirectory()) {
            System.out.println("❌ Đường dẫn không phải là thư mục: " + folderPath);
            return;
        }
        
        System.out.println("🔄 Bắt đầu xử lý thư mục: " + folder.getAbsolutePath());
        
        try {
            String result = pdfToExcelService.convertFolderToExcel(folder, 
                (processedCount) -> {
                    System.out.println("   📄 Đã xử lý " + processedCount + " file PDF...");
                }
            );
            
            System.out.println("✅ " + result);
            
        } catch (Exception e) {
            System.out.println("❌ Lỗi xử lý: " + e.getMessage());
            e.printStackTrace();
        }
    }
}