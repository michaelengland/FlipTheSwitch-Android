package com.github.michaelengland.fliptheswitch.gradle;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.AppPlugin;
import com.android.build.gradle.api.ApplicationVariant;
import com.android.build.gradle.internal.dsl.ProductFlavor;
import com.github.michaelengland.fliptheswitch.Feature;

import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.NamedDomainObjectCollection;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FlipTheSwitchPlugin implements Plugin<Project> {
    private static final String FLIP_THE_SWITCH_EXTENSION_NAME = "features";
    private static final String ANDROID_EXTENSION_NAME = "android";

    @Override
    public void apply(Project project) {
        checkAndroidPlugin(project);
        setupExtension(project);
        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(final Project project) {
                writeFeatureFiles(project);
            }
        });
    }

    private void checkAndroidPlugin(final Project project) {
        if (findAppPlugin(project) == null) {
            throw new GradleException("You must apply the Android App plugin before applying the fliptheswitch plugin");
        }
    }

    private void setupExtension(final Project project) {
        FeaturesExtension featuresExtension = project.getExtensions().create(FLIP_THE_SWITCH_EXTENSION_NAME,
                FeaturesExtension.class);
        featuresExtension.initialize(project);
    }

    private void checkProductFlavorsExist(final Project project) {
        for (ProductFlavorConfig productFlavorConfig : getProductFlavorConfigs(project)) {
            if (!getProductFlavorNames(project).contains(productFlavorConfig.name())) {
                throw new GradleException("You cannot have feature overrides for a non-existent product flavor (" +
                        productFlavorConfig.name() + ")");
            }
        }
    }

    private void writeFeatureFiles(final Project project) {
        checkProductFlavorsExist(project);
        for (ApplicationVariant applicationVariant : getApplicationVariants(project)) {
            writeFeatureFile(project, applicationVariant);
        }
    }

    private void writeFeatureFile(final Project project, final ApplicationVariant applicationVariant) {
        FeaturesWriter featuresWriter = new FeaturesWriter(getFeatures(project, applicationVariant));
        try {
            featuresWriter.build().writeTo(featuresFile(project, applicationVariant));
        } catch (IOException e) {
            throw new GradleException("Failure writing to " + featuresFile(project, applicationVariant), e);
        }
    }

    private Collection<Feature> getFeatures(final Project project, final ApplicationVariant applicationVariant) {
        checkOverriddenFeaturesExist(project, applicationVariant);
        Collection<Feature> defaultFeatures = new ArrayList<>();
        for (FeatureDefinition featureDefinition : getDefaultConfig(project)) {
            defaultFeatures.add(new Feature(featureDefinition.name(), featureDefinition.description(),
                    getOverrides(project, applicationVariant).getOrDefault(featureDefinition.name(),
                            featureDefinition.enabled())));
        }
        return defaultFeatures;
    }

    private void checkOverriddenFeaturesExist(final Project project, final ApplicationVariant applicationVariant) {
        Collection<String> featureNames = getFeatureNames(project);
        for (String name : getOverrides(project, applicationVariant).keySet()) {
            if (!featureNames.contains(name)) {
                throw new GradleException("You cannot have feature overrides for a non-existent feature (" + name +
                        ")");
            }
        }
    }

    private Map<String, Boolean> getOverrides(final Project project, final ApplicationVariant applicationVariant) {
        ProductFlavorConfig productFlavorConfig = getProductFlavorConfigs(project)
                .findByName(applicationVariant.getFlavorName());
        if (productFlavorConfig == null) {
            return new HashMap<>();
        } else {
            return getOverrides(productFlavorConfig);
        }
    }

    private Map<String, Boolean> getOverrides(final ProductFlavorConfig productFlavorConfig) {
        if (productFlavorConfig.inheritsFrom() == null) {
            return productFlavorConfig.overrides();
        } else {
            Map<String, Boolean> overrides = new HashMap<>(getOverrides(productFlavorConfig.inheritsFrom()));
            overrides.putAll(productFlavorConfig.overrides());
            return overrides;
        }
    }

    private NamedDomainObjectCollection<ProductFlavorConfig> getProductFlavorConfigs(final Project project) {
        return findFlipTheSwitchExtension(project).productFlavors();
    }

    private Collection<String> getProductFlavorNames(final Project project) {
        Collection<String> productFlavorNames = new ArrayList<>();
        for (ProductFlavor productFlavor : findAndroidAppExtension(project).getProductFlavors()) {
            productFlavorNames.add(productFlavor.getName());
        }
        return productFlavorNames;
    }

    private Collection<String> getFeatureNames(final Project project) {
        Collection<String> featureNames = new ArrayList<>();
        for (FeatureDefinition featureDefinition : getDefaultConfig(project)) {
            featureNames.add(featureDefinition.name());
        }
        return featureNames;
    }

    private Collection<ApplicationVariant> getApplicationVariants(final Project project) {
        return findAndroidAppExtension(project).getApplicationVariants();
    }

    private Collection<FeatureDefinition> getDefaultConfig(final Project project) {
        return findFlipTheSwitchExtension(project).defaultConfig();
    }

    private File featuresFile(final Project project, final ApplicationVariant applicationVariant) {
        return new File(project.getBuildDir() + "/generated/source/buildConfig/" + applicationVariant.getDirName());
    }

    private FeaturesExtension findFlipTheSwitchExtension(final Project project) {
        return (FeaturesExtension) project.getExtensions().findByName(FLIP_THE_SWITCH_EXTENSION_NAME);
    }

    private AppExtension findAndroidAppExtension(final Project project) {
        return (AppExtension) project.getExtensions().findByName(ANDROID_EXTENSION_NAME);
    }

    private AppPlugin findAppPlugin(final Project project) {
        return project.getPlugins().findPlugin(AppPlugin.class);
    }
}
