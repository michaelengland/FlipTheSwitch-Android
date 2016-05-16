package com.github.michaelengland.fliptheswitch.gradle;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;

public class FeaturesExtension {
    private NamedDomainObjectContainer<FeatureDefinition> defaultConfig;

    public void initialize(final Project project) {
        defaultConfig = project.container(FeatureDefinition.class);
    }

    public void defaultConfig(final Action<NamedDomainObjectContainer<FeatureDefinition>> action) {
        action.execute(defaultConfig);
    }

    public NamedDomainObjectContainer<FeatureDefinition> defaultConfig() {
        return defaultConfig;
    }
}
