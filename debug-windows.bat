@echo off
REM Debug script to test step by step on Windows

echo === PDF Converter Debug Script ===
echo.

set SCRIPT_DIR=%~dp0
set JAR_FILE=%SCRIPT_DIR%target\pdf_convert-1.0-SNAPSHOT.jar

echo 1. Script directory: %SCRIPT_DIR%
echo 2. JAR file path: %JAR_FILE%
echo.

REM Check if JAR file exists
if exist "%JAR_FILE%" (
    echo 3. ✓ JAR file found
) else (
    echo 3. ✗ JAR file NOT found
    echo Please run: mvnw.cmd clean package
    pause
    exit /b 1
)

echo.

REM Check Java
java -version
if %ERRORLEVEL% neq 0 (
    echo 4. ✗ Java not found
    pause
    exit /b 1
) else (
    echo 4. ✓ Java found
)

echo.

REM Check Maven repository
set MAVEN_REPO=%USERPROFILE%\.m2\repository
echo 5. Maven repo: %MAVEN_REPO%
if exist "%MAVEN_REPO%" (
    echo   ✓ Maven repository found
) else (
    echo   ✗ Maven repository NOT found
    pause
    exit /b 1
)

echo.

REM Check JavaFX JARs
set JAVAFX_VERSION=17.0.13
set JAVAFX_PLATFORM=win

set JAVAFX_CONTROLS=%MAVEN_REPO%\org\openjfx\javafx-controls\%JAVAFX_VERSION%\javafx-controls-%JAVAFX_VERSION%-%JAVAFX_PLATFORM%.jar

echo 6. Looking for JavaFX at: %JAVAFX_CONTROLS%
if exist "%JAVAFX_CONTROLS%" (
    echo   ✓ JavaFX found
) else (
    echo   ✗ JavaFX NOT found
    echo   Please run: mvnw.cmd compile
    pause
    exit /b 1
)

echo.
echo 7. All checks passed! Now testing the application...
echo.

REM Set up module path
set JAVAFX_FXML=%MAVEN_REPO%\org\openjfx\javafx-fxml\%JAVAFX_VERSION%\javafx-fxml-%JAVAFX_VERSION%-%JAVAFX_PLATFORM%.jar
set JAVAFX_GRAPHICS=%MAVEN_REPO%\org\openjfx\javafx-graphics\%JAVAFX_VERSION%\javafx-graphics-%JAVAFX_VERSION%-%JAVAFX_PLATFORM%.jar
set JAVAFX_BASE=%MAVEN_REPO%\org\openjfx\javafx-base\%JAVAFX_VERSION%\javafx-base-%JAVAFX_VERSION%-%JAVAFX_PLATFORM%.jar

set MODULE_PATH=%JAVAFX_CONTROLS%;%JAVAFX_FXML%;%JAVAFX_GRAPHICS%;%JAVAFX_BASE%

echo Running command:
echo java --module-path "%MODULE_PATH%" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -cp "%JAR_FILE%" com.omori.pdfconvertor.Main
echo.

java ^
    --module-path "%MODULE_PATH%" ^
    --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base ^
    -cp "%JAR_FILE%" ^
    com.omori.pdfconvertor.Main

echo.
echo Application finished with exit code: %ERRORLEVEL%
pause