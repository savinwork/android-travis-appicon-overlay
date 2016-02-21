package com.github.savinwork.appiconoverlay

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import groovy.transform.CompileStatic
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

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

        // Register extension to allow users to customize
        project.extensions.create(AppIconOverlayExtension.NAME, AppIconOverlayExtension)

        project.afterEvaluate {
            AppIconOverlayTask.log(project, 'start AppIconOverlayPlugin')

            def android = project.extensions.findByType(AppExtension)
            if (!android) {
                throw new Exception("Not an Android application; you forget `apply plugin: 'com.android.application`?")
            }

            def tasks = new ArrayList<Task>();
            DomainObjectSet<ApplicationVariant> variants = android.applicationVariants;
            for (ApplicationVariant variant : variants) {

//                //skip master
//                String gitBranch = "branch";
//                if (System.getenv("TRAVIS_BRANCH") != null) {
//                    gitBranch = System.getenv("TRAVIS_BRANCH");
//                }
//                if (gitBranch.compareTo("master") == 0) {
//                    AppIconOverlayTask.log(project, "[app] skip because TRAVIS_BRANCH == 'master'")
//                    break;
//                }

                //add tasks
                AppIconOverlayTask.log(project, " -> [${AppIconOverlayExtension.NAME}] ${variant.name}")

                def outputDir = new File(project.buildDir, "generated/${AppIconOverlayExtension.NAME}/res/${variant.name}")
                android.sourceSets.findByName(variant.name).res.srcDir(outputDir) //add source dir

                def task = project.task("${AppIconOverlayTask.NAME}${variant.name.capitalize()}", type: AppIconOverlayTask) as AppIconOverlayTask
                task.variant = variant
                task.outputDir = outputDir
                tasks.add(task)

                def generateResources = project.getTasksByName("generate${variant.name.capitalize()}Resources", false)
                for (Task t : generateResources) {
                    t.dependsOn(task)
                }
            }

            project.task(AppIconOverlayTask.NAME, dependsOn: tasks);
            AppIconOverlayTask.log(project, 'fin AppIconOverlayPlugin')
        }
    }

}
