package com.michaelengland.fliptheswitch.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class FlipTheSwitchPlugin implements Plugin<Project> {
    private static final String EXTENSION_NAME = "features";

    @Override
    public void apply(Project project) {
        setupExtension(project);
    }

    private static void setupExtension(final Project project) {
        final FeaturesExtension featuresExtension = project.getExtensions()
                .create(EXTENSION_NAME, FeaturesExtension.class);
        featuresExtension.initialize(project);
    }
}
