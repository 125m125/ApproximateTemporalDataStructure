package de._125m125.approximateTemporalDataStructure.components;

import de._125m125.approximateTemporalDataStructure.ComponentSettings;

public class AggregatedComponent<T> extends Component<T> {

    private T            aggregatedValue;
    private Component<T> component;
    private boolean      aggregated = false;

    public AggregatedComponent(final ComponentSettings<T> settings, final long minTime, final long maxTime,
            final long minY, final long maxY, final int level) {
        super(settings, minTime, maxTime, minY, maxY, level, 1);

        this.aggregatedValue = getSettings().getAggregator().identity();
    }

    public T getAggregatedValue() {
        return this.aggregatedValue;
    }

    public void setAggregatedValue(final T aggregatedValue) {
        this.aggregatedValue = aggregatedValue;
    }

    public void addValueToAggregation(final T newValue) {
        setAggregatedValue(getSettings().getAggregator().aggregate(this.aggregatedValue, newValue));
    }

    public Component<T> getComponent() {
        return this.component;
    }

    public void compress() {
        this.component = null;
        this.aggregated = true;
    }

    @Override
    public long innerAddEntry(final long time, final long y, final T value) {
        addValueToAggregation(value);
        if (this.aggregated) {
            return 0;
        }
        long generatedWeight = 0;
        if (this.component == null) {
            generateComponent();
            generatedWeight = this.component.getWeight();
        }
        return generatedWeight + this.component.addEntry(time, y, value);
    }

    private void generateComponent() {
        this.component = getSettings().getComponentFactory().generateComponent(getLevel(), this);
    }

    @Override
    public T getApproximateValue(final long time, final long y) {
        if (this.aggregated) {
            throw new UnsupportedOperationException(
                    "approximate values can currently not be requested from aggregated components");
            // final double totalSpaces = getContainedSpaces();
            // return
            // getSettings().getAggregator().partial(this.aggregatedValue, 1 /
            // totalSpaces);
        } else if (this.component == null) {
            return getSettings().getAggregator().identity();
        } else {
            return this.component.getApproximateValue(time, y);
        }
    }

    @Override
    public T getAggregatedValue(final long startTime, final long endTime, final long minY, final long maxY) {
        if (startTime <= getMinTime() && endTime >= getMaxTime() && minY <= getMinY() && maxY >= getMaxY()) {
            return this.aggregatedValue;
        }

        if (this.aggregated) {
            final long modStartTime = Math.max(startTime, getMinTime());
            final long modEndTime = Math.min(endTime, getMaxTime());
            final long modMinY = Math.max(minY, getMinY());
            final long modMaxY = Math.min(maxY, getMaxY());
            final double coveredArea = (modEndTime - modStartTime) * (modMaxY - modMinY);
            final double totalArea = (getMaxTime() - getMinTime()) * (getMaxY() - getMinY());
            return getSettings().getAggregator().partial(this.aggregatedValue, coveredArea / totalArea);
        } else if (this.component == null) {
            return getSettings().getAggregator().identity();
        } else {
            return this.component.getAggregatedValue(startTime, endTime, minY, maxY);
        }
    }

    @Override
    public long setFirstChild(final Component<T> component) {
        if (component.getMinTime() == getMinTime() && component.getMaxTime() == getMaxTime()
                && component.getMinY() == getMinY() && component.getMaxY() == getMaxY()) {
            this.component = component;
            return 0;
        }
        throw new IllegalArgumentException("AggregatedComponents can only accept components that match their ranges");
    }

}
