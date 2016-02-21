package com.github.savinwork.appiconoverlay

import com.android.build.gradle.api.ApplicationVariant
import com.android.builder.model.SourceProvider
import com.google.common.collect.Lists
import groovy.io.FileType
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.tasks.TaskAction

class AppIconOverlayTask extends DefaultTask {

    ApplicationVariant variant
    File manifestFile
    File outputDir
    File resourcesPath
    String buildVariant

    static final String NAME = "appiconoverlay"

    @TaskAction
    public void run() {
        def t0 = System.currentTimeMillis()

//        def names = new HashSet<String>(/*iconNames*/)
//        //names.addAll(launcherIconNames)
//        //names.add("@mipmap/ic_launcher")
//
//        //for (String n : names) {
//            for (SourceProvider sourceProvider : variant.sourceSets) {
//                for (File resDir : sourceProvider.resDirectories) {
//                    if (resDir.compareTo(outputDir) != 0) {
//
//                        info("task started name: $n, dir: $resDir, include: ${Resources.resourceFilePattern(n)}")
//                        ConfigurableFileTree tree = project.fileTree(dir: resDir, include: Resources.resourceFilePattern(n));
//                        for (File inputFile : tree.files) {
//                            info("process $inputFile")
//
//                            def basename = inputFile.name
//                            def resType = inputFile.parentFile.name
//                            def outputFile = new File(outputDir, "${resType}/${basename}")
//                            outputFile.parentFile.mkdirs()
//
//                            def ribbonizer = new Ribbonizer(inputFile, outputFile)
//                            ribbonizer.process2(variant)
//                            ribbonizer.save()
//                        }
//                    }
//                }
//            }
//        //}

        info("task finished in ${System.currentTimeMillis() - t0}ms")
    }

    void info(String message) {
        println " -> [$name] $message"
        project.logger.info("[$name] $message")
    }

    /**
     * Icon name to search for in the app drawable folders
     * if none can be found in the app manifest
     */
    static final String DEFAULT_ICON_NAME = "ic_launcher";

    /**
     * Retrieve the app icon from the application manifest
     *
     * @param manifestFile The file pointing to the AndroidManifest
     * @return The icon name specified in the {@code <application/ >} node
     */
    static String getIconName(File manifestFile) {
        if (manifestFile == null || manifestFile.isDirectory() || !manifestFile.exists()) {
            return null;
        }

        def manifestXml = new XmlSlurper().parse(manifestFile)
        def fileName = manifestXml?.application?.@'android:icon'?.text()
        return fileName ? fileName?.split("/")[1] : null
    }

    /**
     * Finds all icon files matching the icon specified in the given manifest.
     *
     * If no icon can be found in the manifest, a default of {@link AppIconOverlayTask#DEFAULT_ICON_NAME} will be used
     */
    static List<File> findIcons(File where, File manifest) {
        String iconName = getIconName(manifest) ?: DEFAULT_ICON_NAME
        List<File> result = Lists.newArrayList();

        where.eachDirMatch(~/^drawable.*|^mipmap.*/) { dir ->
            dir.eachFileMatch(FileType.FILES, ~"^${iconName}.*") { file ->
                result.add(file)
            }
        }

        return result
    }
}