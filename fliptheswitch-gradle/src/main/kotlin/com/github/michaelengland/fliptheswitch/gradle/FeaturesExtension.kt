package com.github.michaelengland.fliptheswitch.gradle

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

open class FeaturesExtension {
    lateinit var defaultConfig: NamedDomainObjectContainer<FeatureDefinition>
    lateinit var productFlavors: NamedDomainObjectContainer<ProductFlavorConfig>

    fun initialize(project: Project) {
        defaultConfig = project.container(FeatureDefinition::class.java)
        productFlavors = project.container(ProductFlavorConfig::class.java)
    }

    fun defaultConfig(action: Action<NamedDomainObjectContainer<FeatureDefinition>>) {
        action.execute(defaultConfig)
    }

    fun productFlavors(action: Action<NamedDomainObjectContainer<ProductFlavorConfig>>) {
        action.execute(productFlavors)
    }
}
