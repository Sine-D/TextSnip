package com.snap;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import java.io.InputStream;

public class App extends JFrame {
    private static final String CAPTURES_PATH = "captures";

    public App() {
        setupModernLook();
        setTitle("TextSnip");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(40, 40, 40));
        setResizable(false);

        // Header Panel
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(32, 32, 32));
        header.setBorder(new EmptyBorder(25, 30, 15, 30));

        JLabel titleLabel = new JLabel("TextSnip", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 24));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);

        // Main Toolbar
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(10, 30, 30, 30));

        JButton btnNew = createModernButton("New Snip", null, true);
        btnNew.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnNew.setMaximumSize(new Dimension(210, 45));
        btnNew.addActionListener(e -> startSnip());

        JLabel hintLabel = new JLabel("Extract text from any part of your screen");
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        hintLabel.setForeground(new Color(150, 150, 150));
        hintLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hintLabel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JLabel copyrightLabel = new JLabel("All Rights Reserved - Sineth Dinsara");
        copyrightLabel.setForeground(new Color(100, 100, 100));
        copyrightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        copyrightLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        mainContent.add(Box.createVerticalGlue());
        mainContent.add(btnNew);
        mainContent.add(hintLabel);
        mainContent.add(Box.createVerticalGlue());

        add(mainContent, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.add(copyrightLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setSize(450, 280);
        setLocationRelativeTo(null);
    }

    private void setupModernLook() {
        try {
            // Enable dark window title bar
            System.setProperty("flatlaf.useWindowDecorations", "true");
            UIManager.setLookAndFeel(new FlatDarkLaf());

            UIManager.put("Button.arc", 18);
            UIManager.put("Component.arc", 18);
            UIManager.put("TextComponent.arc", 18);
            UIManager.put("Panel.background", new Color(40, 40, 40));
            UIManager.put("Label.foreground", Color.WHITE);
            UIManager.put("TableHeader.background", new Color(45, 45, 45));
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }
    }

    private JButton createModernButton(String text, String iconPath, boolean primary) {
        JButton btn = new JButton(text);

        // Load and Scale Icon
        if (iconPath != null) {
            try {
                InputStream is = getClass().getResourceAsStream(iconPath);
                if (is != null) {
                    BufferedImage original = ImageIO.read(is);
                    Image scaled = original.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                    btn.setIcon(new ImageIcon(scaled));
                    btn.setIconTextGap(10);
                }
            } catch (Exception e) {
                System.err.println("Could not load icon: " + iconPath);
            }
        }

        btn.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 15));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (primary) {
            btn.setBackground(new Color(0, 103, 192));
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(new Color(60, 60, 60));
        }

        btn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));

        return btn;
    }

    private void startSnip() {
        setState(JFrame.ICONIFIED); // Minimize main window
        new Timer(300, e -> {
            ((Timer) e.getSource()).stop();
            SwingUtilities.invokeLater(() -> {
                ScreenOverlay overlay = new ScreenOverlay((region) -> {
                    try {
                        Robot robot = new Robot();
                        BufferedImage image = robot.createScreenCapture(region);
                        File savedFile = saveImage(image);
                        String extractedText = processImage(image);

                        // Open the preview window
                        SwingUtilities.invokeLater(() -> new SnipPreview(image, extractedText, savedFile));

                        setState(JFrame.NORMAL); // Restore main window
                    } catch (AWTException ex) {
                        JOptionPane.showMessageDialog(null, "Error capturing screen: " + ex.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                        setState(JFrame.NORMAL);
                    }
                });
                overlay.setVisible(true);
            });
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new App().setVisible(true);
        });
    }

    private static String processImage(BufferedImage image) {
        ITesseract tesseract = new Tesseract();

        try {
            File tessDataFolder = net.sourceforge.tess4j.util.LoadLibs.extractTessResources("tessdata");
            tesseract.setDatapath(tessDataFolder.getAbsolutePath());

            // Set high DPI and use LSTM
            tesseract.setVariable("user_defined_dpi", "300");
            tesseract.setOcrEngineMode(1);
            tesseract.setPageSegMode(3);

            BufferedImage processed = optimizeImageForOCR(image);
            String result = tesseract.doOCR(processed);

            if (result == null || result.trim().isEmpty()) {

                return tesseract.doOCR(image);
            }

            copyToClipboard(result);
            return result;
        } catch (Throwable e) {
            return "OCR Error: " + e.getMessage();
        }
    }

    private static BufferedImage optimizeImageForOCR(BufferedImage image) {

        int scale = 3;
        int w = image.getWidth() * scale;
        int h = image.getHeight() * scale;

        BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = scaled.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.drawImage(image, 0, 0, w, h, null);
        g2d.dispose();

        return scaled;
    }

    private static File saveImage(BufferedImage image) {
        File directory = new File(CAPTURES_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = "snip_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".png";
        File file = new File(directory, fileName);

        try {
            ImageIO.write(image, "png", file);
            return file;
        } catch (IOException e) {
            System.err.println("Could not save image: " + e.getMessage());
            return null;
        }
    }

    private static void copyToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }
}
