# PDF Converter JavaFX

A powerful JavaFX desktop application for extracting and converting data from PDF files to Excel format. This application provides an intuitive graphical user interface for processing PDF documents and exporting structured data to Excel spreadsheets.

## 🚀 Features

- **PDF Data Extraction**: Extract text, tables, and structured data from PDF files
- **Excel Export**: Convert extracted data to Excel (.xlsx) format
- **Batch Processing**: Process multiple PDF files at once
- **Progress Tracking**: Real-time progress bar and timer display
- **Cross-Platform**: Works on Windows, macOS, and Linux
- **User-Friendly Interface**: Clean and intuitive JavaFX GUI
- **Regex Pattern Matching**: Advanced text extraction using regex patterns
- **Error Handling**: Comprehensive error handling and logging

## 🛠 Technologies Used

### Core Technologies
- **Java 17** - Programming language and runtime
- **JavaFX 17.0.13** - Desktop GUI framework
- **Maven 3.9.6** - Build tool and dependency management

### Libraries & Dependencies
- **Apache PDFBox 2.0.29** - PDF document processing and text extraction
- **Apache POI 5.2.5** - Excel file creation and manipulation
- **Logback** - Logging framework
- **JUnit 5.10.2** - Unit testing framework

### Build & Packaging Tools
- **Maven Shade Plugin** - Creates executable JAR with dependencies
- **JPackage Plugin** - Creates native installers (.exe, .dmg, .app)
- **Maven Wrapper** - Ensures consistent Maven version across environments

## 📁 Project Structure

```
pdf-convertor-javafx/
├── src/
│   ├── main/
│   │   ├── java/com/omori/pdfconvertor/
│   │   │   ├── Main.java                    # Application entry point
│   │   │   ├── controller/
│   │   │   │   └── PDFConvertController.java # Main UI controller
│   │   │   ├── service/
│   │   │   │   ├── ConversionService.java    # PDF to Excel conversion logic
│   │   │   │   ├── PDFService.java           # PDF processing service
│   │   │   │   ├── ExcelService.java         # Excel file operations
│   │   │   │   ├── PDFToExcelService.java    # Main conversion service
│   │   │   │   └── TimerService.java         # Timer functionality
│   │   │   ├── model/
│   │   │   │   └── PDFData.java              # Data model for PDF content
│   │   │   ├── util/
│   │   │   │   └── RegexExtractor.java       # Regex pattern utilities
│   │   │   └── exception/
│   │   │       └── PDFProcessingException.java # Custom exceptions
│   │   └── resources/
│   │       └── com/omori/pdfconvertor/
│   │           ├── fxml/
│   │           │   ├── main-view.fxml        # Main UI layout
│   │           │   ├── styles.css            # Application styles
│   │           │   └── process-bar.css       # Progress bar styles
│   │           └── xml/
│   │               └── logback.xml           # Logging configuration
├── pom.xml                                   # Maven configuration
├── mvnw / mvnw.cmd                          # Maven wrapper scripts
└── pdf-converter-LITE/                      # Optimized Windows package
    └── pdf-converter-LITE-20250910.zip      # Ready-to-distribute package
```

## 🔧 Development Setup

### Prerequisites
- **Java 17** or higher (Eclipse Temurin recommended)
- **Maven 3.6+** (or use included Maven wrapper)
- **Git** for version control

### Local Development

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/pdf-convertor-javafx.git
   cd pdf-convertor-javafx
   ```

2. **Build the project**
   ```bash
   # Using Maven wrapper (recommended)
   ./mvnw clean compile
   
   # Or with system Maven
   mvn clean compile
   ```

3. **Run the application**
   ```bash
   # Using Maven wrapper
   ./mvnw javafx:run
   
   # Or with system Maven
   mvn javafx:run
   ```

4. **Run tests**
   ```bash
   ./mvnw test
   ```

## 📦 Building Distribution Packages

### Create Executable JAR
```bash
# Creates target/pdf_convert-1.0-SNAPSHOT.jar with all dependencies
./mvnw clean package -DskipTests
```

### Create Native Applications

#### Windows Executable (.exe)
```bash
# Requires Windows environment with jpackage
./mvnw jpackage:jpackage -Pwindows-exe
```

#### macOS Application (.app/.dmg)
```bash
# Requires macOS environment with jpackage
./mvnw jpackage:jpackage -Pmac
```

#### Linux Application
```bash
# Requires Linux environment with jpackage
./mvnw jpackage:jpackage -Plinux
```

## 🎯 Production Deployment

### Windows Offline Package (Recommended)

For environments without internet access or Java pre-installed:

1. **Download the optimized package**
   - `pdf-converter-LITE-20250910.zip` (1GB) - Contains everything needed

2. **Extract and run**
   - Extract ZIP file on Windows machine
   - Double-click `build-windows-fixed.bat`
   - No internet or Java installation required

### Features of LITE Package:
- ✅ **100% Offline** - No internet downloads required
- ✅ **Self-contained** - Includes JDK 17 + Maven + all dependencies  
- ✅ **Optimized Size** - Reduced by 300MB+ from full package
- ✅ **No installation** - Just extract and run
- ✅ **Direct paths** - No PATH configuration issues

## 🚀 Usage Guide

### Running the Application

1. **Launch the application**
   - Double-click the generated executable, or
   - Run: `java -jar target/pdf_convert-1.0-SNAPSHOT.jar`

2. **Select PDF files**
   - Click "Browse" to select single or multiple PDF files
   - Supported formats: .pdf

3. **Configure extraction**
   - Set output directory for Excel files
   - Choose extraction patterns (optional)

4. **Process files**
   - Click "Convert" to start processing
   - Monitor progress with built-in progress bar and timer

5. **Review results**
   - Generated Excel files will be saved to specified output directory
   - Check logs for any processing errors

### Command Line Usage
```bash
# Run with specific JVM options
java -Xmx2G -jar pdf_convert-1.0-SNAPSHOT.jar

# Run in headless mode (if supported)
java -Djava.awt.headless=true -jar pdf_convert-1.0-SNAPSHOT.jar
```

## 🐛 Troubleshooting

### Common Issues

1. **"JavaFX runtime components are missing"**
   ```bash
   # Ensure JavaFX is included in module path
   java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar app.jar
   ```

2. **Out of Memory errors with large PDFs**
   ```bash
   # Increase heap size
   java -Xmx4G -jar pdf_convert-1.0-SNAPSHOT.jar
   ```

3. **PDF processing errors**
   - Check PDF file integrity
   - Ensure PDF is not password-protected
   - Review logs in `logs/` directory

### Build Issues

1. **Maven dependency resolution**
   ```bash
   # Clear local repository and rebuild
   rm -rf ~/.m2/repository
   ./mvnw clean install
   ```

2. **JavaFX module issues**
   ```bash
   # Verify JavaFX dependencies
   ./mvnw dependency:tree | grep javafx
   ```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📧 Support

For questions, issues, or contributions:
- Create an issue on GitHub
- Contact: [your-email@example.com]

---

**Built with ❤️ using Java 17 & JavaFX**