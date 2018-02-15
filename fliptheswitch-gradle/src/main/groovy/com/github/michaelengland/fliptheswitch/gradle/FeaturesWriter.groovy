package com.github.michaelengland.fliptheswitch.gradle

import com.github.michaelengland.fliptheswitch.Feature
import com.google.common.base.CaseFormat
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec

import javax.lang.model.element.Modifier

class FeaturesWriter {
    private Collection<Feature> defaultFeatures

    FeaturesWriter(final Collection<Feature> defaultFeatures) {
        this.defaultFeatures = defaultFeatures
    }

    JavaFile build() {
        return JavaFile.builder(flipTheSwitchPackageName(), type())
                .build()
    }

    private TypeSpec type() {
        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(featuresTypeName())
                .addModifiers(Modifier.PUBLIC)
                .addStaticBlock(setDefaultFeaturesBlock())
                .addField(flipTheSwitchTypeName(), "flipTheSwitch", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(publicConstructor())
                .addMethod(internalConstructor())
        for (Feature feature : defaultFeatures) {
            typeBuilder.addMethod(isFeatureEnabledMethod(feature))
                    .addMethod(enableFeatureMethod(feature))
                    .addMethod(disableFeatureMethod(feature))
                    .addMethod(setFeatureEnabledMethod(feature))
                    .addMethod(resetFeatureMethod(feature))
        }
        return typeBuilder
                .addMethod(resetAllFeaturesMethod())
                .build()
    }

    private CodeBlock setDefaultFeaturesBlock() {
        CodeBlock.Builder defaultFeaturesMethodBuilder = CodeBlock.builder()
                .addStatement("\$T defaultFeatures = new \$T<>()", listOfFeaturesTypeName(), ArrayList.class)
        for (Feature feature : defaultFeatures) {
            defaultFeaturesMethodBuilder.addStatement("defaultFeatures.add(new \$T(\$S, \$S, \$L))", Feature.class,
                    snakeCaseFeatureName(feature.getName()), feature.getDescription(), feature.isEnabled())
        }
        return defaultFeaturesMethodBuilder.addStatement("\$T.defaultFeatures = defaultFeatures",
                flipTheSwitchTypeName())
                .build()
    }

    private static MethodSpec internalConstructor() {
        return MethodSpec.constructorBuilder()
                .addParameter(flipTheSwitchTypeName(), "flipTheSwitch", Modifier.FINAL)
                .addStatement("this.\$N = \$N", "flipTheSwitch", "flipTheSwitch")
                .build()
    }

    private static MethodSpec publicConstructor() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(contextTypeName(), "context", Modifier.FINAL)
                .addStatement("this(new \$T(\$N))", flipTheSwitchTypeName(), "context")
                .build()
    }

    private static MethodSpec isFeatureEnabledMethod(final Feature feature) {
        return MethodSpec.methodBuilder("is" + camelCaseFeatureName(feature) + "Enabled")
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addStatement("return \$N.isFeatureEnabled(\$S)", "flipTheSwitch",
                snakeCaseFeatureName(feature.getName()))
                .build()
    }

    private static MethodSpec enableFeatureMethod(final Feature feature) {
        return MethodSpec.methodBuilder("enable" + camelCaseFeatureName(feature))
                .addModifiers(Modifier.PUBLIC)
                .addStatement("\$N.enableFeature(\$S)", "flipTheSwitch", snakeCaseFeatureName(feature.getName()))
                .build()
    }

    private static MethodSpec disableFeatureMethod(final Feature feature) {
        return MethodSpec.methodBuilder("disable" + camelCaseFeatureName(feature))
                .addModifiers(Modifier.PUBLIC)
                .addStatement("\$N.disableFeature(\$S)", "flipTheSwitch", snakeCaseFeatureName(feature.getName()))
                .build()
    }

    private static MethodSpec setFeatureEnabledMethod(final Feature feature) {
        return MethodSpec.methodBuilder("set" + camelCaseFeatureName(feature) + "Enabled")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(boolean.class, "enabled", Modifier.FINAL)
                .addStatement("\$N.setFeatureEnabled(\$S, \$N)", "flipTheSwitch", snakeCaseFeatureName(feature.getName()),
                "enabled")
                .build()
    }

    private static MethodSpec resetFeatureMethod(final Feature feature) {
        return MethodSpec.methodBuilder("reset" + camelCaseFeatureName(feature))
                .addModifiers(Modifier.PUBLIC)
                .addStatement("\$N.resetFeature(\$S)", "flipTheSwitch", snakeCaseFeatureName(feature.getName()))
                .build()
    }

    private static String snakeCaseFeatureName(final String string) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, string)
    }

    private static String camelCaseFeatureName(final Feature feature) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, feature.getName())
    }

    private static MethodSpec resetAllFeaturesMethod() {
        return MethodSpec.methodBuilder("resetAllFeatures")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("\$N.resetAllFeatures()", "flipTheSwitch")
                .build()
    }

    private static ClassName flipTheSwitchTypeName() {
        return ClassName.get(flipTheSwitchPackageName(), flipTheSwitchClassName())
    }

    private static ClassName featuresTypeName() {
        return ClassName.get(flipTheSwitchPackageName(), featuresClassName())
    }

    private static ClassName contextTypeName() {
        return ClassName.get(androidContentPackageName(), contextClassName())
    }

    private static ParameterizedTypeName listOfFeaturesTypeName() {
        return ParameterizedTypeName.get(List.class, Feature.class)
    }

    private static String flipTheSwitchPackageName() {
        return "com.github.michaelengland.fliptheswitch"
    }

    private static String androidContentPackageName() {
        return "android.content"
    }

    private static String featuresClassName() {
        return "Features"
    }

    private static String flipTheSwitchClassName() {
        return "FlipTheSwitch"
    }

    private static String contextClassName() {
        return "Context"
    }
}
