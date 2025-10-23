# ================================================================
# PDF Converter - Windows PowerShell Build Script
# ================================================================
# This script builds Windows .exe and .msi installers
# Requirements: JDK 21+ with jpackage tool
# ================================================================

Write-Host "🚀 PDF Converter Windows Build Script" -ForegroundColor Cyan
Write-Host "=======================================" -ForegroundColor Cyan

# Check if we're on Windows
if (-not $IsWindows -and $PSVersionTable.PSVersion.Major -ge 7) {
    Write-Host "❌ This script is for Windows only" -ForegroundColor Red
    exit 1
}

# Check for Java
Write-Host "📋 Checking Java installation..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "✅ Found Java: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Java not found! Please install JDK 21 or higher" -ForegroundColor Red
    Write-Host "   Download from: https://adoptium.net/" -ForegroundColor Yellow
    exit 1
}

# Check for jpackage
Write-Host "📋 Checking jpackage availability..." -ForegroundColor Yellow
try {
    $jpackageVersion = jpackage --version 2>&1
    Write-Host "✅ Found jpackage: $jpackageVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ jpackage not found! Please ensure you have JDK 21+ installed" -ForegroundColor Red
    Write-Host "   jpackage is included with JDK 17 and later" -ForegroundColor Yellow
    exit 1
}

# Clean and build
Write-Host "🧹 Cleaning previous builds..." -ForegroundColor Yellow
& .\gradlew.bat clean

Write-Host "🔨 Building fat JAR..." -ForegroundColor Yellow
& .\gradlew.bat fatJar
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Failed to build JAR" -ForegroundColor Red
    exit 1
}

# Create output directory
if (-not (Test-Path "build\distributions")) {
    New-Item -ItemType Directory -Path "build\distributions" -Force | Out-Null
}

Write-Host "📦 Building Windows EXE installer..." -ForegroundColor Yellow
& .\gradlew.bat createWindowsEXE
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Failed to build EXE" -ForegroundColor Red
    exit 1
}

Write-Host "📦 Building Windows MSI installer..." -ForegroundColor Yellow  
& .\gradlew.bat createWindowsMSI
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Failed to build MSI" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "✅ Windows build completed successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "📁 Output files (check build\distributions folder):" -ForegroundColor Cyan
Write-Host "   - PDFConverter-1.0.0.exe" -ForegroundColor White
Write-Host "   - PDFConverter-1.0.0.msi" -ForegroundColor White
Write-Host ""
Write-Host "🎯 Installation notes:" -ForegroundColor Cyan
Write-Host "   - .exe: Self-extracting installer with GUI" -ForegroundColor White
Write-Host "   - .msi: Windows Installer package" -ForegroundColor White
Write-Host "   - Both include JVM runtime (no Java required on target machine)" -ForegroundColor White
Write-Host "   - Creates Start Menu shortcuts and desktop icons" -ForegroundColor White
Write-Host ""