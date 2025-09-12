package com.omori.pdfconvertor;

public class Main {
    public static void main(String[] args) {
        // If command line arguments provided, use headless mode
        if (args.length > 0) {
            HeadlessMain.main(args);
            return;
        }
        
        // Check if we're in a headless environment
        boolean headless = Boolean.parseBoolean(System.getProperty("java.awt.headless", "false"));
        
        if (headless) {
            HeadlessMain.main(args);
        } else {
            // Try Swing GUI first, fallback to headless if it fails
            try {
                // Test if AWT is available before launching GUI
                Class.forName("java.awt.Toolkit");
                SwingMain.main(args);
            } catch (Exception | Error e) {
                // Fallback to headless if GUI fails
                System.out.println("GUI không khả dụng, chuyển sang chế độ console...");
                System.out.println("Lỗi: " + e.getMessage());
                HeadlessMain.main(args);
            }
        }
    }
}
