package com.github.michaelengland.fliptheswitch.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.dsl.ProductFlavor
import com.github.michaelengland.fliptheswitch.Feature
import org.gradle.api.*

class FlipTheSwitchPlugin implements Plugin<Project> {
    private static final String FLIP_THE_SWITCH_EXTENSION_NAME = "features"
    private static final String ANDROID_EXTENSION_NAME = "android"
    private static final String CREATE_FEATURES_TASK_PREFIX = "generate"
    private static final String CREATE_FEATURES_TASK_SUFFIX = "Features"

    @Override
    void apply(final Project project) {
        checkAndroidPlugin(project)
        setupExtension(project)
        project.afterEvaluate {
            checkProductFlavorsExist(project)
            createFeatureTask(project)
        }
    }

    private static void checkAndroidPlugin(final Project project) {
        if (findAppPlugin(project) == null) {
            throw new GradleException("You must apply the Android App plugin before applying the fliptheswitch plugin")
        }
    }

    private static void setupExtension(final Project project) {
        FeaturesExtension featuresExtension = project.extensions.create(FLIP_THE_SWITCH_EXTENSION_NAME,
                FeaturesExtension.class)
        featuresExtension.initialize(project)
    }

    private static void checkProductFlavorsExist(final Project project) {
        for (ProductFlavorConfig productFlavorConfig : getProductFlavorConfigs(project)) {
            if (!getProductFlavorNames(project).contains(productFlavorConfig.name)) {
                throw new GradleException("You cannot have feature overrides for a non-existent product flavor ($productFlavorConfig.name)")
            }
        }
    }

    private static void createFeatureTask(final Project project) {
        Task generateAllTask = project.tasks.create("$CREATE_FEATURES_TASK_PREFIX$CREATE_FEATURES_TASK_SUFFIX")
        for (ApplicationVariant applicationVariant : getApplicationVariants(project)) {
            CreateFeaturesTask task = project.tasks.create("$CREATE_FEATURES_TASK_PREFIX${applicationVariant.name.capitalize()}$CREATE_FEATURES_TASK_SUFFIX", CreateFeaturesTask.class)
            task.features = getFeatures(project, applicationVariant)
            task.description = "Generate features class for $applicationVariant.name"
            task.buildDirectory = featuresFile(project)
            generateAllTask.dependsOn(task)
            applicationVariant.registerJavaGeneratingTask(task, task.buildDirectory)
        }
    }

    private static List<Feature> getFeatures(
            final Project project, final ApplicationVariant applicationVariant) {
        checkOverriddenFeaturesExist(project, applicationVariant)
        List<Feature> defaultFeatures = new ArrayList<>()
        for (FeatureDefinition featureDefinition : getDefaultConfig(project)) {
            boolean isEnabled
            if (getOverrides(project, applicationVariant).containsKey(featureDefinition.name)) {
                isEnabled = getOverrides(project, applicationVariant).get(featureDefinition.name)
            } else {
                isEnabled = featureDefinition.enabled
            }
            defaultFeatures.add(new Feature(featureDefinition.name, featureDefinition.description, isEnabled))
        }
        return defaultFeatures
    }

    private static void checkOverriddenFeaturesExist(
            final Project project, final ApplicationVariant applicationVariant) {
        Collection<String> featureNames = getFeatureNames(project)
        for (String name : getOverrides(project, applicationVariant).keySet()) {
            if (!featureNames.contains(name)) {
                throw new GradleException("You cannot have feature overrides for a non-existent feature ($name)")
            }
        }
    }

    private static Map<String, Boolean> getOverrides(
            final Project project, final ApplicationVariant applicationVariant) {
        ProductFlavorConfig productFlavorConfig = getProductFlavorConfigs(project)
                .findByName(applicationVariant.flavorName)
        if (productFlavorConfig == null) {
            return new HashMap<>()
        } else {
            return getOverrides(productFlavorConfig)
        }
    }

    private static Map<String, Boolean> getOverrides(final ProductFlavorConfig productFlavorConfig) {
        if (productFlavorConfig.inheritsFrom == null) {
            return productFlavorConfig.overrides
        } else {
            Map<String, Boolean> overrides = new HashMap<>(getOverrides(productFlavorConfig.inheritsFrom))
            overrides.putAll(productFlavorConfig.overrides)
            return overrides
        }
    }

    private static NamedDomainObjectCollection<ProductFlavorConfig> getProductFlavorConfigs(
            final Project project) {
        return findFlipTheSwitchExtension(project).productFlavors
    }

    private static Collection<String> getProductFlavorNames(final Project project) {
        Collection<String> productFlavorNames = new ArrayList<>()
        for (ProductFlavor productFlavor : findAndroidAppExtension(project).productFlavors) {
            productFlavorNames.add(productFlavor.name)
        }
        return productFlavorNames
    }

    private static Collection<String> getFeatureNames(final Project project) {
        Collection<String> featureNames = new ArrayList<>()
        for (FeatureDefinition featureDefinition : getDefaultConfig(project)) {
            featureNames.add(featureDefinition.name)
        }
        return featureNames
    }

    private static File featuresFile(final Project project) {
        return new File("$project.buildDir/generated/source/fliptheswitch")
    }

    private static Collection<ApplicationVariant> getApplicationVariants(final Project project) {
        return findAndroidAppExtension(project).applicationVariants
    }

    private static Collection<FeatureDefinition> getDefaultConfig(final Project project) {
        return findFlipTheSwitchExtension(project).defaultConfig
    }

    private static FeaturesExtension findFlipTheSwitchExtension(final Project project) {
        return project.extensions.findByName(FLIP_THE_SWITCH_EXTENSION_NAME)
    }

    private static AppExtension findAndroidAppExtension(final Project project) {
        return project.extensions.findByName(ANDROID_EXTENSION_NAME)
    }

    private static AppPlugin findAppPlugin(final Project project) {
        return project.plugins.findPlugin(AppPlugin.class)
    }
}
