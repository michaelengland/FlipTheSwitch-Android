package com.github.michaelengland.fliptheswitch.gradle;

public class FeatureDefinition {
    private final String name;
    private String description;
    private boolean enabled;

    public FeatureDefinition(final String name) {
        this.name = name;
        description = "";
        enabled = false;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public void description(final String description) {
        this.description = description;
    }

    public boolean enabled() {
        return enabled;
    }

    public void enabled(final boolean enabled) {
        this.enabled = enabled;
    }
}
