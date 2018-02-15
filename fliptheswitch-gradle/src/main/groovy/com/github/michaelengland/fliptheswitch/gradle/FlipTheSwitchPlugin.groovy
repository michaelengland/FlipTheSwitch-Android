package com.github.michaelengland.fliptheswitch.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.dsl.ProductFlavor
import com.github.michaelengland.fliptheswitch.Feature
import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Plugin
import org.gradle.api.Project

class FlipTheSwitchPlugin implements Plugin<Project> {
    private static final String FLIP_THE_SWITCH_EXTENSION_NAME = "features"
    private static final String ANDROID_EXTENSION_NAME = "android"
    private static final String CREATE_FEATURES_TASK_NAME = "createFeatures"

    @Override
    void apply(final Project project) {
        checkAndroidPlugin(project)
        setupExtension(project)
        project.afterEvaluate {
            createFeatureTask(project)
            writeFeatureFiles(project)
        }
    }

    private void createFeatureTask(final Project project) {
        project.task(CREATE_FEATURES_TASK_NAME) {
            doLast {
                writeFeatureFiles(project)
            }
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
                throw new GradleException("You cannot have feature overrides for a non-existent product flavor (" +
                        productFlavorConfig.name + ")")
            }
        }
    }

    private void writeFeatureFiles(final Project project) {
        checkProductFlavorsExist(project)
        for (ApplicationVariant applicationVariant : getApplicationVariants(project)) {
            setupFeaturesSource(project, applicationVariant)
            writeFeatureFile(project, applicationVariant)
        }
    }

    private void setupFeaturesSource(final Project project, final ApplicationVariant applicationVariant) {
        applicationVariant.javaCompile.doFirst {
            writeFeatureFile(project, applicationVariant)
        }
    }

    private void writeFeatureFile(final Project project, final ApplicationVariant applicationVariant) {
        featuresFile(project, applicationVariant).mkdirs()
        FeaturesWriter featuresWriter = new FeaturesWriter(getFeatures(project, applicationVariant))
        try {
            featuresWriter.build().writeTo(featuresFile(project, applicationVariant))
        } catch (IOException e) {
            throw new GradleException("Failure writing to " + featuresFile(project, applicationVariant), e)
        }
    }

    private Collection<Feature> getFeatures(final Project project, final ApplicationVariant applicationVariant) {
        checkOverriddenFeaturesExist(project, applicationVariant)
        Collection<Feature> defaultFeatures = new ArrayList<>()
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

    private void checkOverriddenFeaturesExist(final Project project, final ApplicationVariant applicationVariant) {
        Collection<String> featureNames = getFeatureNames(project)
        for (String name : getOverrides(project, applicationVariant).keySet()) {
            if (!featureNames.contains(name)) {
                throw new GradleException("You cannot have feature overrides for a non-existent feature (" + name +
                        ")")
            }
        }
    }

    private Map<String, Boolean> getOverrides(final Project project, final ApplicationVariant applicationVariant) {
        ProductFlavorConfig productFlavorConfig = getProductFlavorConfigs(project)
                .findByName(applicationVariant.flavorName)
        if (productFlavorConfig == null) {
            return new HashMap<>()
        } else {
            return getOverrides(productFlavorConfig)
        }
    }

    private Map<String, Boolean> getOverrides(final ProductFlavorConfig productFlavorConfig) {
        if (productFlavorConfig.inheritsFrom == null) {
            return productFlavorConfig.overrides
        } else {
            Map<String, Boolean> overrides = new HashMap<>(getOverrides(productFlavorConfig.inheritsFrom))
            overrides.putAll(productFlavorConfig.overrides)
            return overrides
        }
    }

    private static NamedDomainObjectCollection<ProductFlavorConfig> getProductFlavorConfigs(final Project project) {
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

    private static Collection<ApplicationVariant> getApplicationVariants(final Project project) {
        return findAndroidAppExtension(project).applicationVariants
    }

    private static Collection<FeatureDefinition> getDefaultConfig(final Project project) {
        return findFlipTheSwitchExtension(project).defaultConfig
    }

    private static File featuresFile(final Project project, final ApplicationVariant applicationVariant) {
        return new File("$project.buildDir/generated/source/buildConfig/$applicationVariant.dirName")
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
