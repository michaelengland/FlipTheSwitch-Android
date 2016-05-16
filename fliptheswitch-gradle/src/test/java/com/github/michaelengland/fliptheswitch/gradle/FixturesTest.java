package com.github.michaelengland.fliptheswitch.gradle;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class FixturesTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void aSimpleProjectSetup_generatesCorrectFeaturesFile() throws Exception {
        setupFromFixtureName("simple");

        gradleRunner().build();
    }

    @Test
    public void aProjectSetupWithoutAndroid_throwsError() throws Exception {
        setupFromFixtureName("no-android-yet");

        assertThat(gradleRunner().buildAndFail().getOutput(), containsString("You must apply the Android plugin before applying the fliptheswitch plugin"));
    }

    private GradleRunner gradleRunner() throws Exception {
        return GradleRunner.create()
                .withProjectDir(temporaryFolder.getRoot())
                .withPluginClasspath(pluginClasspaths())
                .withArguments(gradleArguments());
    }

    private void setupFromFixtureName(final String fixtureName) throws Exception {
        FileUtils.copyDirectory(folderForFixtureName(fixtureName), temporaryFolder.getRoot());
        FileUtils.copyDirectory(sourceFolder(), temporaryFolder.newFolder("src"));
    }

    private File folderForFixtureName(final String fixtureName) throws Exception {
        return new File(Resources.getResource("fixtures/" + fixtureName).toURI());
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

    private List<String> gradleArguments() {
        List<String> gradleArguments = new ArrayList<>();
        gradleArguments.add("--stacktrace");
        return gradleArguments;
    }
}
