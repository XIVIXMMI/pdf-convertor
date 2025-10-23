# PDF Converter

A simple PDF to Excel converter with GUI and command-line interfaces. Extracts structured data from PDFs and exports to Excel format.

**Version:** 1.0.0

## Features

- PDF to Excel conversion
- GUI and command-line modes
- Batch processing support
- Cross-platform (Windows, macOS, Linux)

## Requirements

- **JDK 21+** (for building)
- Gradle 8.0+ (or use included wrapper)
- Pre-built installers include JVM runtime (no Java required)

## Installation

### Pre-built Installers (No JDK Required)

**Windows**
```cmd
# Option 1: Use build script (builds both EXE and MSI)
build-windows.bat

# Option 2: Use Gradle directly
.\gradlew createWindowsEXE
.\gradlew createWindowsMSI

# Output files (in build\distributions):
# - PDFConverter-1.0.0.exe
# - PDFConverter-1.0.0.msi
```

**macOS**
```bash
# Build with embedded JVM
./gradlew createDMGWithRuntime
# Output: PDFConverter-1.0.0.dmg

# Build native (smaller, faster)
./gradlew createNativeDMG
# Output: PDFConverter-Native-1.0.0.dmg
```

### Universal JAR (JDK 21+ Required)

```bash
./gradlew distZip
unzip build/distributions/PDFConverter-1.0-SNAPSHOT.zip
cd PDFConverter-1.0-SNAPSHOT/bin
./PDFConverter
```

## Usage

**GUI Mode**
- Launch the application
- Select folder containing PDFs
- Excel files are created in the same folder

**Command Line**
```bash
java -jar pdf-convertor-1.0-SNAPSHOT.jar /path/to/folder
```

## Building

```bash
# Build JAR
./gradlew build

# Create distribution
./gradlew distZip

# Run application
./gradlew run

# Build all (macOS)
./gradlew createFullDistribution
```

## Project Structure

```
src/main/java/com/omori/pdfconvertor/
├── Main.java                  # Entry point
├── SwingMain.java            # GUI interface
├── HeadlessMain.java         # Command-line interface
├── PDFToExcelService.java    # Main conversion service
├── PDFData.java              # Data model
└── RegexExtractor.java       # Text extraction utilities
```

## Business Logic

**TID00 Field Processing:**
- Checks if positions 3-4 of Terminal ID are "39"
- If yes: replaces with "00" (e.g., `12395678` → `12005678`)
- If no: keeps original Terminal ID

## Technologies

- Java 21
- Apache PDFBox (PDF processing)
- Apache POI (Excel generation)
- Swing (GUI)
- Gradle 8.5

## License

MIT License