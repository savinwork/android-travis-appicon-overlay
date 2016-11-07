package com.github.savinwork.appiconoverlay

import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariantOutput
import com.android.builder.model.SourceProvider
import com.google.common.collect.Lists
import groovy.io.FileType
import groovy.text.SimpleTemplateEngine
import groovy.util.slurpersupport.GPathResult
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.tasks.TaskAction
import org.xml.sax.SAXException

import javax.imageio.ImageIO
import javax.xml.parsers.ParserConfigurationException
import java.awt.image.BufferedImage

class AppIconOverlayTask extends DefaultTask {

    ApplicationVariant variant
    File outputDir

    static final String NAME = "appiconoverlay"

    @TaskAction
    public void run() {
        def t0 = System.currentTimeMillis()

        AppIconOverlayExtension config = project.appiconoverlay;

        String travisBranch = queryGit(project, "abbrev-ref");
        if (System.getenv("TRAVIS_BRANCH") != null) {
            travisBranch = System.getenv("TRAVIS_BRANCH");
        }
        travisBranch = travisBranch.replace("feature-", "")
                //.replace("hotfix-", "")
                //.replace("hotfixes-", "")
                //.replace("fix-", "")
                //.replace("fixes-", "")
                .replace("task-", "")
                .replace("test-", "");
        def formatBinding = [
                'branch': queryGit(project, "abbrev-ref"),
                'commit': queryGit(project, "short"),
                'build': variant.name,
                'flavorName': variant.flavorName,
                'TRAVIS_BRANCH': travisBranch,
                'TRAVIS_BUILD_NUMBER': System.getenv("TRAVIS_BUILD_NUMBER") ?: queryGit(project, "short")
        ]
        String header = new SimpleTemplateEngine().createTemplate(config.textFormat).make(formatBinding).toString().trim().toUpperCase()
        String footer = new SimpleTemplateEngine().createTemplate(config.footerTextFormat).make(formatBinding).toString().trim().toUpperCase()

        if (header == "" && footer == "") {
            info("overlay text is empty")
            info("task finished in ${System.currentTimeMillis() - t0}ms")
            return
        }

        // add launcher icon names
        def names = new HashSet<String>(config.getIconNames())
        for (BaseVariantOutput output: variant.outputs) {
            String icon = getLauncherIcon(output.processManifest.manifestOutputFile)
            if (!names.contains(icon))
                names.add(icon)
        }

        for (String n : names) {
            for (SourceProvider sourceProvider : variant.sourceSets) {
                for (File resDir : sourceProvider.resDirectories) {
                    if (resDir.compareTo(outputDir) != 0) {

                        info("task started name: $n, dir: $resDir, include: ${resourceFilePattern(n)}")
                        ConfigurableFileTree tree = project.fileTree(dir: resDir, include: resourceFilePattern(n));
                        for (File inputFile : tree.files) {
                            info("process $inputFile")

                            def basename = inputFile.name
                            def resType = inputFile.parentFile.name
                            def outputFile = new File(outputDir, "${resType}/${basename}")
                            outputFile.parentFile.mkdirs()

                            BufferedImage image = ImageIO.read(inputFile);
                            DefaultOverlayFilter filter = new DefaultOverlayFilter(config);
                            filter.apply(image, header, footer);
                            ImageIO.write(image, "png", outputFile);
                        }

                    }
                }
            }
        }

        info("task finished in ${System.currentTimeMillis() - t0}ms")
    }

    void info(String message) {
        log(project, message)
    }

    public static void log(Project project, String message) {
        println " -> [$NAME] $message"
        project.logger.info("[$NAME] $message")
    }

    public static String getLauncherIcon(File manifestFile)
            throws SAXException, ParserConfigurationException, IOException {

        if (manifestFile == null || manifestFile.isDirectory() || !manifestFile.exists()) {
            return "@mipmap/ic_launcher"
        }

        GPathResult manifestXml = new XmlSlurper().parse(manifestFile);
        GPathResult applicationNode = (GPathResult) manifestXml.getProperty("application");
        return String.valueOf(applicationNode.getProperty("@android:icon"));
    }

    public static String resourceFilePattern(String name) {
        if (name.startsWith("@")) {
            String[] pair = name.substring(1).split("/", 2);
            String baseResType = pair[0];
            String fileName = pair[1];
            if (fileName == null) {
                throw new IllegalArgumentException("Icon names does include resource types (e.g. drawable/ic_launcher): " + name);
            }
            return baseResType + "*/" + fileName + ".*";
        } else {
            return name;
        }
    }

    public static String queryGit(Project project, String command) {
        def args = ["git", "rev-parse", "--${command}", "HEAD"]
        //log(project, "executing git: ${args.join(' ')}")
        def git = args.execute(null, project.projectDir)
        git.waitFor()
        if (git.exitValue() != 0) {
            log(project, "git exited with a non-zero error code. Is there a .git directory?")
        }
        return git.in.text.trim()
    }
}