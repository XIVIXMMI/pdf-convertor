# PDF Converter

A cross-platform PDF to Excel converter with both GUI and command-line interfaces. Built with Java 21 and modern desktop technologies for high-performance document processing.

## Features

- **PDF to Excel Conversion**: Extract structured data from PDFs to Excel (.xlsx) format
- **Dual Interface**: Graphical user interface with automatic fallback to command-line mode
- **Batch Processing**: Process multiple PDF files and folders simultaneously
- **Cross-Platform**: Native executables for Windows, macOS, and universal JAR for all platforms
- **High Performance**: Multi-threaded processing with progress tracking
- **Smart Detection**: Automatically switches between GUI and headless modes based on environment

## Quick Start

### Pre-built Downloads

| Platform | File | Size | JVM Required |
|----------|------|------|--------------|
| **Universal** | `PDFConverter-1.0-SNAPSHOT.zip` | ~25MB | Yes (Java 17+) |
| **macOS** | `PDFConverter-1.0.0.dmg` | ~150MB | No |
| **macOS Native** | `PDFConverter-Native-1.0.0.dmg` | ~65MB | No |
| **Windows** | `PDFConverter-1.0.0.exe` | ~150MB | No |
| **Windows** | `PDFConverter-1.0.0.msi` | ~150MB | No |

### Installation

**macOS**
```bash
# Download .dmg file and install
open PDFConverter-1.0.0.dmg
# Drag to Applications folder
```

**Windows**
```cmd
# Run installer (creates desktop shortcut and start menu entry)
PDFConverter-1.0.0.exe
```

**Universal (All Platforms)**
```bash
# Extract and run
unzip PDFConverter-1.0-SNAPSHOT.zip
cd PDFConverter-1.0-SNAPSHOT

# Linux/macOS
./bin/PDFConverter

# Windows
bin\PDFConverter.bat
```

## Usage

### GUI Mode
1. Launch the application
2. Click "Choose Folder" or drag-and-drop folders containing PDFs
3. Monitor conversion progress with the built-in progress bar
4. Excel files are created in the same directories as the source PDFs

### Command Line Mode
```bash
# Convert PDFs in a specific folder
./bin/PDFConverter /path/to/pdf/folder

# Multiple folders
./bin/PDFConverter /folder1 /folder2 /folder3

# With Java directly
java -jar lib/pdf-convertor-1.0-SNAPSHOT.jar /path/to/folder
```

## Building from Source

### System Requirements
- **JDK 21** (Eclipse Temurin recommended)
- **Gradle 8.0+** (or use included wrapper)
- **GraalVM 21** (for native builds)

### Build Commands

```bash
# Clone repository
git clone <repository-url>
cd pdf-convertor

# Build all distributions for current platform
./gradlew createAllPlatformDistributions

# Platform-specific builds
./gradlew createDMGWithRuntime        # macOS with JVM
./gradlew createNativeDMG             # macOS native
./gradlew createWindowsEXE            # Windows executable
./gradlew createWindowsMSI            # Windows installer
./gradlew distZip                     # Universal ZIP
```

### Complete Build (macOS)
```bash
# Build all macOS distributions
./gradlew createFullDistribution
```

### Windows Build (on Windows)
```cmd
# Use provided script
build-windows.bat

# Or manual
gradlew.bat createWindowsEXE
gradlew.bat createWindowsMSI
```

## Project Structure

```
pdf-convertor/
├── src/main/java/com/omori/pdfconvertor/
│   ├── Main.java                     # Entry point with mode detection
│   ├── SwingMain.java               # GUI application
│   ├── HeadlessMain.java            # Command-line interface
│   ├── model/
│   │   └── PDFData.java             # Data model
│   ├── service/
│   │   ├── PDFService.java          # PDF processing
│   │   ├── ExcelService.java        # Excel generation
│   │   └── PDFToExcelService.java   # Main conversion service
│   ├── util/
│   │   └── RegexExtractor.java      # Text extraction utilities
│   └── exception/
│       └── PDFProcessingException.java
├── src/main/scripts/                # Platform launcher scripts
├── build.gradle                     # Gradle build configuration
├── build-windows.bat               # Windows build script
└── BUILD_GUIDE.md                  # Detailed build instructions
```

## Technologies

### Core
- **Java 21** - Runtime and language features
- **Swing** - Cross-platform GUI framework
- **Gradle 8.5** - Build automation

### Libraries
- **Apache PDFBox 2.0.29** - PDF processing
- **Apache POI 5.2.5** - Excel file generation
- **Logback + SLF4J** - Logging framework
- **Lombok 1.18.30** - Code generation

### Build Tools
- **jpackage** - Native installers with embedded JVM
- **GraalVM Native Image** - Ahead-of-time compilation
- **Gradle Application Plugin** - Distribution packaging

## Performance

| Build Type | Startup Time | Memory Usage | File Size |
|------------|--------------|--------------|-----------|
| Universal ZIP | Fast* | ~200MB | ~25MB |
| macOS DMG | Medium | ~300MB | ~150MB |
| Native DMG | Very Fast | ~150MB | ~65MB |
| Windows EXE/MSI | Medium | ~300MB | ~150MB |

*When JVM is already available

## Troubleshooting

### Common Issues

**"GUI not available"**
- Application automatically switches to console mode
- On Linux, ensure DISPLAY environment variable is set

**Out of memory with large PDFs**
```bash
export JAVA_OPTS="-Xmx4G"
./bin/PDFConverter
```

**Build failures**
```bash
# Ensure Java 21 is active
java -version

# For GraalVM native builds
sdk install java 21.0.1-graal
gu install native-image
```

### Platform-specific Notes

- **Windows .exe/.msi**: Can only be built on Windows machines
- **macOS .dmg**: Can only be built on macOS machines
- **Universal ZIP**: Builds on any platform, requires JVM to run

## Development

```bash
# Run in development mode
./gradlew run

# Run tests
./gradlew test

# Build fat JAR
./gradlew fatJar

# Native compilation
./gradlew nativeCompile
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit changes (`git commit -m 'Add new feature'`)
4. Push to branch (`git push origin feature/new-feature`)
5. Create a Pull Request

## License

This project is licensed under the MIT License.

## Support

- **GitHub Issues**: Report bugs and feature requests
- **Email**: nguyen.le.programmer@gmail.com

---

**Built with Java 21 | Cross-Platform Desktop Application**