package com.github.michaelengland.fliptheswitch;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class FlipTheSwitch {
    private static final String SHARED_PREFERENCES_NAME = "features";

    static List<Feature> defaultFeatures = new ArrayList<>();

    private final SharedPreferences sharedPreferences;

    public FlipTheSwitch(Context context) {
        this(context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE));
    }

    FlipTheSwitch(final SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public static List<Feature> getDefaultFeatures() {
        return defaultFeatures;
    }

    public boolean isFeatureEnabled(final String featureName) {
        return sharedPreferences.getBoolean(featureName, getDefaultEnabled(featureName));
    }

    public void enableFeature(final String featureName) {
        setFeatureEnabled(featureName, true);
    }

    public void disableFeature(final String featureName) {
        setFeatureEnabled(featureName, false);
    }

    public void setFeatureEnabled(final String featureName, final boolean enabled) {
        sharedPreferences.edit().putBoolean(featureName, enabled).apply();
    }

    public void resetFeature(final String featureName) {
        sharedPreferences.edit().remove(featureName).apply();
    }

    public void resetAllFeatures() {
        sharedPreferences.edit().clear().apply();
    }

    private boolean getDefaultEnabled(final String featureName) {
        Feature defaultFeature = getDefaultFeatureForName(featureName);
        return defaultFeature != null && defaultFeature.isEnabled();
    }

    private Feature getDefaultFeatureForName(final String featureName) {
        for (Feature feature : defaultFeatures) {
            if (feature.getName().equals(featureName)) {
                return feature;
            }
        }
        return null;
    }
}
