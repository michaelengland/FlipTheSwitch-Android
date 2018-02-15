package com.github.michaelengland.fliptheswitch.gradle

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

class FeaturesExtension {
    NamedDomainObjectContainer<FeatureDefinition> defaultConfig
    NamedDomainObjectContainer<ProductFlavorConfig> productFlavors

    NamedDomainObjectContainer<FeatureDefinition> defaultConfig() {
        return defaultConfig
    }

    void initialize(final Project project) {
        defaultConfig = project.container(FeatureDefinition.class);
        productFlavors = project.container(ProductFlavorConfig.class);
    }

    void defaultConfig(final Action<NamedDomainObjectContainer<FeatureDefinition>> action) {
        action.execute(defaultConfig)
    }

    NamedDomainObjectContainer<ProductFlavorConfig> productFlavors() {
        return productFlavors
    }

    void productFlavors(final Action<NamedDomainObjectContainer<ProductFlavorConfig>> action) {
        action.execute(productFlavors)
    }
}
