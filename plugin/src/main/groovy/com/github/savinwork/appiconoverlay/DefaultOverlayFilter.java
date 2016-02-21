package com.github.savinwork.appiconoverlay;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class DefaultOverlayFilter {

    String label;
    String ver;

    String fontName = "Default";

    int fontStyle = Font.PLAIN;

    public DefaultOverlayFilter(String label, String ver) {
        this.label = label;
        this.ver = ver;
    }

    static int calculateMaxLabelWidth(int w, int percent) {
        return (int) (w * percent / 100);
    }

    public void accept(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        Graphics2D g = (Graphics2D) image.getGraphics();

        //g.setTransform(AffineTransform.getRotateInstance(Math.toRadians(-45)));

        int y = 0; //height / 4;
        int x = 0; //-width

        if (label.isEmpty()) {
            label = "EMPTY";
        }
        String[] lines = label.split("(?<=\\G.{8})");

        // calculate the rectangle where the label is rendered
        FontRenderContext frc = new FontRenderContext(g.getTransform(), true, true);
        //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(getFont(calculateMaxLabelWidth(width, 95), (int)height/3, lines[0], frc));
        FontMetrics fm = g.getFontMetrics();

        for (String line : lines) {
            Rectangle2D labelBounds = g.getFont().getStringBounds(line, frc);

            // draw the ribbon
            g.setColor(new Color(0, 0, 0, 0xFF));
            g.fillRect(x, y, width, (int) labelBounds.getHeight());

            // draw the label
            g.setColor(Color.WHITE);
            g.drawString(line,
                    (int) (width - labelBounds.getWidth()) / 2,
                    y + fm.getAscent());
            y += (int) labelBounds.getHeight();
        }

        //draw version
        if (ver.compareTo("dev") != 0) {
            g.setFont(getFont(calculateMaxLabelWidth(width, 50), (int)height/2, ver, frc));
            fm = g.getFontMetrics();
            Rectangle2D labelBounds = g.getFont().getStringBounds(ver, frc);
            y = height - (int) labelBounds.getHeight();

            // draw the ribbon
            g.setColor(new Color(0, 0x5A, 0xB7, 0xFF));
            g.fillRect(x, y, width, (int) labelBounds.getHeight());

            // draw the label
            g.setColor(Color.WHITE);
            g.drawString(ver,
                    (int) (width - labelBounds.getWidth()) / 2,
                    y + fm.getAscent());
        }

        g.dispose();
    }

    Font getFont(int maxLabelWidth, int maxLabelHeight, String str, FontRenderContext frc) {
        int max = 64;
        int min = 6;
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