package com.github.michaelengland.fliptheswitch;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.common.collect.ImmutableList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class FlipTheSwitchTest {
    private FlipTheSwitch flipTheSwitch;

    private String defaultEnabledFeatureName;
    private String defaultDisabledFeatureName;
    private String standardFeatureName;

    @Before
    public void setUp() {
        defaultEnabledFeatureName = "default_enabled_feature";
        defaultDisabledFeatureName = "default_disabled_feature";
        standardFeatureName = "standard_feature";
        SharedPreferences sharedPreferences = RuntimeEnvironment.application.getSharedPreferences("features",
                Context.MODE_PRIVATE);
        FlipTheSwitch.defaultFeatures = ImmutableList.of(
                new Feature(defaultEnabledFeatureName, "ein geiles Feature", true),
                new Feature(defaultDisabledFeatureName, "noch ein Feature", false)
        );
        flipTheSwitch = new FlipTheSwitch(sharedPreferences);
    }

    @Test
    public void isFeatureEnabled_returnsTrue_whenEnabledByDefault() throws Exception {
        assertThat(flipTheSwitch.isFeatureEnabled(defaultEnabledFeatureName), is(true));
    }

    @Test
    public void isFeatureEnabled_returnsFalse_whenDisabledByDefault() throws Exception {
        assertThat(flipTheSwitch.isFeatureEnabled(defaultDisabledFeatureName), is(false));
    }

    @Test
    public void isFeatureEnabled_returnsFalse_whenNotDefault() throws Exception {
        assertThat(flipTheSwitch.isFeatureEnabled(standardFeatureName), is(false));
    }

    @Test
    public void isFeatureEnabled_returnsTrue_whenManuallyEnabled() throws Exception {
        flipTheSwitch.enableFeature(defaultEnabledFeatureName);
        flipTheSwitch.enableFeature(standardFeatureName);

        assertThat(flipTheSwitch.isFeatureEnabled(defaultEnabledFeatureName), is(true));
        assertThat(flipTheSwitch.isFeatureEnabled(standardFeatureName), is(true));
    }

    @Test
    public void isFeatureEnabled_returnsFalse_whenManuallyDisabled() throws Exception {
        flipTheSwitch.disableFeature(defaultEnabledFeatureName);
        flipTheSwitch.disableFeature(standardFeatureName);

        assertThat(flipTheSwitch.isFeatureEnabled(defaultEnabledFeatureName), is(false));
        assertThat(flipTheSwitch.isFeatureEnabled(standardFeatureName), is(false));
    }

    @Test
    public void isFeatureEnabled_returnsSetValue_whenManuallySet() throws Exception {
        flipTheSwitch.setFeatureEnabled(defaultEnabledFeatureName, false);
        flipTheSwitch.setFeatureEnabled(standardFeatureName, true);

        assertThat(flipTheSwitch.isFeatureEnabled(defaultEnabledFeatureName), is(false));
        assertThat(flipTheSwitch.isFeatureEnabled(standardFeatureName), is(true));
    }

    @Test
    public void isFeatureEnabled_returnsOriginalState_whenFeatureIsReset() throws Exception {
        flipTheSwitch.enableFeature(standardFeatureName);
        flipTheSwitch.disableFeature(defaultEnabledFeatureName);
        flipTheSwitch.resetFeature(standardFeatureName);
        flipTheSwitch.resetFeature(defaultEnabledFeatureName);

        assertThat(flipTheSwitch.isFeatureEnabled(defaultEnabledFeatureName), is(true));
        assertThat(flipTheSwitch.isFeatureEnabled(standardFeatureName), is(false));
    }

    @Test
    public void isFeatureEnabled_returnsOriginalState_whenAllFeaturesAreReset() throws Exception {
        flipTheSwitch.enableFeature(standardFeatureName);
        flipTheSwitch.disableFeature(defaultEnabledFeatureName);
        flipTheSwitch.resetAllFeatures();

        assertThat(flipTheSwitch.isFeatureEnabled(defaultEnabledFeatureName), is(true));
        assertThat(flipTheSwitch.isFeatureEnabled(standardFeatureName), is(false));
    }
}
