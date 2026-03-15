package com.snap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ScreenOverlay extends JFrame {
    private Point startPoint;
    private Point endPoint;
    private Rectangle selection;
    private final SelectionCallback callback;

    public interface SelectionCallback {
        void onRegionSelected(Rectangle region);
    }

    private BufferedImage cleanScreenshot;
    private BufferedImage dimmedScreenshot;

    public ScreenOverlay(SelectionCallback callback) {
        this.callback = callback;

        // Take a screenshot of the entire screen
        captureScreenBackground();

        setUndecorated(true);
        setAlwaysOnTop(true);
        setType(Window.Type.UTILITY);

        // Make it full screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0, 0, screenSize.width, screenSize.height);
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

        OverlayPanel panel = new OverlayPanel();
        add(panel);

        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
                selection = new Rectangle(startPoint);
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                endPoint = e.getPoint();
                int x = Math.min(startPoint.x, endPoint.x);
                int y = Math.min(startPoint.y, endPoint.y);
                int width = Math.abs(startPoint.x - endPoint.x);
                int height = Math.abs(startPoint.y - endPoint.y);
                selection = new Rectangle(x, y, width, height);
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selection != null && selection.width > 5 && selection.height > 5) {
                    setVisible(false);
                    callback.onRegionSelected(selection);
                    dispose();
                }
            }
        };

        panel.addMouseListener(adapter);
        panel.addMouseMotionListener(adapter);
    }

    private void captureScreenBackground() {
        try {
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            this.cleanScreenshot = robot.createScreenCapture(screenRect);

            this.dimmedScreenshot = new BufferedImage(cleanScreenshot.getWidth(), cleanScreenshot.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g = dimmedScreenshot.createGraphics();
            g.drawImage(cleanScreenshot, 0, 0, null);
            g.setColor(new Color(0, 0, 0, 130));
            g.fillRect(0, 0, dimmedScreenshot.getWidth(), dimmedScreenshot.getHeight());
            g.dispose();

        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private class OverlayPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (dimmedScreenshot != null) {
                g2d.drawImage(dimmedScreenshot, 0, 0, null);
            } else {
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }

            if (selection != null && selection.width > 0 && selection.height > 0) {
                if (cleanScreenshot != null) {
                    BufferedImage subImage = cleanScreenshot.getSubimage(selection.x, selection.y, selection.width,
                            selection.height);
                    g2d.drawImage(subImage, selection.x, selection.y, null);
                }
                g2d.setColor(new Color(255, 255, 255, 200));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRect(selection.x, selection.y, selection.width, selection.height);

                g2d.setColor(new Color(0, 120, 215));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRect(selection.x - 1, selection.y - 1, selection.width + 2, selection.height + 2);
            }
        }
    }
}
