package de._125m125.approximateTemporalDataStructure;

import de._125m125.approximateTemporalDataStructure.components.Component;

public class ApproximateTemporalDataStructure<T> {
    private Component<T>               root;
    private final ComponentSettings<T> settings;

    private final long                 startTime;

    public ApproximateTemporalDataStructure(final ComponentSettings<T> settings, final long startTime) {
        this.settings = settings;
        this.startTime = startTime;
    }

    public long addEntry(final long time, final long y, final T value) {
        checkRoot();
        long changes = 0;
        while (y >= this.root.getMaxY()) {
            System.out.println("y-split");
            final Component<T> generatedComponent = this.settings.getComponentFactory().performYSplit(this.settings,
                    this.root.getLevel() + 1, this.startTime, 0);
            changes = generatedComponent.getWeight();
            changes += generatedComponent.setFirstChild(this.root);
            this.root = generatedComponent;

        }
        while (time >= this.root.getMaxTime()) {
            System.out.println("time-split");
            final Component<T> generatedComponent = this.settings.getComponentFactory().generateComponent(this.settings,
                    this.root.getLevel() + 1, this.startTime, 0);
            changes = generatedComponent.getWeight();
            changes += generatedComponent.setFirstChild(this.root);
            this.root = generatedComponent;
        }
        changes += this.root.addEntry(time, y, value);
        // TODO: compress by added size if too large
        return changes;
    }

    public T getAggregatedValue(final long startTime, final long endTime, final long minY, final long maxY) {
        checkRoot();
        if (minY >= this.root.getMaxY() || maxY < this.root.getMinY() || startTime >= this.root.getMaxTime()
                || endTime < this.root.getMinTime()) {
            return this.settings.getAggregator().identity();
        }
        return this.root.getAggregatedValue(startTime, endTime, minY, maxY);
    }

    private void checkRoot() {
        if (this.root == null) {
            this.root = this.settings.getComponentFactory().generateComponent(this.settings, 0, this.startTime, 0);
        }
    }
}
