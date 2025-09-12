@echo off
setlocal EnableDelayedExpansion

REM PDF Converter Windows Launcher Script
REM Automatically detects Java and launches with GUI support

echo 🚀 Khởi động PDF Converter...

REM Get script directory
set "SCRIPT_DIR=%~dp0"
set "LIB_DIR=%SCRIPT_DIR%..\lib"

REM Find Java
set "JAVA_CMD="
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
    )
)

if not defined JAVA_CMD (
    where java >nul 2>&1
    if !errorlevel! == 0 (
        set "JAVA_CMD=java"
    )
)

if not defined JAVA_CMD (
    echo ❌ Java không được tìm thấy. Vui lòng cài đặt Java hoặc đặt JAVA_HOME.
    pause
    exit /b 1
)

REM Build classpath
set "CLASSPATH="
for %%f in ("%LIB_DIR%\*.jar") do (
    if defined CLASSPATH (
        set "CLASSPATH=!CLASSPATH!;%%f"
    ) else (
        set "CLASSPATH=%%f"
    )
)

REM Java options for GUI and encoding  
set "JAVA_OPTS=-Dfile.encoding=UTF-8 -Djava.awt.headless=false -Xmx2g -Xms512m"

echo 📱 GUI mode enabled (Windows)

REM Launch the application
"%JAVA_CMD%" %JAVA_OPTS% -cp "%CLASSPATH%" com.omori.pdfconvertor.Main %*