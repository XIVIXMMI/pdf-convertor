# 📄 PDF Converter Tool

A powerful PDF to Excel converter with a user-friendly graphical interface. The application supports batch processing and real-time progress tracking.

## 🚀 Features

- **🔄 PDF to Excel Conversion**: Extract data from PDF files and export to Excel (.xlsx) format
- **📁 Batch Processing**: Process multiple PDF folders simultaneously
- **🎯 Drag & Drop Interface**: Drag and drop folders for quick processing
- **📊 Progress Tracking**: Real-time progress bar and timer
- **🌍 Cross-Platform**: Runs on Windows, macOS, and Linux
- **🎨 User-Friendly GUI**: Modern Swing interface with intuitive design
- **🚀 Multiple Modes**: Automatic GUI/Console mode switching
- **⚡ High Performance**: Multi-threading for fast processing

## 🛠 Technologies Used

### Core Technologies
- **Java 21** - Programming language and runtime
- **Swing** - Desktop GUI framework  
- **Gradle 8.5** - Build tool and dependency management

### Libraries & Dependencies
- **Apache PDFBox 2.0.29** - PDF document processing and data extraction
- **Apache POI 5.2.5** - Excel file creation and manipulation
- **Logback + SLF4J** - Logging framework
- **Lombok** - Code generation and annotations

### Build & Packaging Tools
- **Gradle Application Plugin** - Creates executable distributions
- **jpackage** - Creates native installers (.exe, .dmg, .msi)
- **GraalVM Native Image** - Creates native executables
- **Gradle Wrapper** - Ensures consistent Gradle version

## 📁 Project Structure

```
pdf-convertor/
├── src/
│   ├── main/
│   │   ├── java/com/omori/pdfconvertor/
│   │   │   ├── Main.java                    # Main entry point
│   │   │   ├── SwingMain.java               # GUI Swing application
│   │   │   ├── HeadlessMain.java            # Console mode application
│   │   │   └── service/
│   │   │       └── PDFToExcelService.java   # PDF conversion service
│   │   ├── scripts/
│   │   │   ├── PDFConverter                 # Linux/Mac launcher script
│   │   │   └── PDFConverter.bat            # Windows launcher script
│   │   └── resources/
│   │       └── META-INF/                    # Native image configuration
├── build.gradle                            # Gradle build configuration
├── gradle.properties                       # Gradle properties
├── build-windows.bat                       # Windows build script
├── build-windows.ps1                      # PowerShell build script
├── BUILD_GUIDE.md                         # Detailed build instructions
└── README.md                              # This file
```

## 🔧 System Requirements

### To Build from Source Code
- **JDK 17+** (Eclipse Temurin or Oracle JDK recommended)
- **Gradle 8.0+** (or use included Gradle wrapper)
- **Git** to clone repository

### To Run Application
- **No JVM required** (for .dmg/.exe/.msi files with embedded runtime)
- **Or JVM 17+** (for .zip distribution)

## 🚀 Installation & Usage

### 📥 Download Pre-built Releases

Download pre-built files from the [Releases page]:

| Platform | File | Size | JVM Required | Description |
|----------|------|------|--------------|-------------|
| **Universal** | `PDFConverter-1.0-SNAPSHOT.zip` | ~22MB | ✅ | Runs on any platform with JVM |
| **macOS** | `PDFConverter-1.0.0.dmg` | ~150MB | ❌ | macOS installer with embedded JVM |
| **macOS Native** | `PDFConverter-Native-1.0.0.dmg` | ~65MB | ❌ | GraalVM native, fastest startup |
| **Windows** | `PDFConverter-1.0.0.exe` | ~150MB | ❌ | Windows installer with embedded JVM |
| **Windows** | `PDFConverter-1.0.0.msi` | ~150MB | ❌ | Windows MSI package |

### 💻 How to Use

#### macOS
```bash
# Download and install .dmg file
# Double-click PDFConverter-1.0.0.dmg
# Drag to Applications folder
# Launch from Applications or Spotlight
```

