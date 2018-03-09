package de._125m125.approximateTemporalDataStructure.components;

import de._125m125.approximateTemporalDataStructure.ComponentSettings;

public abstract class Component<T> {
    private final ComponentSettings<T> settings;
    private final long                 minTime;
    private final long                 maxTime;
    private final long                 minY;
    private final long                 maxY;
    private final int                  level;
    private long                       weight;

    public Component(final ComponentSettings<T> settings, final long minTime, final long maxTime, final long minY,
            final long maxY, final int level, final long weight) {
        this.settings = settings;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.minY = minY;
        this.maxY = maxY;
        this.level = level;
        this.weight = weight;
    }

    public ComponentSettings<T> getSettings() {
        return this.settings;
    }

    public long getMinTime() {
        return this.minTime;
    }

    public long getMaxTime() {
        return this.maxTime;
    }

    public long getMinY() {
        return this.minY;
    }

    public long getMaxY() {
        return this.maxY;
    }

    public boolean containsTime(final long time) {
        return time < this.maxTime && time > this.minTime;
    }

    public long getWeight() {
        return this.weight;
    }

    public void setWeight(final long weight) {
        this.weight = weight;
    }

    public int getLevel() {
        return this.level;
    }

    public long addEntry(final long time, final long y, final T value) {
        final long generatedWeight = innerAddEntry(time, y, value);
        this.weight += generatedWeight;
        return generatedWeight;
    }

    protected abstract long innerAddEntry(final long time, final long y, final T value);

    public abstract T getApproximateValue(final long time, final long y);

    public abstract T getAggregatedValue(final long startTime, final long endTime, final long minY, final long maxY);

    public abstract long setFirstChild(Component<T> component);

    public abstract long compress(long compressionAmount);

}
