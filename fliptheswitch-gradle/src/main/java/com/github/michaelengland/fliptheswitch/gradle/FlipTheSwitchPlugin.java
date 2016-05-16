package com.github.michaelengland.fliptheswitch.gradle;

import com.android.build.gradle.AppPlugin;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class FlipTheSwitchPlugin implements Plugin<Project> {
    private static final String EXTENSION_NAME = "features";

    @Override
    public void apply(Project project) {
        checkAndroidPlugin(project);
        setupExtension(project);
    }

    private void checkAndroidPlugin(final Project project) {
        if (project.getPlugins().findPlugin(AppPlugin.class) == null) {
            throw new GradleException("You must apply the Android plugin before applying the fliptheswitch plugin");
        }
    }

    private void setupExtension(final Project project) {
        final FeaturesExtension featuresExtension = project.getExtensions()
                .create(EXTENSION_NAME, FeaturesExtension.class);
        featuresExtension.initialize(project);
    }
}
