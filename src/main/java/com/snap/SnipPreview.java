package com.snap;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.File;

public class SnipPreview extends JFrame {
    private final BufferedImage image;
    private final String extractedText;

    public SnipPreview(BufferedImage image, String text, File file) {
        this.image = image;
        this.extractedText = (text != null) ? text.trim() : "";

        setTitle("TextSnip");
        setMinimumSize(new Dimension(650, 450));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Use FlatLaf properties
        getRootPane().putClientProperty(FlatClientProperties.TITLE_BAR_SHOW_ICON, false);
        getRootPane().putClientProperty(FlatClientProperties.TITLE_BAR_SHOW_TITLE, false);
        getRootPane().putClientProperty(FlatClientProperties.TITLE_BAR_BACKGROUND, new Color(32, 32, 32));
        getRootPane().putClientProperty(FlatClientProperties.TITLE_BAR_FOREGROUND, Color.WHITE);

        // Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.EAST);

        // Main Content - Image Preview
        JPanel mainContent = createMainContent();
        add(mainContent, BorderLayout.CENTER);

        pack();
        setSize(700, 480);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(new Color(40, 40, 40));
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(50, 50, 50)));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Resolution Info
        JLabel resTitle = new JLabel("IMAGE DETAILS");
        resTitle.setFont(new Font("Segoe UI Bold", Font.PLAIN, 11));
        resTitle.setForeground(new Color(150, 150, 150));
        resTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel resValue = new JLabel(image.getWidth() + " × " + image.getHeight() + " px");
        resValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resValue.setForeground(new Color(200, 200, 200));
        resValue.setAlignmentX(Component.LEFT_ALIGNMENT);
        resValue.setBorder(new EmptyBorder(5, 0, 25, 0));

        // OCR Result Section
        JLabel ocrTitle = new JLabel("DETECTED TEXT");
        ocrTitle.setFont(new Font("Segoe UI Bold", Font.PLAIN, 11));
        ocrTitle.setForeground(new Color(150, 150, 150));
        ocrTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea textArea = new JTextArea(extractedText);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setBackground(new Color(30, 30, 30));
        textArea.setForeground(new Color(230, 230, 230));
        textArea.setMargin(new Insets(12, 12, 12, 12));
        textArea.putClientProperty(FlatClientProperties.STYLE, "arc: 12");

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1, true));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        // Copy Text Button for Sidebar
        JButton btnCopyText = createStyledButton("Copy Detected Text", null, true);
        btnCopyText.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCopyText.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btnCopyText.addActionListener(e -> {
            StringSelection selection = new StringSelection(extractedText);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
            btnCopyText.setEnabled(false);
            btnCopyText.setText("Copied!");
        });

        content.add(resTitle);
        content.add(resValue);
        content.add(ocrTitle);
        content.add(Box.createVerticalStrut(10));
        content.add(scrollPane);
        content.add(Box.createVerticalStrut(20));
        content.add(btnCopyText);

        sidebar.add(content, BorderLayout.NORTH);
        return sidebar;
    }

    private JPanel createMainContent() {
        JPanel canvas = new JPanel(new GridBagLayout());
        canvas.setBackground(new Color(25, 25, 25));

        JLabel imageLabel = new JLabel(new ImageIcon(image));
        imageLabel.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1));

        // Add to a container
        JPanel imageContainer = new JPanel(new GridBagLayout());
        imageContainer.setOpaque(false);
        imageContainer.setBorder(new EmptyBorder(25, 25, 25, 25));
        imageContainer.add(imageLabel);

        JScrollPane scrollPane = new JScrollPane(imageContainer);
        scrollPane.setBorder(null);
        scrollPane.setBackground(new Color(25, 25, 25));
        scrollPane.getViewport().setBackground(new Color(25, 25, 25));

        // Use a layout that fills the center
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    private JButton createStyledButton(String text, String icon, boolean primary) {
        String label = (icon != null) ? icon + "  " + text : text;
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);

        if (primary) {
            btn.setBackground(new Color(0, 120, 215));
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(new Color(55, 55, 55));
            btn.setForeground(new Color(230, 230, 230));
        }

        btn.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (primary)
                    btn.setBackground(new Color(0, 140, 240));
                else
                    btn.setBackground(new Color(70, 70, 70));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (primary)
                    btn.setBackground(new Color(0, 120, 215));
                else
                    btn.setBackground(new Color(55, 55, 55));
            }
        });

        return btn;
    }

}
