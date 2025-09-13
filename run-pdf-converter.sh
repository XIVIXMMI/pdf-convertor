#!/bin/bash

# PDF Converter Launcher Script
# This script runs the PDF converter application with proper JavaFX support

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAR_FILE="$SCRIPT_DIR/target/pdf_convert-1.0-SNAPSHOT.jar"

echo "Starting PDF Converter..."
echo "Using JAR: $JAR_FILE"

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    echo "Please install Java 17 or later"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "Error: Java 17 or later is required. Found Java $JAVA_VERSION"
    exit 1
fi

# Detect Maven local repository
MAVEN_REPO="$HOME/.m2/repository"
if [ ! -d "$MAVEN_REPO" ]; then
    echo "Error: Maven local repository not found at $MAVEN_REPO"
    echo "Please run 'mvn compile' first to download dependencies"
    exit 1
fi

# JavaFX module path for macOS ARM64
JAVAFX_VERSION="17.0.13"
JAVAFX_PLATFORM="mac-aarch64"

# Check if this is Intel Mac
if [[ $(uname -m) == "x86_64" ]]; then
    JAVAFX_PLATFORM="mac"
fi

JAVAFX_CONTROLS="$MAVEN_REPO/org/openjfx/javafx-controls/$JAVAFX_VERSION/javafx-controls-$JAVAFX_VERSION-$JAVAFX_PLATFORM.jar"
JAVAFX_FXML="$MAVEN_REPO/org/openjfx/javafx-fxml/$JAVAFX_VERSION/javafx-fxml-$JAVAFX_VERSION-$JAVAFX_PLATFORM.jar"
JAVAFX_GRAPHICS="$MAVEN_REPO/org/openjfx/javafx-graphics/$JAVAFX_VERSION/javafx-graphics-$JAVAFX_VERSION-$JAVAFX_PLATFORM.jar"
JAVAFX_BASE="$MAVEN_REPO/org/openjfx/javafx-base/$JAVAFX_VERSION/javafx-base-$JAVAFX_VERSION-$JAVAFX_PLATFORM.jar"

# Check if JavaFX JARs exist
if [ ! -f "$JAVAFX_CONTROLS" ]; then
    echo "Error: JavaFX dependencies not found. Please run 'mvn compile' first."
    exit 1
fi

MODULE_PATH="$JAVAFX_CONTROLS:$JAVAFX_FXML:$JAVAFX_GRAPHICS:$JAVAFX_BASE"

echo "Using JavaFX platform: $JAVAFX_PLATFORM"

# Run the application with JavaFX module path
exec java \
    --module-path "$MODULE_PATH" \
    --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base \
    -Dfile.encoding=UTF-8 \
    -Djava.awt.headless=false \
    -cp "$JAR_FILE" \
    com.omori.pdfconvertor.Main \
    "$@"