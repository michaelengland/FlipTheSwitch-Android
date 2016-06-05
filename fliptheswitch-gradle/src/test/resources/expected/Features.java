package com.github.michaelengland.fliptheswitch;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class Features {
  static {
    List<Feature> defaultFeatures = new ArrayList<>();
    defaultFeatures.add(new Feature("another_feature", "Another disabled feature", false));
    defaultFeatures.add(new Feature("some_feature", "Some simple enabled feature", true));
    FlipTheSwitch.defaultFeatures = defaultFeatures;
  }

  private final FlipTheSwitch flipTheSwitch;

  public Features(final Context context) {
    this(new FlipTheSwitch(context));
  }

  Features(final FlipTheSwitch flipTheSwitch) {
    this.flipTheSwitch = flipTheSwitch;
  }

  public boolean isAnotherFeatureEnabled() {
    return flipTheSwitch.isFeatureEnabled("another_feature");
  }

  public void enableAnotherFeature() {
    flipTheSwitch.enableFeature("another_feature");
  }

  public void disableAnotherFeature() {
    flipTheSwitch.disableFeature("another_feature");
  }

  public void setAnotherFeatureEnabled(final boolean enabled) {
    flipTheSwitch.setFeatureEnabled("another_feature", enabled);
  }

  public void resetAnotherFeature() {
    flipTheSwitch.resetFeature("another_feature");
  }

  public boolean isSomeFeatureEnabled() {
    return flipTheSwitch.isFeatureEnabled("some_feature");
  }

  public void enableSomeFeature() {
    flipTheSwitch.enableFeature("some_feature");
  }

  public void disableSomeFeature() {
    flipTheSwitch.disableFeature("some_feature");
  }

  public void setSomeFeatureEnabled(final boolean enabled) {
    flipTheSwitch.setFeatureEnabled("some_feature", enabled);
  }

  public void resetSomeFeature() {
    flipTheSwitch.resetFeature("some_feature");
  }

  public void resetAllFeatures() {
    flipTheSwitch.resetAllFeatures();
  }
}
