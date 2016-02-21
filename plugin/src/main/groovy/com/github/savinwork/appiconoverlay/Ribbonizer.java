package com.github.savinwork.appiconoverlay;

import com.android.build.gradle.api.ApplicationVariant;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Ribbonizer {

    final File inputFile;

    final File outputFile;

    final BufferedImage image;

    public Ribbonizer(File inputFile, File outputFile) throws IOException {
        this.inputFile = inputFile;
        this.outputFile = outputFile;

        image = ImageIO.read(inputFile);
    }

    public void save() throws IOException {
        outputFile.getParentFile().mkdirs();
        ImageIO.write(image, "png", outputFile);
    }

    public void process2(ApplicationVariant variant) {

        String gitBranch = variant.getVersionName();
        if (System.getenv("TRAVIS_BRANCH") != null) {
            gitBranch = System.getenv("TRAVIS_BRANCH");
        }
        gitBranch = gitBranch.replace("feature-", "").replace("fix-", "").replace("task-", "").replace("test-", "");

        String ver = "dev";
        if (System.getenv("TRAVIS_BUILD_NUMBER") != null) {
            ver = System.getenv("TRAVIS_BUILD_NUMBER").toUpperCase();
        }

        DefaultOverlayFilter filter = new DefaultOverlayFilter(gitBranch.toUpperCase(), ver);
        filter.accept(image);
    }
}