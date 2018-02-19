package com.github.michaelengland.fliptheswitch.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.api.ApplicationVariant
import com.github.michaelengland.fliptheswitch.Feature
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

open class FlipTheSwitchPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        checkAndroidPlugin(project)
        setupExtension(project)
        project.afterEvaluate {
            checkProductFlavorsExist(project)
            createFeatureTask(project)
        }
    }

    private fun checkAndroidPlugin(project: Project) {
        if (findAppPlugin(project) == null) {
            throw GradleException("You must apply the Android App plugin before applying the fliptheswitch plugin")
        }
    }

    private fun setupExtension(project: Project) {
        project.extensions.create(flipTheSwitchExtensionName, FeaturesExtension::class.java).initialize(project)
    }

    private fun checkProductFlavorsExist(project: Project) {
        val productFlavorNames = getProductFlavorNames(project)
        getProductFlavorConfigs(project).forEach {
            if (it.name !in productFlavorNames) {
                throw GradleException("You cannot have feature overrides for a non-existent product flavor (${it.name})")
            }
        }
    }

    private fun createFeatureTask(project: Project) {
        val generateAllTask = project.tasks.create("$createFeaturesTaskPrefix$createFeaturesTaskSuffix")
        getApplicationVariants(project).forEach {
            val task = project.tasks.create("$createFeaturesTaskPrefix${it.name.capitalize()}$createFeaturesTaskSuffix", CreateFeaturesTask::class.java)
            task.features = getFeatures(project, it)
            task.description = "Generate features class for ${it.name}"
            task.buildDirectory = featuresFile(project, it)
            it.registerJavaGeneratingTask(task, task.buildDirectory)
            generateAllTask.dependsOn(task)
            it.preBuild.dependsOn(task)
        }
    }

    private fun getFeatures(project: Project, applicationVariant: ApplicationVariant): List<Feature> {
        checkOverriddenFeaturesExist(project, applicationVariant)
        return getDefaultConfig(project).map {
            Feature(it.name, it.description, getOverrides(project, applicationVariant)[it.name]
                    ?: it.enabled)
        }
    }

    private fun checkOverriddenFeaturesExist(project: Project, applicationVariant: ApplicationVariant) {
        val featureNames = getFeatureNames(project)
        getOverrides(project, applicationVariant).keys.forEach {
            if (it !in featureNames) {
                throw GradleException("You cannot have feature overrides for a non-existent feature ($it)")
            }
        }
    }

    private fun getOverrides(project: Project, applicationVariant: ApplicationVariant): Map<String, Boolean> {
        val productFlavorConfig = getProductFlavorConfigs(project).findByName(applicationVariant.flavorName)
        return if (productFlavorConfig == null) {
            mapOf()
        } else {
            getOverrides(productFlavorConfig)
        }
    }

    private fun getOverrides(productFlavorConfig: ProductFlavorConfig): Map<String, Boolean> {
        val inheritsFrom = productFlavorConfig.inheritsFrom
        return if (inheritsFrom == null) {
            productFlavorConfig.overrides
        } else {
            getOverrides(inheritsFrom) + productFlavorConfig.overrides
        }
    }

    private fun getProductFlavorConfigs(project: Project) =
            findFlipTheSwitchExtension(project).productFlavors

    private fun getProductFlavorNames(project: Project) =
            findAndroidAppExtension(project).productFlavors.map { it.name }

    private fun getFeatureNames(project: Project) =
            getDefaultConfig(project).map { it.name }

    private fun featuresFile(project: Project, applicationVariant: ApplicationVariant) =
            File(listOf(project.buildDir, "generated", "source", "fliptheswitch", applicationVariant.dirName).joinToString(separator = File.separator))

    private fun getApplicationVariants(project: Project) =
            findAndroidAppExtension(project).applicationVariants

    private fun getDefaultConfig(project: Project) =
            findFlipTheSwitchExtension(project).defaultConfig

    private fun findFlipTheSwitchExtension(project: Project) =
            project.extensions.findByName(flipTheSwitchExtensionName) as FeaturesExtension

    private fun findAndroidAppExtension(project: Project) =
            project.extensions.findByName(androidExtensionName) as AppExtension

    private fun findAppPlugin(project: Project): AppPlugin? =
            project.plugins.findPlugin(AppPlugin::class.java)

    private val flipTheSwitchExtensionName = "features"
    private val androidExtensionName = "android"
    private val createFeaturesTaskPrefix = "generate"
    private val createFeaturesTaskSuffix = "Features"
}
