package com.github.michaelengland.fliptheswitch.gradle;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class FixturesTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testSimple() throws Exception {
        setupFromFixtureName("simple");

        BuildResult result = gradleBuild();

        assertThat(result.getOutput(), equalTo("Hello world!\n"));
    }

    private BuildResult gradleBuild() throws Exception {
        return gradleRunner().build();
    }

    private GradleRunner gradleRunner() throws Exception {
        return GradleRunner.create()
                .withProjectDir(temporaryFolder.getRoot())
                .withPluginClasspath(pluginClasspaths())
                .withArguments(gradleArguments());
    }

    private void setupFromFixtureName(final String fixtureName) throws Exception {
        FileUtils.copyDirectory(folderForFixtureName(fixtureName), temporaryFolder.getRoot());
    }

    private File folderForFixtureName(final String fixtureName) throws Exception {
        return new File(new File(Resources.getResource(fixtureName).toURI()), "fixture");
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
        gradleArguments.add("helloWorld");
        gradleArguments.add("--quiet");
        gradleArguments.add("--stacktrace");
        return gradleArguments;
    }
}
