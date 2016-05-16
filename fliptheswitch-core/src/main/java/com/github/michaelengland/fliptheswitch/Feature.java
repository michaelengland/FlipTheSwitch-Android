package com.github.michaelengland.fliptheswitch;

public class Feature {
    private final String name;
    private final String description;
    private final boolean enabled;

    public Feature(final String name, final String description, boolean enabled) {
        this.name = name;
        this.description = description;
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Feature feature = (Feature) o;

        if (enabled != feature.enabled) return false;
        if (!name.equals(feature.name)) return false;
        return description.equals(feature.description);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + (enabled ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Feature{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
