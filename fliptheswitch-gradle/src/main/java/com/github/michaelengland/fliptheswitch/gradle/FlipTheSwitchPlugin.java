package com.github.michaelengland.fliptheswitch.gradle;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.AppPlugin;
import com.android.build.gradle.api.ApplicationVariant;
import com.github.michaelengland.fliptheswitch.Feature;

import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class FlipTheSwitchPlugin implements Plugin<Project> {
    private static final String FLIP_THE_SWITCH_EXTENSION_NAME = "features";
    private static final String ANDROID_EXTENSION_NAME = "android";

    @Override
    public void apply(Project project) {
        checkAndroidPlugin(project);
        setupExtension(project);
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
        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(final Project project) {
                writeFeatureFiles(project, getDefaultFeatures(project));
            }
        });
    }

    private void writeFeatureFiles(final Project project, final Collection<Feature> defaultFeatures) {
        findAndroidAppExtension(project).getApplicationVariants().all(new Action<ApplicationVariant>() {
            @Override
            public void execute(ApplicationVariant applicationVariant) {
                writeFeatureFile(project, applicationVariant, defaultFeatures);
            }
        });
    }

    private void writeFeatureFile(final Project project, final ApplicationVariant applicationVariant,
            final Collection<Feature> defaultFeatures) {
        FeaturesWriter featuresWriter = new FeaturesWriter(defaultFeatures);
        try {
            featuresWriter.build().writeTo(featuresFile(project, applicationVariant));
        } catch (IOException e) {
            throw new GradleException("Failure writing to " + featuresFile(project, applicationVariant), e);
        }
    }

    private Collection<Feature> getDefaultFeatures(final Project project) {
        Collection<Feature> defaultFeatures = new ArrayList<>();
        for (FeatureDefinition featureDefinition : getConfig(project)) {
            defaultFeatures.add(new Feature(featureDefinition.name(), featureDefinition.description(),
                    featureDefinition.enabled()));
        }
        return defaultFeatures;
    }

    private Collection<FeatureDefinition> getConfig(final Project project) {
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
