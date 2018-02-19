package com.github.michaelengland.fliptheswitch.gradle

import com.github.michaelengland.fliptheswitch.Feature
import com.google.common.base.CaseFormat
import com.squareup.javapoet.*
import javax.lang.model.element.Modifier

open class FeaturesWriter(private val defaultFeatures: List<Feature>) {
    fun build(): JavaFile =
            JavaFile.builder(flipTheSwitchPackageName, type())
                    .build()

    private fun type(): TypeSpec {
        val typeBuilder = TypeSpec.classBuilder(featuresTypeName)
                .addModifiers(Modifier.PUBLIC)
                .addStaticBlock(setDefaultFeaturesBlock())
                .addField(flipTheSwitchTypeName, "flipTheSwitch", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(publicConstructor())
                .addMethod(internalConstructor())
        defaultFeatures.forEach {
            typeBuilder.addMethod(isFeatureEnabledMethod(it))
                    .addMethod(enableFeatureMethod(it))
                    .addMethod(disableFeatureMethod(it))
                    .addMethod(setFeatureEnabledMethod(it))
                    .addMethod(resetFeatureMethod(it))
        }
        return typeBuilder
                .addMethod(resetAllFeaturesMethod())
                .build()
    }

    private fun setDefaultFeaturesBlock(): CodeBlock {
        val defaultFeaturesMethodBuilder = CodeBlock.builder()
                .addStatement("\$T defaultFeatures = new \$T<>()", listOfFeaturesTypeName, ArrayList::class.java)
        defaultFeatures.forEach {
            defaultFeaturesMethodBuilder.addStatement("defaultFeatures.add(new \$T(\$S, \$S, \$L))", Feature::class.java,
                    snakeCaseFeatureName(it), it.description, it.isEnabled)

        }
        return defaultFeaturesMethodBuilder.addStatement("\$T.defaultFeatures = defaultFeatures",
                flipTheSwitchTypeName)
                .build()
    }

    private fun internalConstructor() =
            MethodSpec.constructorBuilder()
                    .addParameter(flipTheSwitchTypeName, "flipTheSwitch", Modifier.FINAL)
                    .addStatement("this.\$N = \$N", "flipTheSwitch", "flipTheSwitch")
                    .build()

    private fun publicConstructor() =
            MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(contextTypeName, "context", Modifier.FINAL)
                    .addStatement("this(new \$T(\$N))", flipTheSwitchTypeName, "context")
                    .build()

    private fun isFeatureEnabledMethod(feature: Feature) =
            MethodSpec.methodBuilder("is" + camelCaseFeatureName(feature) + "Enabled")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(Boolean::class.java)
                    .addStatement("return \$N.isFeatureEnabled(\$S)", "flipTheSwitch",
                            snakeCaseFeatureName(feature))
                    .build()

    private fun enableFeatureMethod(feature: Feature) =
            MethodSpec.methodBuilder("enable" + camelCaseFeatureName(feature))
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("\$N.enableFeature(\$S)", "flipTheSwitch", snakeCaseFeatureName(feature))
                    .build()

    private fun disableFeatureMethod(feature: Feature) =
            MethodSpec.methodBuilder("disable" + camelCaseFeatureName(feature))
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("\$N.disableFeature(\$S)", "flipTheSwitch", snakeCaseFeatureName(feature))
                    .build()

    private fun setFeatureEnabledMethod(feature: Feature) =
            MethodSpec.methodBuilder("set" + camelCaseFeatureName(feature) + "Enabled")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(Boolean::class.java, "enabled", Modifier.FINAL)
                    .addStatement("\$N.setFeatureEnabled(\$S, \$N)", "flipTheSwitch", snakeCaseFeatureName(feature),
                            "enabled")
                    .build()

    private fun resetFeatureMethod(feature: Feature) =
            MethodSpec.methodBuilder("reset" + camelCaseFeatureName(feature))
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("\$N.resetFeature(\$S)", "flipTheSwitch", snakeCaseFeatureName(feature))
                    .build()

    private fun snakeCaseFeatureName(feature: Feature) =
            CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, feature.name)

    private fun camelCaseFeatureName(feature: Feature) =
            CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, feature.name)

    private fun resetAllFeaturesMethod() =
            MethodSpec.methodBuilder("resetAllFeatures")
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("\$N.resetAllFeatures()", "flipTheSwitch")
                    .build()

    private val flipTheSwitchPackageName = "com.github.michaelengland.fliptheswitch"
    private val androidContentPackageName = "android.content"
    private val featuresClassName = "Features"
    private val flipTheSwitchClassName = "FlipTheSwitch"
    private val contextClassName = "Context"
    private val flipTheSwitchTypeName = ClassName.get(flipTheSwitchPackageName, flipTheSwitchClassName)
    private val featuresTypeName = ClassName.get(flipTheSwitchPackageName, featuresClassName)
    private val contextTypeName = ClassName.get(androidContentPackageName, contextClassName)
    private val listOfFeaturesTypeName = ParameterizedTypeName.get(List::class.java, Feature::class.java)
}
