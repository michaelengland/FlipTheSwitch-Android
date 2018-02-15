package com.github.michaelengland.fliptheswitch.gradle

class ProductFlavorConfig {
    final String name

    ProductFlavorConfig inheritsFrom
    Map<String, Boolean> overrides

    ProductFlavorConfig(final String name) {
        this.name = name
        overrides = new HashMap<>()
    }

    void inheritsFrom(final ProductFlavorConfig inheritsFrom) {
        this.inheritsFrom = inheritsFrom
    }

    void overrides(final Map<String, Boolean> overrides) {
        this.overrides = overrides
    }
}
