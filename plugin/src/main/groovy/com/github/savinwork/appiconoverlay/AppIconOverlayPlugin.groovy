package com.github.savinwork.appiconoverlay

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariantOutput
import com.android.builder.core.BuilderConstants
import groovy.transform.CompileStatic
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

// see http://www.gradle.org/docs/current/userguide/custom_plugins.html

@CompileStatic
public class AppIconOverlayPlugin implements Plugin<Project> {

    static {
        System.setProperty("java.awt.headless", "true")

        // workaround for an Android Studio issue
        try {
            Class.forName(System.getProperty("java.awt.graphicsenv"))
        } catch (ClassNotFoundException e) {
            System.err.println("[WARN] java.awt.graphicsenv: " + e)
            System.setProperty("java.awt.graphicsenv", "sun.awt.CGraphicsEnvironment")
        }
        try {
            Class.forName(System.getProperty("awt.toolkit"))
        } catch (ClassNotFoundException e) {
            System.err.println("[WARN] awt.toolkit: " + e)
            System.setProperty("awt.toolkit", "sun.lwawt.macosx.LWCToolkit")
        }
    }

    @Override
    void apply(Project project) {
        def log = project.logger

        project.extensions.create("appiconoverlay", AppIconOverlayExtension)

        def android = project.extensions.findByType(AppExtension)
        if (!android) {
            throw new Exception("Not an Android application; you forget `apply plugin: 'com.android.application'`?")
        }
        android.applicationVariants.all {ApplicationVariant appvariant ->
            /* skip release builds */
            if(appvariant.buildType.name.equals(BuilderConstants.RELEASE)) {
                log.debug("Skipping build type: ${appvariant.buildType.name}")
                return;
            }

            appvariant.outputs.each {BaseVariantOutput output ->
                /* set up overlay task */
                def overlayTask = project.task(type:AppIconOverlayTask, "${AppIconOverlayTask.NAME}${appvariant.name.capitalize()}") as AppIconOverlayTask
                overlayTask.variant = appvariant
                overlayTask.outputDir = new File(project.buildDir, "generated/${AppIconOverlayExtension.NAME}/res/${appvariant.name}")
                overlayTask.manifestFile = output.processManifest.manifestOutputFile
                overlayTask.resourcesPath = appvariant.mergeResources.outputDir
                overlayTask.buildVariant = "${appvariant.name.capitalize()}"

                /* hook overlay task into android build chain */
                overlayTask.dependsOn output.processManifest
                output.processResources.dependsOn overlayTask
            }
        }
    }

    /*
    @Override
    void apply(Project project) {

        // Register extension to allow users to customize
        project.extensions.create(AppIconOverlayExtension.NAME, AppIconOverlayExtension)

        project.afterEvaluate {
            println 'start AppIconOverlayPlugin'

            def android = project.extensions.findByType(AppExtension)
            if (!android) {
                throw new Exception(
                        "Not an Android application; you forget `apply plugin: 'com.android.application`?")
            }
            //def extension = project.extensions.findByType(AppIconOverlayExtension)

            def tasks = new ArrayList<Task>();

            DomainObjectSet<ApplicationVariant> variants = android.applicationVariants;
            for (ApplicationVariant variant : variants) {

                //skip master
                String gitBranch = "branch";
                if (System.getenv("TRAVIS_BRANCH") != null) {
                    gitBranch = System.getenv("TRAVIS_BRANCH");
                }
                if (gitBranch.compareTo("master") == 0) {
                    println "[app] skip because TRAVIS_BRANCH == 'master'"
                    break;
                }

                //add tasks
                println " -> [${AppIconOverlayExtension.NAME}] ${variant.name}"
                def generatedResDir = getGeneratedResDir(project, variant)
                android.sourceSets.findByName(variant.name).res.srcDir(generatedResDir)

                def name = "${AppIconOverlayTask.NAME}${variant.name.capitalize()}"
                def task = project.task(name, type: AppIconOverlayTask) as AppIconOverlayTask
                task.variant = variant
                task.outputDir = generatedResDir
                tasks.add(task)

                def generateResources = project.getTasksByName("generate${variant.name.capitalize()}Resources", false)
                for (Task t : generateResources) {
                    t.dependsOn(task)
                }
            }

            project.task(AppIconOverlayTask.NAME, dependsOn: tasks);
            println 'fin AppIconOverlayPlugin'
        }
    }
    */

    static File getGeneratedResDir(Project project, ApplicationVariant variant) {
        return new File(project.buildDir, "generated/${AppIconOverlayExtension.NAME}/res/${variant.name}")
    }
}
