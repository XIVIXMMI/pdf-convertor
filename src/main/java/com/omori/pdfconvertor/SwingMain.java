package com.omori.pdfconvertor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SwingMain extends JFrame {
    
    private JTextField pathField;
    private JTextArea statusArea;
    private JProgressBar progressBar;
    private JLabel progressLabel;
    private JLabel timerLabel;
    private JButton convertButton;
    private JButton cancelButton;
    
    private File[] selectedFolders;
    private volatile boolean isCancelled;
    private ExecutorService executor;
    private PDFToExcelService pdfToExcelService;
    private long startTime;
    private Timer timer;
    
    public SwingMain() {
        this.pdfToExcelService = new PDFToExcelService();
        this.executor = Executors.newFixedThreadPool(2);
        initializeUI();
        setupEventHandlers();
    }
    
    private void initializeUI() {
        setTitle("Công Cụ Chuyển Đổi PDF");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLabel = new JLabel("Công Cụ Chuyển Đổi PDF", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Center panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Directory selection section
        JPanel dirPanel = new JPanel(new BorderLayout(10, 10));
        dirPanel.setBorder(new TitledBorder("Chọn thư mục chứa file PDF:"));
        
        JButton browseButton = new JButton("Chọn Thư Mục");
        pathField = new JTextField();
        pathField.setEditable(false);
        pathField.setPreferredSize(new Dimension(300, 25));
        
        JPanel pathPanel = new JPanel(new BorderLayout(5, 0));
        pathPanel.add(browseButton, BorderLayout.WEST);
        pathPanel.add(pathField, BorderLayout.CENTER);
        dirPanel.add(pathPanel, BorderLayout.CENTER);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        centerPanel.add(dirPanel, gbc);
        
        // Drag and drop area
        JPanel dropPanel = new JPanel();
        dropPanel.setPreferredSize(new Dimension(400, 150));
        dropPanel.setBorder(new TitledBorder("Thả thư mục chứa file PDF tại đây"));
        dropPanel.setBackground(Color.LIGHT_GRAY);

        // Enable drag and drop
        new DropTarget(dropPanel, new DropTargetListener() {
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                dropPanel.setBackground(Color.GREEN.brighter());
            }
            
            @Override
            public void dragOver(DropTargetDragEvent dtde) {}
            
            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {}
            
            @Override
            public void dragExit(DropTargetEvent dte) {
                dropPanel.setBackground(Color.LIGHT_GRAY);
            }
            
            @Override
            public void drop(DropTargetDropEvent dtde) {
                dropPanel.setBackground(Color.LIGHT_GRAY);
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    @SuppressWarnings("unchecked")
                    List<File> droppedFiles = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    
                    selectedFolders = droppedFiles.stream()
                            .filter(File::isDirectory)
                            .toArray(File[]::new);
                            
                    if (selectedFolders.length > 0) {
                        StringBuilder paths = new StringBuilder();
                        for (int i = 0; i < selectedFolders.length; i++) {
                            if (i > 0) paths.append(", ");
                            paths.append(selectedFolders[i].getAbsolutePath());
                        }
                        pathField.setText(paths.toString());
                        setStatus("Đã chọn " + selectedFolders.length + " thư mục", Color.BLACK);
                        resetProgress();
                    } else {
                        setStatus("Vui lòng chọn thư mục hợp lệ", Color.RED);
                    }
                    dtde.dropComplete(true);
                } catch (Exception ex) {
                    setStatus("Lỗi khi xử lý drag & drop: " + ex.getMessage(), Color.RED);
                    dtde.dropComplete(false);
                }
            }
        });
        
        gbc.gridy = 1;
        centerPanel.add(dropPanel, gbc);
        
        // Progress section
        JPanel progressPanel = new JPanel(new BorderLayout(5, 5));
        progressPanel.setBorder(new TitledBorder("Tiến độ xử lý:"));
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressLabel = new JLabel("0/0 thư mục đã xử lý", SwingConstants.CENTER);
        timerLabel = new JLabel("Thời gian xử lý: 00:00:00");
        
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(timerLabel, BorderLayout.SOUTH);
        
        gbc.gridy = 2;
        centerPanel.add(progressPanel, gbc);
        
        // Status area
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(new TitledBorder("Trạng thái:"));
        
        statusArea = new JTextArea(8, 40);
        statusArea.setEditable(false);
        statusArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(statusArea);
        statusPanel.add(scrollPane, BorderLayout.CENTER);
        
        gbc.gridy = 3; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        centerPanel.add(statusPanel, gbc);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        convertButton = new JButton("Xuất dữ liệu");
        cancelButton = new JButton("Hủy");
        JButton exitButton = new JButton("Thoát");
        
        convertButton.setBackground(new Color(0, 120, 215)); // Màu xanh đẹp
        convertButton.setForeground(Color.WHITE);
        convertButton.setOpaque(true); // Quan trọng cho macOS
        convertButton.setBorderPainted(false); // Loại bỏ border mặc định
        cancelButton.setBackground(Color.RED.darker());
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setOpaque(true); // Quan trọng cho macOS
        cancelButton.setBorderPainted(false); // Loại bỏ border mặc định
        cancelButton.setEnabled(false);
        
        buttonPanel.add(convertButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(exitButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
        
        // Set up event handlers
        browseButton.addActionListener(e -> openDirectoryChooser());
        convertButton.addActionListener(e -> startConversion());
        cancelButton.addActionListener(e -> cancelOperation());
        exitButton.addActionListener(e -> exitApplication());
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void setupEventHandlers() {
        // Timer for elapsed time display
        timer = new Timer(1000, e -> {
            long elapsed = (System.currentTimeMillis() - startTime) / 1000;
            timerLabel.setText(String.format("Thời gian xử lý: %02d:%02d:%02d",
                    elapsed / 3600, (elapsed % 3600) / 60, elapsed % 60));
        });
    }
    
    private void openDirectoryChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Chọn thư mục chứa PDF");
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            selectedFolders = new File[]{selectedDirectory};
            pathField.setText(selectedDirectory.getAbsolutePath());
            setStatus("Đã chọn thư mục: " + selectedDirectory.getName(), Color.BLACK);
            resetProgress();
        }
    }
    
    private void startConversion() {
        if (selectedFolders == null || selectedFolders.length == 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn thư mục trước!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        isCancelled = false;
        convertButton.setEnabled(false);
        cancelButton.setEnabled(true);
        
        resetProgress();
        startTime = System.currentTimeMillis();
        timer.start();
        
        // Run conversion in background thread
        SwingUtilities.invokeLater(() -> {
            new Thread(this::processAllFolders).start();
        });
    }
    
    private void processAllFolders() {
        try {
            final AtomicInteger completedFolders = new AtomicInteger(0);
            final int totalFolders = selectedFolders.length;
            
            SwingUtilities.invokeLater(() -> updateProgress(0, totalFolders));
            
            // Process folders with executor
            for (File folder : selectedFolders) {
                if (isCancelled) break;
                
                executor.submit(() -> {
                    if (isCancelled) return;
                    
                    try {
                        String result = pdfToExcelService.convertFolderToExcel(folder, 
                            (processedPDFs) -> {
                                SwingUtilities.invokeLater(() -> 
                                    setStatus(String.format("%s: Đã xử lý %d PDFs", 
                                        folder.getName(), processedPDFs), Color.BLACK));
                            }
                        );
                        
                        int completed = completedFolders.incrementAndGet();
                        
                        SwingUtilities.invokeLater(() -> {
                            updateProgress(completed, totalFolders);
                            setStatus(result, Color.GREEN);
                            
                            if (completed >= totalFolders) {
                                finishConversion();
                            }
                        });
                        
                    } catch (Exception e) {
                        SwingUtilities.invokeLater(() -> 
                            setStatus("Lỗi xử lý thư mục " + folder.getName() + ": " + e.getMessage(), Color.RED));
                    }
                });
            }
            
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                setStatus("Error during conversion: " + e.getMessage(), Color.RED);
                finishConversion();
            });
        }
    }
    
    private void updateProgress(int completed, int total) {
        int percentage = total > 0 ? (completed * 100) / total : 0;
        progressBar.setValue(percentage);
        progressBar.setString(completed + "/" + total + " thư mục đã xử lý (" + percentage + "%)");
    }
    
    private void finishConversion() {
        timer.stop();
        convertButton.setEnabled(true);
        cancelButton.setEnabled(false);
        
        long totalTime = (System.currentTimeMillis() - startTime) / 1000;
        setStatus(String.format("Hoàn thành! Tổng thời gian xử lý: %02d:%02d:%02d",
                totalTime / 3600, (totalTime % 3600) / 60, totalTime % 60), Color.GREEN);
    }
    
    private void cancelOperation() {
        isCancelled = true;
        
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
            try {
                executor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            executor = Executors.newFixedThreadPool(2);
        }
        
        timer.stop();
        resetProgress();
        convertButton.setEnabled(true);
        cancelButton.setEnabled(false);
        timerLabel.setText("Thời gian xử lý: 00:00:00");
        setStatus("Đã hủy quá trình chuyển đổi.", Color.RED);
    }
    
    private void resetProgress() {
        progressBar.setValue(0);
        progressBar.setString("0%");
    }
    
    private void setStatus(String message, Color color) {
        statusArea.setForeground(color);
        statusArea.append(message + "\n");
        statusArea.setCaretPosition(statusArea.getDocument().getLength());
    }
    
    private void exitApplication() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        System.exit(0);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Force button colors trên macOS
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            } catch (Exception e) {
                // Ignore, use default
            }
            new SwingMain().setVisible(true);
        });
    }
}