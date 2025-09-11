@echo off
REM Simple PDF Converter Launcher
REM Double-click this file to run the PDF Converter

title PDF Converter
cd /d "%~dp0"

echo ========================================
echo    PDF Converter Application
echo ========================================
echo.

call run-pdf-converter.bat

echo.
echo Application has closed.
pause