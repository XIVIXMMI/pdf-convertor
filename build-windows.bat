@echo off
setlocal EnableDelayedExpansion

REM ================================================================
REM PDF Converter - Windows Build Script
REM ================================================================
REM This script builds Windows .exe and .msi installers
REM Requirements: JDK 17+ with jpackage tool
REM ================================================================

echo 🚀 PDF Converter Windows Build Script
echo =======================================

REM Check if we're on Windows
if not "%OS%"=="Windows_NT" (
    echo ❌ This script is for Windows only
    pause
    exit /b 1
)

REM Check for Java
echo 📋 Checking Java installation...
java -version >nul 2>&1
if !errorlevel! neq 0 (
    echo ❌ Java not found! Please install JDK 17 or higher
    echo    Download from: https://adoptium.net/
    pause
    exit /b 1
)

REM Check for jpackage
echo 📋 Checking jpackage availability...
jpackage --version >nul 2>&1
if !errorlevel! neq 0 (
    echo ❌ jpackage not found! Please ensure you have JDK 17+ installed
    echo    jpackage is included with JDK 17 and later
    pause
    exit /b 1
)

echo ✅ Java and jpackage are available

REM Clean and build
echo 🧹 Cleaning previous builds...
call gradlew.bat clean

echo 🔨 Building fat JAR...
call gradlew.bat fatJar
if !errorlevel! neq 0 (
    echo ❌ Failed to build JAR
    pause
    exit /b 1
)

REM Create output directory
if not exist "build\distributions" mkdir "build\distributions"

echo 📦 Building Windows EXE installer...
call gradlew.bat createWindowsEXE
if !errorlevel! neq 0 (
    echo ❌ Failed to build EXE
    pause
    exit /b 1
)

echo 📦 Building Windows MSI installer...
call gradlew.bat createWindowsMSI
if !errorlevel! neq 0 (
    echo ❌ Failed to build MSI
    pause
    exit /b 1
)

echo.
echo ✅ Windows build completed successfully!
echo.
echo 📁 Output files:
echo    - build\distributions\PDFConverter-1.0.0.exe
echo    - build\distributions\PDFConverter-1.0.0.msi
echo.
echo 🎯 Installation notes:
echo    - .exe: Self-extracting installer with GUI
echo    - .msi: Windows Installer package
echo    - Both include JVM runtime (no Java required on target machine)
echo    - Creates Start Menu shortcuts and desktop icons
echo.
pause