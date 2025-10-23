# PDF Converter

A simple PDF to Excel converter with GUI and command-line interfaces. Extracts structured data from PDFs and exports to Excel format.

**Version:** 1.0-SNAPSHOT

## Features

- PDF to Excel conversion
- GUI and command-line modes
- Batch processing support
- Cross-platform (Windows, macOS, Linux)

## Quick Start

### Installation

**Universal JAR (All platforms with Java 21+)**
```bash
./gradlew distZip
unzip build/distributions/PDFConverter-1.0-SNAPSHOT.zip
cd PDFConverter-1.0-SNAPSHOT/bin
./PDFConverter
```

**Windows**
```bash
gradlew.bat createWindowsEXE
# Install the generated .exe file
```

**macOS**
```bash
./gradlew createDMGWithRuntime
# Open the generated .dmg file
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

## Requirements

- JDK 21+
- Gradle 8.0+ (or use wrapper)

## License

MIT License