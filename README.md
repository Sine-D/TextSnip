# TextSnip ✂️🖼️

**TextSnip** is a simple, modern screen capture and Optical Character Recognition (OCR) application written in Java. It allows you to select any portion of your screen, capture the image, and automatically extract the text from it using the Tesseract OCR engine.

## ✨ Features
*   **Targeted Screen Capture:** Easily snip any section of your screen to capture text.
*   **Automatic OCR:** Uses the powerful Tess4J (Tesseract) engine to extract text from images.
*   **Image Optimization:** Built-in image scaling and enhancements to improve OCR accuracy on low-resolution screen captures.
*   **Clipboard Integration:** Extracted text is automatically copied to your clipboard so you can paste it immediately.
*   **Image Saving:** Automatically saves your snipped screen captures into a local `captures/` folder as `.png` files.
*   **Modern UI:** A clean, dark-themed user interface powered by FlatLaf.
*   **Portable Executable:** Can be packaged into a standalone `.exe` using Launch4j, bundled with a custom JRE via `jlink`.

## 🛠️ Built With
*   **Java 17** - Core programming language.
*   **Tess4J** - Java wrapper for the Tesseract OCR API.
*   **FlatLaf** - Modern open-source Look and Feel for Java Swing desktop applications.
*   **Maven** - Dependency management and building.
*   **Launch4j** - Used to wrap the Java application into a Windows executable (`.exe`).

## 📋 Prerequisites
To build and run the project from source, you will need:
*   [Java Development Kit (JDK) 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or higher
*   [Apache Maven](https://maven.apache.org/)

## 🚀 How to Build and Run

### 1. Build the Project
Open a terminal in the project's root directory and run:
```bash
mvn clean package
```
This will compile the application, download all dependencies, and create a fat JAR inside the `target/` directory.

### 2. Run from Source
You can run the application directly using Maven:
```bash
mvn exec:java -Dexec.mainClass="com.snap.App"
```

### 3. Generate the Executable (`.exe`)
The `pom.xml` is configured to automatically do the following during the `package` phase:
1. Build a "fat" executable `.jar` file using the Maven Shade Plugin.
2. Build a Windows executable `TextSnip.exe` in the `dist/` folder using Launch4j.
3. Bundle a minimized runtime (JRE) using `jlink` and place it in `dist/jre/`.

To distribute the app, simply share the `dist/` directory, which contains the `.exe` and the bundled JRE.

## 💡 Usage
1. Open **TextSnip**, and click the "New Snip" button.
2. The application will minimize, and your screen will freeze.
3. Click and drag the crosshair cursor to highlight the area containing the text you want to extract.
4. Release the mouse. The app will capture the area, save the image, extract the text, and copy it to your clipboard.
5. The Snip Preview window will pop up showing the image and the extracted text.

## 📄 License
All Rights Reserved - Sineth Dinsara.







