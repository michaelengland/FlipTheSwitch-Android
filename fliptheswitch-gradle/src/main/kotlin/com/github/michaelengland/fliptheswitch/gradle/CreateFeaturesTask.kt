package com.github.michaelengland.fliptheswitch.gradle

import com.github.michaelengland.fliptheswitch.Feature
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

open class CreateFeaturesTask : DefaultTask() {
    var features: List<Feature> = listOf()
    @get:OutputDirectory
    var buildDirectory: File? = null

    @TaskAction
    fun createFeatures() {
        buildDirectory?.mkdirs()
        FeaturesWriter(features).build().writeTo(buildDirectory)
    }
}
