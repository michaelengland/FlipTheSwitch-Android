package com.github.michaelengland.fliptheswitch.gradle;

import com.github.michaelengland.fliptheswitch.Feature;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class FeaturesWriterTest {
    private FeaturesWriter featuresWriter;
    private String expectedGeneratedFeaturesFile;

    @Before
    public void setup() throws Exception {
        URL url = Resources.getResource("expected/Features.java");
        expectedGeneratedFeaturesFile = Resources.toString(url, Charsets.UTF_8);
        List<Feature> defaultFeatures = new ArrayList<>();
        defaultFeatures.add(new Feature("anotherFeature", "Another disabled feature", false));
        defaultFeatures.add(new Feature("someFeature", "Some simple enabled feature", true));
        featuresWriter = new FeaturesWriter(defaultFeatures);
    }

    @Test
    public void build_generatesFeaturesFile() throws IOException {
        assertThat(featuresWriter.build().toString(), equalTo(expectedGeneratedFeaturesFile));
    }
}
