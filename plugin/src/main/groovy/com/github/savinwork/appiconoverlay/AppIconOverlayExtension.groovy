package com.github.savinwork.appiconoverlay

import java.awt.Color

class AppIconOverlayExtension {

    public static String NAME = "appiconoverlay";

    public AppIconOverlayExtension() {
    }

    /**
     * Format string to be used to create the text in the overlay.
     * Note: Use single quotes, it's a GString.
     * The following variables are available:
     *     - $branch: name of git branch
     *     - $commit: short SHA1 of latest commit in current branch
     *     - $build: the name of the build variant ex. Debug
     */
    String textFormat = "HEADER" //'$build->$branch\n$commit'
    /* header text color [r, g, b, a] */
    int[] textColor = [0xFF, 0xFF, 0xFF, 0xFF]
    /* header background color [r, g, b, a] */
    int[] textBackColor = [0, 0, 0, 0xAF]
    /* header chars per line */
    public int maxCharsPerLine = 8
    /* header max width, percentage of image.width */
    public int maxWidth = 95
    /* header max height per line, percentage of image.height */
    public int maxHeight = 30

    /* footer text */
    String footerTextFormat = "FOOTER"
    /* footer text color [r, g, b, a] */
    int[] footerTextColor = [0xFF, 0xFF, 0xFF, 0xFF] //r, g, b, a
    /* footer background color [r, g, b, a] */
    int[] footerBackColor = [0, 0x5A, 0xB7, 0xAF]
    /* header chars per line */
    public int footerMaxCharsPerLine = 8
    /* footer max width, percentage of image.width */
    public int footerMaxWidth = 95
    /* footer max height per line, percentage of image.height */
    public int footerMaxHeight = 50

    public Color getTextColor() {
        return intArrayToColor(textColor) ?: WHITE
    }

    public Color getFooterTextColor() {
        return intArrayToColor(footerTextColor) ?: WHITE
    }

    public Color getBackColor() {
        return intArrayToColor(textBackColor) ?: TRANSPARENT
    }

    public Color getFooterBackColor() {
        return intArrayToColor(footerBackColor) ?: TRANSPARENT
    }

    private static Color intArrayToColor(int[] colorParts) {
        if (colorParts == null || colorParts.length != 4) {
            return null;
        } else {
            return new Color(colorParts[0], colorParts[1], colorParts[2], colorParts[3])
        }
    }

    Set<String> iconNames = new HashSet<>();
    public Set<String> getIconNames() {
        return iconNames;
    }

    /**
     * @param resNames Names of icons. For example "@drawable/ic_launcher", "@mipmap/icon"
     */
    public void setIconNames(Collection<String> resNames) {
        iconNames = new HashSet<>(resNames);
    }

    /**
     * @param resNames Names of icons. For example "@drawable/ic_launcher", "@mipmap/icon"
     */
    public void iconNames(Collection<String> resNames) {
        setIconNames(resNames);
    }

    /**
     * @param resNames Names of icons. For example "@drawable/ic_launcher", "@mipmap/icon"
     */
    public void iconNames(String... resNames) {
        setIconNames(Arrays.asList(resNames));
    }

    /**
     * @param resName A name of icons. For example "@drawable/ic_launcher", "@mipmap/icon"
     */
    public void iconName(String resName) {
        iconNames.add(resName);
    }
}