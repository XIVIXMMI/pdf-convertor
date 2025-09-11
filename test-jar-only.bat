@echo off
REM Test just the JAR file without JavaFX to see the exact error

echo Testing JAR file directly...
echo.

set SCRIPT_DIR=%~dp0
set JAR_FILE=%SCRIPT_DIR%target\pdf_convert-1.0-SNAPSHOT.jar

echo JAR file: %JAR_FILE%
echo.

if not exist "%JAR_FILE%" (
    echo ERROR: JAR file not found!
    echo You need to build the project first:
    echo   mvnw.cmd clean package
    echo.
    pause
    exit /b 1
)

echo Running: java -jar "%JAR_FILE%"
echo.

java -jar "%JAR_FILE%"

echo.
echo Exit code: %ERRORLEVEL%
pause