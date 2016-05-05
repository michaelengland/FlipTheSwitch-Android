package com.michaelengland.fliptheswitch.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class FlipTheSwitchPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        setupExtension(project);
    }

    private static void setupExtension(final Project project) {
    }
}
