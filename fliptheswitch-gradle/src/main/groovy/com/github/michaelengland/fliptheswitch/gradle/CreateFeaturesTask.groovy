package com.github.michaelengland.fliptheswitch.gradle

import com.github.michaelengland.fliptheswitch.Feature
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs

class CreateFeaturesTask extends SourceTask {
    List<Feature> features
    File buildDirectory

    @TaskAction
    void execute(IncrementalTaskInputs inputs) {
        buildDirectory.mkdirs()
        FeaturesWriter featuresWriter = new FeaturesWriter()
        featuresWriter.build().writeTo(buildDirectory)
    }
}
