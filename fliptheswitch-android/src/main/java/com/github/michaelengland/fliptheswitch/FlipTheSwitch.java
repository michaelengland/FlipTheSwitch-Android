package com.github.michaelengland.fliptheswitch;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

public class FlipTheSwitch {
    private static final String SHARED_PREFERENCES_NAME = "features";

    private static volatile FlipTheSwitch singleton;

    private final SharedPreferences sharedPreferences;
    private final List<Feature> defaultFeatures;

    public static FlipTheSwitch with(final Context context, List<Feature> defaultFeatures) {
        if (singleton == null) {
            synchronized (FlipTheSwitch.class) {
                if (singleton == null) {
                    singleton = new FlipTheSwitch(context.getSharedPreferences(SHARED_PREFERENCES_NAME,
                            Context.MODE_PRIVATE), defaultFeatures);
                }
            }
        }
        return singleton;
    }

    FlipTheSwitch(final SharedPreferences sharedPreferences, final List<Feature> defaultFeatures) {
        this.sharedPreferences = sharedPreferences;
        this.defaultFeatures = defaultFeatures;
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
