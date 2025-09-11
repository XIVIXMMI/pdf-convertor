@echo off
REM PDF Converter Launcher Script for Windows
REM This script runs the PDF converter application with proper JavaFX support

setlocal enabledelayedexpansion

set SCRIPT_DIR=%~dp0
set JAR_FILE=%SCRIPT_DIR%target\pdf_convert-1.0-SNAPSHOT.jar

echo Starting PDF Converter...
echo Using JAR: %JAR_FILE%

REM Check if Java is available
java -version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java 17 or later
    pause
    exit /b 1
)

REM Check Java version (simplified check)
for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VERSION_STRING=%%g
)
set JAVA_VERSION_STRING=%JAVA_VERSION_STRING:"=%
for /f "delims=. tokens=1" %%v in ("%JAVA_VERSION_STRING%") do set JAVA_MAJOR=%%v

if %JAVA_MAJOR% lss 17 (
    echo Error: Java 17 or later is required. Found Java %JAVA_MAJOR%
    pause
    exit /b 1
)

REM Detect Maven local repository
set MAVEN_REPO=%USERPROFILE%\.m2\repository
if not exist "%MAVEN_REPO%" (
    echo Error: Maven local repository not found at %MAVEN_REPO%
    echo Please run 'mvnw.cmd compile' first to download dependencies
    pause
    exit /b 1
)

REM JavaFX module path for Windows
set JAVAFX_VERSION=17.0.13
set JAVAFX_PLATFORM=win

REM Check processor architecture
if "%PROCESSOR_ARCHITECTURE%"=="AMD64" set JAVAFX_PLATFORM=win
if "%PROCESSOR_ARCHITECTURE%"=="x86" set JAVAFX_PLATFORM=win

set JAVAFX_CONTROLS=%MAVEN_REPO%\org\openjfx\javafx-controls\%JAVAFX_VERSION%\javafx-controls-%JAVAFX_VERSION%-%JAVAFX_PLATFORM%.jar
set JAVAFX_FXML=%MAVEN_REPO%\org\openjfx\javafx-fxml\%JAVAFX_VERSION%\javafx-fxml-%JAVAFX_VERSION%-%JAVAFX_PLATFORM%.jar
set JAVAFX_GRAPHICS=%MAVEN_REPO%\org\openjfx\javafx-graphics\%JAVAFX_VERSION%\javafx-graphics-%JAVAFX_VERSION%-%JAVAFX_PLATFORM%.jar
set JAVAFX_BASE=%MAVEN_REPO%\org\openjfx\javafx-base\%JAVAFX_VERSION%\javafx-base-%JAVAFX_VERSION%-%JAVAFX_PLATFORM%.jar

REM Check if JavaFX JARs exist
if not exist "%JAVAFX_CONTROLS%" (
    echo Error: JavaFX dependencies not found. Please run 'mvnw.cmd compile' first.
    echo Looking for: %JAVAFX_CONTROLS%
    pause
    exit /b 1
)

set MODULE_PATH=%JAVAFX_CONTROLS%;%JAVAFX_FXML%;%JAVAFX_GRAPHICS%;%JAVAFX_BASE%

echo Using JavaFX platform: %JAVAFX_PLATFORM%
echo.

REM Run the application with JavaFX module path
java ^
    --module-path "%MODULE_PATH%" ^
    --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base ^
    -Dfile.encoding=UTF-8 ^
    -Djava.awt.headless=false ^
    -cp "%JAR_FILE%" ^
    com.omori.pdfconvertor.Main ^
    %*

REM Pause to see any error messages
if %ERRORLEVEL% neq 0 (
    echo.
    echo Application exited with error code: %ERRORLEVEL%
    pause
)

endlocal