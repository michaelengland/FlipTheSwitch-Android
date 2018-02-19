package com.github.michaelengland.fliptheswitch.gradle;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FixturesTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void aSimpleProjectSetup_generatesCorrectFeaturesFilePerBuildType() throws Exception {
        setupFromFixtureName("simple");

        gradleRunner("assembleDebug").build();
        assertThat(generatedFile(), is(expectedFile()));

        gradleRunner("clean").build();

        gradleRunner("assembleRelease").build();
        assertThat(generatedFile(), is(expectedFile()));
    }

    @Test
    public void aProjectSetupWithProductFlavors_generatesCorrectFeaturesFilePerFlavor() throws Exception {
        setupFromFixtureName("product-flavors");

        gradleRunner("assembleProductionDebug").build();
        assertThat(generatedFile(), is(expectedFile()));

        gradleRunner("clean").build();

        gradleRunner("assembleProductionRelease").build();
        assertThat(generatedFile(), is(expectedFile()));

    }

    @Test
    public void aProjectSetupWithInheritedProductFlavors_generatesCorrectFeaturesFilePerFlavor() throws Exception {
        setupFromFixtureName("inheritance");

        gradleRunner("assembleProductionDebug").build();
        assertThat(generatedFile(), is(expectedFile()));

        gradleRunner("clean").build();

        gradleRunner("assembleProductionRelease").build();
        assertThat(generatedFile(), is(expectedFile()));
    }

    @Test
    public void aProjectSetupWithoutAndroid_throwsError() throws Exception {
        setupFromFixtureName("no-android-yet");

        assertThat(gradleRunner("assemble").buildAndFail().getOutput(),
                containsString("You must apply the Android App plugin before applying the fliptheswitch plugin"));
    }

    @Test
    public void aProjectSetupWithNonExistentProductFlavors_throwsError() throws Exception {
        setupFromFixtureName("non-existent-product-flavors");

        assertThat(gradleRunner("assembleProductionRelease").buildAndFail().getOutput(),
                containsString("You cannot have feature overrides for a non-existent product flavor (nonExistent)"));
    }

    @Test
    public void aProjectSetupWithNonExistentFeatureOverrides_throwsError() throws Exception {
        setupFromFixtureName("non-existent-feature-override");

        assertThat(gradleRunner("assembleProductionRelease").buildAndFail().getOutput(),
                containsString("You cannot have feature overrides for a non-existent feature (nonExistent)"));
    }

    private GradleRunner gradleRunner(String buildCommand) throws Exception {
        return GradleRunner.create()
                .withProjectDir(temporaryFolder.getRoot())
                .withPluginClasspath(pluginClasspaths())
                .withArguments(gradleArguments(buildCommand));
    }

    private void setupFromFixtureName(final String fixtureName) throws Exception {
        FileUtils.copyDirectory(folderForFixtureName(fixtureName), temporaryFolder.getRoot());
        FileUtils.copyDirectory(sourceFolder(), temporaryFolder.newFolder("src"));
    }

    private File folderForFixtureName(final String fixtureName) throws Exception {
        return new File(Resources.getResource("fixtures" + File.separator + fixtureName).toURI());
    }

    private String generatedFile() throws Exception {
        return FileUtils.readFileToString(new File(temporaryFolder.getRoot() +
                "/build/generated/source/fliptheswitch/com/github/michaelengland/fliptheswitch/Features.java"));
    }

    private String expectedFile() throws Exception {
        URL url = Resources.getResource("expected/Features.java");
        return Resources.toString(url, Charsets.UTF_8);
    }

    private File sourceFolder() throws Exception {
        return new File(Resources.getResource("src").toURI());
    }

    private List<File> pluginClasspaths() throws Exception {
        List<String> classpathStrings = Resources.readLines(Resources.getResource("plugin-classpath.txt"),
                Charsets.UTF_8);
        List<File> pluginClasspaths = new ArrayList<>(classpathStrings.size());
        for (String classpathString : classpathStrings) {
            pluginClasspaths.add(new File(classpathString));
        }
        return pluginClasspaths;
    }

    private List<String> gradleArguments(String buildCommand) {
        List<String> gradleArguments = new ArrayList<>();
        gradleArguments.add(buildCommand);
        gradleArguments.add("--stacktrace");
        gradleArguments.add("--info");
        return gradleArguments;
    }
}
