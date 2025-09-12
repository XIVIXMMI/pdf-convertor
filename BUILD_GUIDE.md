# 🏗️ PDF Converter - Build Guide

This guide explains how to build PDF Converter for different platforms.

## 🎯 Available Build Targets

| Platform | File Type | Size | JVM Required | Build Command |
|----------|-----------|------|--------------|---------------|
| **Universal** | `.zip` | ~22MB | ✅ Yes | `./gradlew distZip` |
| **macOS** | `.dmg` | ~150MB | ❌ No | `./gradlew createDMGWithRuntime` |
| **macOS Native** | `.dmg` | ~65MB | ❌ No | `./gradlew createNativeDMG` |
| **Windows** | `.exe` | ~150MB | ❌ No | `./gradlew createWindowsEXE` |
| **Windows** | `.msi` | ~150MB | ❌ No | `./gradlew createWindowsMSI` |

## 🔧 Prerequisites

### For All Platforms
- **JDK 17 or higher** (with jpackage tool included)
- **Gradle** (use included gradlew)

### For macOS Native (.dmg with GraalVM)
- **GraalVM 21** or higher with native-image tool

### For Windows (.exe/.msi)
- **Windows machine** (jpackage creates platform-specific installers)
- **Windows SDK** (automatically used by jpackage)

## 📦 Build Commands

### Universal ZIP (Cross-platform)
```bash
# Works on any platform, requires JVM on target machine
./gradlew distZip
```
**Output:** `build/distributions/PDFConverter-1.0-SNAPSHOT.zip`

### macOS Builds
```bash
# DMG with embedded JVM (no JVM required on target Mac)
./gradlew createDMGWithRuntime

# Native DMG using GraalVM (smallest, fastest)
./gradlew createNativeDMG

# Build both macOS versions
./gradlew createFullDistribution
```
**Outputs:** 
- `build/distributions/PDFConverter-1.0.0.dmg`
- `build/distributions/PDFConverter-Native-1.0.0.dmg`

### Windows Builds
```bash
# EXE installer (on Windows machine)
./gradlew createWindowsEXE

# MSI installer (on Windows machine)  
./gradlew createWindowsMSI

# Use provided batch scripts
build-windows.bat          # Command Prompt
build-windows.ps1          # PowerShell
```
**Outputs:**
- `build/distributions/PDFConverter-1.0.0.exe`
- `build/distributions/PDFConverter-1.0.0.msi`

### Auto-detect Platform
```bash
# Automatically builds for current platform
./gradlew createAllPlatformDistributions
```

## 🚀 Quick Start

### For macOS Users
```bash
./gradlew createFullDistribution
```

### For Windows Users
```cmd
REM Option 1: Use batch script
build-windows.bat

REM Option 2: Use PowerShell script  
powershell -ExecutionPolicy Bypass -File build-windows.ps1

REM Option 3: Manual commands
gradlew.bat createWindowsEXE
gradlew.bat createWindowsMSI
```

## 📁 Output Directory Structure

```
build/
├── distributions/
│   ├── PDFConverter-1.0-SNAPSHOT.zip     # Universal ZIP
│   ├── PDFConverter-1.0.0.dmg            # macOS DMG with JVM
│   ├── PDFConverter-Native-1.0.0.dmg     # macOS Native DMG
│   ├── PDFConverter-1.0.0.exe            # Windows EXE installer
│   └── PDFConverter-1.0.0.msi            # Windows MSI installer
└── libs/
    └── pdf-convertor-1.0-SNAPSHOT-fat.jar # Fat JAR (all dependencies)
```

## 🎯 Installation & Usage

### Universal ZIP
```bash
# Extract and run
unzip PDFConverter-1.0-SNAPSHOT.zip
cd PDFConverter-1.0-SNAPSHOT
./bin/PDFConverter              # Linux/Mac
bin\PDFConverter.bat           # Windows
```

### macOS DMG
```bash
# Double-click to mount, drag to Applications
# Run from Applications or Spotlight search
```

### Windows EXE/MSI
```cmd
# Double-click to install
# Creates Start Menu shortcut and desktop icon
# No Java required on target machine
```

## 🔍 Troubleshooting

### "Java not found" error
- Install JDK 17+ from [Adoptium](https://adoptium.net/)
- Set `JAVA_HOME` environment variable

### "jpackage command not found"
- Ensure you have JDK 17+ (not just JRE)
- jpackage is included with JDK 17 and later

### GraalVM native-image not found
- Install GraalVM: `sdk install java 21.0.1-graal`
- Install native-image: `gu install native-image`

### Windows build fails on macOS/Linux
- Windows .exe/.msi can only be built on Windows machines
- Use the Universal ZIP for cross-platform distribution

## 🎨 GUI Features

All build targets include full GUI support:
- **Swing-based interface** with drag & drop
- **Progress tracking** with real-time updates
- **Multi-folder processing** with threading
- **Cross-platform look and feel**
- **Automatic GUI/console mode detection**

## 📊 Size Comparison

| Build Type | Compressed | Installed | Startup Time |
|------------|------------|-----------|--------------|
| Universal ZIP | ~22MB | ~35MB | Fast (if JVM present) |
| macOS DMG | ~150MB | ~200MB | Medium |
| Native DMG | ~65MB | ~80MB | Very Fast |
| Windows EXE/MSI | ~150MB | ~200MB | Medium |

Choose based on your distribution needs:
- **Universal ZIP**: Smallest, requires Java
- **Platform DMG/EXE**: Larger, no Java required
- **Native DMG**: Best performance, macOS only