package com.github.michaelengland.fliptheswitch.gradle

class FeatureDefinition {
    final String name

    String description
    boolean enabled

    FeatureDefinition(String name) {
        this.name = name
        description = ""
        enabled = false
    }

    void description(final String description) {
        this.description = description
    }

    void enabled(final boolean enabled) {
        this.enabled = enabled
    }
}
