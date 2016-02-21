package com.github.savinwork.appiconoverlay;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class DefaultOverlayFilter {

    static final String OVERLAY_NAME = "default";

    String fontName = "Default";
    int fontStyle = Font.PLAIN;

    AppIconOverlayExtension config = new AppIconOverlayExtension();

    public DefaultOverlayFilter(AppIconOverlayExtension cfg) {
        this.config = cfg;
    }

    static int calcMaxSize(int total, int percent) {
        return (int) (total * percent / 100);
    }

    private void drawText(Graphics2D g, int ImageW, int ImageH, String text, Boolean asHeader) {

        if (text.isEmpty())
            return;

        int maxW = (asHeader) ? config.maxWidth : config.footerMaxWidth;
        int maxH = (asHeader) ? config.maxHeight : config.footerMaxHeight;
        Integer maxChars = (asHeader) ? config.maxCharsPerLine : config.footerMaxCharsPerLine;
        Color fontColor = (asHeader) ? config.getTextColor() : config.getFooterTextColor();
        Color backColor = (asHeader) ? config.getBackColor() : config.getFooterBackColor();

        String[] lines = (maxChars > 0) ? text.split("(?<=\\G.{" + maxChars.toString() + "})") : text.split("\\r?\\n");

        // get font and metrics
        FontRenderContext frc = new FontRenderContext(g.getTransform(), true, true);
        g.setFont(getFont(calcMaxSize(ImageW, maxW), calcMaxSize(ImageH, maxH), lines[0], frc));
        FontMetrics fm = g.getFontMetrics();

        Rectangle2D labelBounds = g.getFont().getStringBounds(lines[0], frc);
        int y = (asHeader) ? 0 : ImageH - (int) labelBounds.getHeight() * lines.length;
        int x = 0;

        for (String line : lines) {
            labelBounds = g.getFont().getStringBounds(line, frc);

            // background
            g.setColor(backColor);
            g.fillRect(x, y, ImageW, (int) labelBounds.getHeight());

            // text
            g.setColor(fontColor);
            g.drawString(line, (int) (ImageW - labelBounds.getWidth()) / 2, y + fm.getAscent());
            y += (int) labelBounds.getHeight();
        }
    }

    public void apply(BufferedImage image, String header, String footer) {
        int width = image.getWidth();
        int height = image.getHeight();

        Graphics2D g = (Graphics2D) image.getGraphics();
        try {
            //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            //g.setTransform(AffineTransform.getRotateInstance(Math.toRadians(-45)));

            drawText(g, width, height, header, true);
            drawText(g, width, height, footer, false);
        } finally {
            g.dispose();
        }
    }

    Font getFont(int maxLabelWidth, int maxLabelHeight, String str, FontRenderContext frc) {
        int max = 64;
        int min = 8;
        int x = max;
        int px = 0;
        int py = 0;

        do {
            Font font = new Font(fontName, fontStyle, x);
            Rectangle2D labelBounds = font.getStringBounds(str, frc);
            px = (int) labelBounds.getWidth();
            py = (int) labelBounds.getHeight();

            if (px > maxLabelWidth || py > maxLabelHeight) {
                x--;
            }
        } while ((px > maxLabelWidth || py > maxLabelHeight) && (x > min));

        return new Font(fontName, fontStyle, x);
    }
}