package com.github.michaelengland.fliptheswitch.gradle;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;

public class FeaturesExtension {
    private NamedDomainObjectContainer<FeatureDefinition> defaultConfig;
    private NamedDomainObjectContainer<ProductFlavorConfig> productFlavors;

    public NamedDomainObjectContainer<FeatureDefinition> defaultConfig() {
        return defaultConfig;
    }

    public void initialize(final Project project) {
        defaultConfig = project.container(FeatureDefinition.class);
        productFlavors = project.container(ProductFlavorConfig.class);
    }

    public void defaultConfig(final Action<NamedDomainObjectContainer<FeatureDefinition>> action) {
        action.execute(defaultConfig);
    }

    public NamedDomainObjectContainer<ProductFlavorConfig> productFlavors() {
        return productFlavors;
    }

    public void productFlavors(final Action<NamedDomainObjectContainer<ProductFlavorConfig>> action) {
        action.execute(productFlavors);
    }
}
