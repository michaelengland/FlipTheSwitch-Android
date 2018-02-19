package com.github.michaelengland.fliptheswitch.gradle

import com.github.michaelengland.fliptheswitch.Feature
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import java.io.File

open class CreateFeaturesTask : SourceTask() {
    var features: List<Feature> = listOf()
    @get:OutputDirectory
    var buildDirectory: File? = null

    @TaskAction
    fun execute(inputs: IncrementalTaskInputs) {
        buildDirectory?.mkdirs()
        FeaturesWriter(features).build().writeTo(buildDirectory)
    }
}
