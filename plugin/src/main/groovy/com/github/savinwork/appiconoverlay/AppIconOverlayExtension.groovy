package com.github.savinwork.appiconoverlay

import java.awt.Color

class AppIconOverlayExtension {

    public static String NAME = "appiconoverlay";

    int[] textColor = [255, 255, 255, 255]
    int[] textBackColor = [0, 0, 0, 255]
    int[] footerTextColor = [255, 255, 255, 255]
    int[] footerBackColor = [0, 0, 0, 255]

    public Color getFooterTextColor() {
        return intArrayToColor(textColor) ?: TRANSPARENT
    }

    public Color getTextColor() {
        return intArrayToColor(footerTextColor) ?: TRANSPARENT
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

    /**
     * Format string to be used to create the text in the overlay.
     * Note: Use single quotes, it's a GString.
     * The following variables are available:
     *     - $branch: name of git branch
     *     - $commit: short SHA1 of latest commit in current branch
     *     - $build: the name of the build variant ex. Debug
     */
    String format = '$build->$branch\n$commit'

    public AppIconOverlayExtension() {
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

//    List<FilterBuilder> filterBuilders = new ArrayList<>();
//    public List<FilterBuilder> getFilterBuilders() {
//        return filterBuilders;
//    }
//
//    public void setFilterBuilders(Collection<FilterBuilder> filterBuilders) {
//        this.filterBuilders = new ArrayList<>(filterBuilders);
//    }
//
//    public void builder(FilterBuilder filterBuilder)
//            throws IllegalAccessException, InstantiationException {
//        this.filterBuilders.clear();
//        this.filterBuilders.add(filterBuilder);
//    }
//
//    // utilities
//
//    public Consumer<BufferedImage> grayScaleFilter(ApplicationVariant variant, File iconFile) {
//        return new GrayScaleBuilder().apply(variant, iconFile);
//    }
//
//    public Consumer<BufferedImage> grayRibbonFilter(ApplicationVariant variant, File iconFile) {
//        return new GrayRibbonBuilder().apply(variant, iconFile);
//    }
//
//    public Consumer<BufferedImage> yellowRibbonFilter(ApplicationVariant variant, File iconFile) {
//        return new YellowRibbonBuilder().apply(variant, iconFile);
//    }
//
//    public Consumer<BufferedImage> greenRibbonFilter(ApplicationVariant variant, File iconFile) {
//        return new GreenRibbonBuilder().apply(variant, iconFile);
//    }
}