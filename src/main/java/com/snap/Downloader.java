package com.snap;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Downloader {
    public static void main(String[] args) {
        String urlString = "https://github.com/tesseract-ocr/tessdata_fast/raw/main/eng.traineddata";
        String targetPath = "src/main/resources/tessdata/eng.traineddata";
        
        try {
            Files.createDirectories(Paths.get("src/main/resources/tessdata"));
            System.out.println("Downloading " + urlString + "...");
            try (BufferedInputStream in = new BufferedInputStream(new URL(urlString).openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(targetPath)) {
                byte dataBuffer[] = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            }
            System.out.println("Download complete: " + targetPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
