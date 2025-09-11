package com.omori.pdfconvertor.controller;

import com.omori.pdfconvertor.service.ExcelService;
import com.omori.pdfconvertor.service.PDFService;
import com.omori.pdfconvertor.service.PDFToExcelService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PDFConvertController {

    @FXML private TextField pdfPathField;
    @FXML private VBox dropArea;
    @FXML private TextArea statusTextArea;
    @FXML private ProgressBar progressBar;
    @FXML private Button cancelButton;
    @FXML private Button convertButton;
    @FXML private Label progressLabel;
    @FXML private Label timerLabel;

    private File[] selectedFolders;
    private volatile boolean isCancelled;
    private final PDFService pdfService;
    private final StringProperty progressMessage = new SimpleStringProperty();
    private final SimpleBooleanProperty converting = new SimpleBooleanProperty(false);
    private Task<Void> currentTask;
    private final ExcelService excelService;
    private final PDFToExcelService pdfToExcelService;
    
    // Limited 2-thread executor for optimal performance
    private ExecutorService optimizedExecutor;

    private final SimpleDoubleProperty progress = new SimpleDoubleProperty(0);
    private long startTime;
    private Timeline timer;
    private final SimpleLongProperty elapsedTimeInSeconds = new SimpleLongProperty(0);

    // Progress Bar
    private final DoubleProperty totalProgress = new SimpleDoubleProperty(0);

    public PDFConvertController() {
        this.pdfService = new PDFService();
        this.excelService = new ExcelService();
        this.pdfToExcelService = new PDFToExcelService();
        this.optimizedExecutor = Executors.newFixedThreadPool(2); // Only 2 threads for memory efficiency
    }

    private enum ConversionType {
        PDF_TO_TXT,
        TXT_TO_EXCEL,
        BOTH
    }

    // Controller
    @FXML
    public void initialize() {

        setupDragAndDrop();
        setupButtons();
        setupTimer();

        // Initialize totalProgress to 0 first, then bind
        totalProgress.set(0.0);  // Start at 0%
        progressBar.progressProperty().bind(totalProgress);
        
        convertButton.disableProperty().bind(converting);
        cancelButton.disableProperty().bind(converting.not());
    }

    private void setupButtons() {
        convertButton.setOnAction(event -> convertData());
        cancelButton.setOnAction(event -> cancelOperation());
    }

    @FXML
    private void convertData() {
        if (selectedFolders == null || selectedFolders.length == 0) {
            showError("Hãy chọn thư mục trước!");
            return;
        }

        // Reset progress
        Platform.runLater(() -> {
            totalProgress.set(0);
            progressLabel.setText("0%");
        });

        startConversion(ConversionType.BOTH);
    }

    private void startConversion(ConversionType type) {
        isCancelled = false;
        currentTask = createConversionTask(type);
        configureTaskBindings(currentTask);

        converting.set(true);
        Platform.runLater(() -> {
            progressBar.getStyleClass().removeAll("complete", "reset");
            progressBar.getStyleClass().add("running");
        });

        new Thread(currentTask).start();
    }

    private Task<Void> createConversionTask(ConversionType type) {
        return new Task<>() {
            @Override
            protected Void call() {
                try {
                    startTime = System.currentTimeMillis();
                    Platform.runLater(() ->{
                        startTime = System.currentTimeMillis();
                        progress.set(0);
                        timer.play();
                    });

                    // Use optimized concurrent processing
                    processAllFoldersOptimized();
                    return null;
                }catch ( Exception e) {
                    return null;
                } finally {
                    Platform.runLater(() -> {
                        converting.set(false);
                        timer.stop();
                        // Lưu tổng thời gian xử lý
                        long totalTime = (System.currentTimeMillis() - startTime) / 1000;
                        setStatus(String.format("Hoàn thành! Tổng thời gian xử lý: %02d:%02d:%02d",
                                totalTime / 3600, (totalTime % 3600) / 60, totalTime % 60), "green");
                    });
                }
            }
        };
    }

    private void setupTimer() {
        timer = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    elapsedTimeInSeconds.set((System.currentTimeMillis() - startTime) / 1000);
                })
        );
        timer.setCycleCount(Timeline.INDEFINITE);

        // Bind timer label to time property
        timerLabel.textProperty().bind(
                Bindings.createStringBinding(() ->
                                String.format("Thời gian xử lý: %02d:%02d:%02d",
                                        elapsedTimeInSeconds.get() / 3600,
                                        (elapsedTimeInSeconds.get() % 3600) / 60,
                                        elapsedTimeInSeconds.get() % 60),
                        elapsedTimeInSeconds
                )
        );
    }

    // Optimized processing with 2 threads max + batched UI updates
    private void processAllFoldersOptimized() throws InterruptedException {
        if (selectedFolders == null || selectedFolders.length == 0) return;
        
        final AtomicInteger completedFolders = new AtomicInteger(0);
        final int totalFolders = selectedFolders.length;
        
        // Update initial progress
        updateProgressLabel(0, totalFolders);
        Platform.runLater(() -> {
            totalProgress.set(0);
            progressLabel.setText("0%");
        });
        
        // Submit all folders to 2-thread executor
        for (File folder : selectedFolders) {
            if (isCancelled) break;
            
            optimizedExecutor.submit(() -> {
                if (isCancelled) return;
                
                try {
                    // Direct PDF to Excel conversion (skips TXT)
                    String result = pdfToExcelService.convertFolderToExcel(folder, 
                        (processedPDFs) -> {
                            // Batched UI updates every 5 PDFs
                            Platform.runLater(() -> {
                                setStatus(String.format("%s: Đã xử lý %d PDFs", 
                                    folder.getName(), processedPDFs), "black");
                            });
                        }
                    );
                    
                    int completed = completedFolders.incrementAndGet();
                    
                    // Update progress after each folder completion
                    Platform.runLater(() -> {
                        double progress = (double) completed / totalFolders;
                        totalProgress.set(progress);
                        progressLabel.setText(String.format("%.1f%%", progress * 100));
                        updateProgressLabel(completed, totalFolders);
                        setStatus(result, "green");
                        
                        if (progress >= 1.0) {
                            progressBar.getStyleClass().removeAll("running", "reset");
                            progressBar.getStyleClass().add("complete");
                        }
                    });
                    
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        setStatus("Lỗi xử lý thư mục " + folder.getName() + ": " + e.getMessage(), "red");
                    });
                }
            });
        }
        
        // Wait for all folders to complete
        optimizedExecutor.shutdown();
        optimizedExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        
        // Recreate executor for next conversion
        optimizedExecutor = Executors.newFixedThreadPool(2);
    }

    private void processFolderSequential(File folder, int completedTasks, int totalTasks, ConversionType type) {
        if (!folder.isDirectory()) {
            updateTaskMessage("Thư mục không hợp lệ: " + folder.getName());
            return;
        }

        try {
            // Calculate progress per folder
            double progressPerFolder = 1.0 / totalTasks;

            // PDF conversion phase (50% of folder progress)
            if (type == ConversionType.PDF_TO_TXT || type == ConversionType.BOTH) {
                String pdfResult = pdfService.convertAllPDFs(folder);
                updateTaskStatus(pdfResult, folder.getName());

                // Update progress for PDF phase
                Platform.runLater(() -> {
                    double currentProgress = totalProgress.get() + (progressPerFolder * 0.5);
                    totalProgress.set(currentProgress);
                    progressLabel.setText(String.format("%.1f%%", currentProgress * 100));
                });
            }

            // Excel conversion phase (remaining 50% of folder progress)
            if (type == ConversionType.TXT_TO_EXCEL || type == ConversionType.BOTH) {
                File txtFile = new File(folder, folder.getName() + ".txt");
                if (txtFile.exists()) {
                    excelService.convertTxtToExcel(txtFile);
                    updateTaskStatus("Chuyển đổi Excel thành công: " + folder.getName(),
                            folder.getName());

                    // Update progress for Excel phase
                    Platform.runLater(() -> {
                        double currentProgress = totalProgress.get() + (progressPerFolder * 0.5);
                        totalProgress.set(currentProgress);
                        progressLabel.setText(String.format("%.1f%%", currentProgress * 100));
                    });
                }
            }

            // Final progress update
            Platform.runLater(() -> {
                double finalProgress = (double) (completedTasks + 1) / totalTasks;
                progress.set(finalProgress);
                updateProgressDisplay(finalProgress);
            });

        } catch (Exception e) {
            updateTaskStatus("Lỗi xử lý: " + e.getMessage(), folder.getName());
        }
    }


    // reset progressBar
    private void resetProgressBar() {
        Platform.runLater(() -> {
            progressBar.getStyleClass().removeAll("complete", "running");
            progressBar.getStyleClass().add("reset");
            totalProgress.set(0);
            progressLabel.setText("0%");
        });
    }

    private void updateTaskStatus(String message, String fileName) {
        Platform.runLater(() -> {
            setStatus(fileName + ": " + message, "black");
            progressMessage.set(message);
        });
    }

    // Method to update the progress label text
    public void updateProgressLabel(int processed, int total) {
        progressMessage.set(processed + "/" + total + " thư mục đã xử lý");
    }

    private void updateProgressDisplay(double progress) {
        int percentage = (int) (progress * 100);
        progressLabel.setText(percentage + "%");

        // Khi tiến trình hoàn tất, làm nổi bật thanh tiến trình
        if (progress >= 1.0) {
            progressBar.getStyleClass().removeAll("running", "reset");
            progressBar.getStyleClass().add("complete");
            setStatus("Quá trình chuyển đổi hoàn tất!", "green");
        }
    }

    private void updateTaskMessage(String message) {
        Platform.runLater(() -> progressMessage.set(message));
    }

    private void configureTaskBindings(Task<Void> task) {
        // Don't bind to task.progressProperty() - we use our own totalProgress instead
        // This prevents the indeterminate sliding animation
        
        task.messageProperty().addListener((obs, oldMessage, newMessage) ->
                setStatus(newMessage, "black"));
    }

    private void setupDragAndDrop() {
        dropArea.setOnDragOver(this::handleDragOver);
        dropArea.setOnDragDropped(this::handleDragDropped);

        // Add visual feedback
        dropArea.setOnDragEntered(e -> dropArea.setStyle("-fx-border-color: green;"));
        dropArea.setOnDragExited(e -> dropArea.setStyle(""));
    }

    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    private void handleDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;

        if (db.hasFiles()) {
            selectedFolders = db.getFiles().stream()
                    .filter(File::isDirectory)
                    .toArray(File[]::new);

            if (selectedFolders.length > 0) {
                pdfPathField.setText(String.join(", ",
                        db.getFiles().stream()
                                .map(File::getAbsolutePath)
                                .toArray(String[]::new)));
                success = true;
                setStatus("Đã chọn " + selectedFolders.length + " thư mục", "black");
                resetProgressBar();
            } else {
                setStatus("Vui lòng chọn thư mục hợp lệ", "red");
            }
        }

        event.setDropCompleted(success);
        event.consume();
    }


    private void cancelOperation() {
        isCancelled = true;
        if (currentTask != null) {
            currentTask.cancel(true);
        }
        if (optimizedExecutor != null && !optimizedExecutor.isShutdown()) {
            optimizedExecutor.shutdownNow();
            optimizedExecutor = Executors.newFixedThreadPool(2); // Recreate for next use
        }

        Platform.runLater(() -> {
            resetProgressBar();
            totalProgress.set(0);
            progressLabel.setText("Đã hủy tác vụ");
            progressBar.setStyle(""); // Reset màu thanh tiến trình
            timer.stop();
            elapsedTimeInSeconds.set(0);
            timerLabel.setText("Thời gian xử lý: 00:00:00");
            convertButton.setDisable(false);
            cancelButton.setDisable(true);
            setStatus("Đã hủy quá trình chuyển đổi.", "red");
        });
    }

    @FXML
    private void openDirectoryChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Chọn thư mục chứa PDF");
        File selectedDirectory = directoryChooser.showDialog(dropArea.getScene().getWindow());

        if (selectedDirectory != null) {
            selectedFolders = new File[]{selectedDirectory};
            pdfPathField.setText(selectedDirectory.getAbsolutePath());
            setStatus("Đã chọn thư mục: " + selectedDirectory.getName(), "black");
        }
    }

    public void shutdown() {
        // Cleanup resources if needed
        if (currentTask != null) {
            currentTask.cancel(true);
        }
        if (optimizedExecutor != null && !optimizedExecutor.isShutdown()) {
            optimizedExecutor.shutdown();
            try {
                if (!optimizedExecutor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    optimizedExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                optimizedExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    @FXML
    private void exitApplication() {
        shutdown(); // Clean up resources
        Platform.exit();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setStatus(String message, String color) {
        Platform.runLater(() -> {
            statusTextArea.setStyle("-fx-text-fill: " + color + ";");
            statusTextArea.appendText(message + "\n");
            statusTextArea.setScrollTop(Double.MAX_VALUE); // Auto-scroll to bottom
        });
    }

}
