# PDF Converter - Windows Setup Guide

## Quick Start (For End Users)

1. **Download and Install Java 17+**
   - Go to: https://www.oracle.com/java/technologies/javase-downloads.html
   - Download and install Java 17 or later

2. **Download the Project**
   - Download the entire project folder
   - Extract to a location like `C:\pdf-converter\`

3. **Build the Application**
   - Open Command Prompt as Administrator
   - Navigate to the project folder: `cd C:\pdf-converter`
   - Run: `mvnw.cmd clean package`

4. **Run the Application**
   - **Option 1**: Double-click `PDF-Converter.bat`
   - **Option 2**: Run `run-pdf-converter.bat` from Command Prompt

## What Each File Does

### `run-pdf-converter.bat`
- Main launcher script with error checking
- Automatically detects Java version and JavaFX dependencies
- Shows detailed error messages if something goes wrong

### `PDF-Converter.bat` 
- Simple double-click launcher for end users
- Nice interface with title bar
- Calls the main launcher script

### `mvnw.cmd`
- Maven wrapper for Windows
- Downloads Maven automatically if not installed
- Use this to build the project: `mvnw.cmd clean package`

## Build Commands

```cmd
# Clean and build the project
mvnw.cmd clean package

# Run during development
mvnw.cmd javafx:run

# Build Windows installer (requires WiX toolset)
mvnw.cmd clean package jpackage:jpackage -P windows-exe
```

## Troubleshooting

### "Java is not installed or not in PATH"
- Install Java 17+ from Oracle
- Make sure Java is added to your Windows PATH

### "JavaFX dependencies not found"
- Run `mvnw.cmd compile` first to download dependencies
- Make sure internet connection is available

### Application won't start
- Check that you have Java 17 or later
- Try running `mvnw.cmd javafx:run` to test
- Look for error messages in the console

## System Requirements

- **Operating System**: Windows 10/11
- **Java**: Version 17 or later
- **Memory**: 2GB RAM minimum
- **Disk Space**: 500MB for dependencies

## Distribution

To distribute your application:

1. **Zip Distribution** (Easiest):
   - Build the project: `mvnw.cmd clean package`
   - Zip the entire folder
   - Users extract and double-click `PDF-Converter.bat`

2. **Windows Installer** (Advanced):
   - Install WiX Toolset v3.11
   - Run: `mvnw.cmd clean package jpackage:jpackage -P windows-exe`
   - Installer will be in `target\dist\`

## Development

For developers working on this project:

```cmd
# Run in development mode
mvnw.cmd javafx:run

# Compile only
mvnw.cmd compile

# Clean everything
mvnw.cmd clean

# Run tests
mvnw.cmd test
```