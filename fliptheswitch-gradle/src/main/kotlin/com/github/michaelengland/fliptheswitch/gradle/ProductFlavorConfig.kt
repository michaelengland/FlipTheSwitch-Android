package com.github.michaelengland.fliptheswitch.gradle

open class ProductFlavorConfig(val name: String) {
    var inheritsFrom: ProductFlavorConfig? = null
    var overrides: Map<String, Boolean> = mapOf()
}