#### Windows  
```cmd
# Download and run .exe or .msi file
# Double-click to install
# Shortcuts will be created on Desktop and Start Menu
# No Java installation required
```

#### Universal ZIP (All Platforms)
```bash
# Extract zip file
unzip PDFConverter-1.0-SNAPSHOT.zip
cd PDFConverter-1.0-SNAPSHOT

# Run on Linux/macOS
./bin/PDFConverter

# Run on Windows
bin\PDFConverter.bat
```

## 🏗️ Building from Source Code

### Quick Start
```bash
# Clone repository
git clone https://github.com/yourusername/pdf-convertor.git
cd pdf-convertor

# Build all distributions for current platform
./gradlew createAllPlatformDistributions
```

### Platform-specific Builds

#### macOS
```bash
# DMG with embedded JVM
./gradlew createDMGWithRuntime

# Native DMG (GraalVM)
./gradlew createNativeDMG

# Build all macOS distributions
./gradlew createFullDistribution
```

#### Windows
```cmd
# Use provided script (recommended)
build-windows.bat

# Or manual commands
gradlew.bat createWindowsEXE
gradlew.bat createWindowsMSI
```

#### Universal ZIP
```bash
# Create ZIP that runs on any platform
./gradlew distZip
```

### Detailed Build Instructions
See [BUILD_GUIDE.md](BUILD_GUIDE.md) for comprehensive build process documentation.

## 📖 Usage Guide

### Main Interface

1. **Select Folders**: Click "Choose Folder" or drag and drop folders containing PDFs
2. **Track Progress**: Monitor progress bar and elapsed timer
3. **View Results**: Excel files will be created in the same directory as PDFs
4. **Batch Processing**: Can select multiple folders simultaneously

### Advanced Features

- **🎯 Smart Detection**: Automatically detects GUI/Console mode availability
- **🔄 Multi-threading**: Parallel processing of multiple files
- **📊 Progress Tracking**: Real-time progress with elapsed time display
- **🛡️ Error Handling**: Comprehensive error handling and recovery
- **📝 Logging**: Detailed logs for debugging and troubleshooting

### Command Line Mode
```bash
# Run with arguments to use headless mode
./bin/PDFConverter /path/to/pdf/folder

# Or with Java directly
java -jar lib/pdf-convertor-1.0-SNAPSHOT.jar /path/to/folder
```

## 🐛 Troubleshooting

### Common Issues

1. **"GUI not available"**
   - Application will automatically switch to console mode
   - Ensure DISPLAY environment is set (Linux)

2. **"Java not found"**
   ```bash
   # For ZIP distribution, need JVM 17+
   export JAVA_HOME=/path/to/jdk-17
   # Or use .dmg/.exe with embedded JVM
   ```

3. **Out of Memory with large PDFs**
   ```bash
   # Increase heap size
   export JAVA_OPTS="-Xmx4G"
   ./bin/PDFConverter
   ```

4. **Excel files not created**
   - Check directory permissions
   - Ensure PDFs are not password-protected
   - Review logs in status area

### Build Issues

1. **GraalVM native-image not found**
   ```bash
   # Install GraalVM and native-image
   sdk install java 21.0.1-graal
   gu install native-image
   ```

2. **Windows build fails on macOS/Linux**
   - Windows .exe/.msi can only be built on Windows machines
   - Use Universal ZIP for cross-platform distribution

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📊 Performance

| Build Type | File Size | Startup Time | Memory Usage |
|------------|-----------|--------------|--------------|
| Universal ZIP | ~22MB | Fast* | ~200MB |
| macOS DMG | ~150MB | Medium | ~300MB |
| Native DMG | ~65MB | Very Fast | ~150MB |
| Windows EXE/MSI | ~150MB | Medium | ~300MB |

*If JVM is already available

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📧 Support

For support, bug reports, or contributions:
- Create an issue on GitHub
- Email: nguyen.le.programmer@gmail.com

---

**🚀 Built with Java 21 & Swing | Optimized for Performance & User Experience**