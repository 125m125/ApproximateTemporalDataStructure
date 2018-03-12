package de._125m125.approximateTemporalDataStructure.components;

import de._125m125.approximateTemporalDataStructure.ComponentSettings;
import de._125m125.approximateTemporalDataStructure.aggregators.Aggregator;
import de._125m125.approximateTemporalDataStructure.events.ValueModificationEvent;

public class ArrayComponent<T> extends Component<T> {
    public static int        touchCount = 0;

    private final Object[][] values;
    private final long       timeIntervalSize;
    private final int        arraySizeX;
    private final int        arraySizeY;

    public ArrayComponent(final ComponentSettings<T> settings, final long minTime, final long minY,
            final long timeIntervalSize, final int arraySizeX, final int arraySizeY) {
        super(settings, minTime, minTime + timeIntervalSize * arraySizeX, minY, minY + arraySizeY, 0, 1);
        this.timeIntervalSize = timeIntervalSize;
        this.arraySizeX = arraySizeX;
        this.arraySizeY = arraySizeY;
        this.values = new Object[arraySizeX][arraySizeY];
    }

    @Override
    public long innerAddEntry(final long time, final long y, final T value) {
        long generatedWeight = 0;
        final T current = getAtIndex(getTimeIndex(time), getYIndex(y));
        if (current != null) {
            set(time, y, getSettings().getAggregator().aggregate(current, value));
        } else {
            set(time, y, value);
            generatedWeight = 1;
        }
        getSettings().onValueModification(new ValueModificationEvent<>(value, getTimeIndex(time), getYIndex(y), this));
        return generatedWeight;
    }

    @Override
    public T getApproximateValue(final long time, final long y) {
        return this.getAtIndexOrIdentity(getTimeIndex(time), getYIndex(y));
    }

    @SuppressWarnings("unchecked")
    public T getAtIndex(final int time, final int y) {
        if (time < 0 || time > this.values.length) {// FIXME this should not
                                                    // happen...
            return getSettings().getAggregator().identity();
        }
        return (T) this.values[time][y];
    }

    private void set(final long time, final long y, final T value) {
        this.values[getTimeIndex(time)][getYIndex(y)] = value;
    }

    public T getAtIndexOrIdentity(final int time, final int y) {
        final T atIndex = getAtIndex(time, y);
        if (atIndex == null) {
            return getSettings().getAggregator().identity();
        }
        return atIndex;
    }

    @Override
    public T getAggregatedValue(final long startTime, final long endTime, final long minY, final long maxY) {
        ArrayComponent.touchCount++;
        int t0 = getTimeIndex(startTime);
        double leftWeight;
        if (t0 < 0) {
            t0 = 0;
            leftWeight = 1d;
        } else {
            leftWeight = 1 - (startTime % this.timeIntervalSize) / (double) this.timeIntervalSize;
        }

        int te = getTimeIndex(endTime);
        final double rightWeight;
        if (te >= this.values.length) {
            te = this.values.length - 1;
            rightWeight = 1d;
        } else {
            rightWeight = (endTime % this.timeIntervalSize) / (double) this.timeIntervalSize;
        }
        final int y0 = Math.max(getYIndex(minY), 0);
        final int ye = Math.min(getYIndex(maxY) + 1, this.arraySizeY);
        final Aggregator<T> aggregator = getSettings().getAggregator();

        if (t0 == te) {
            if (leftWeight == rightWeight && leftWeight == 1) {
                return aggregateCol(t0, y0, ye, aggregator);
            }
            return aggregateColWeighted(t0, Math.abs(leftWeight - rightWeight), y0, ye, aggregator);
        }

        T result = aggregateColWeighted(t0, leftWeight, y0, ye, aggregator);
        for (int i = t0 + 1; i < te; i++) {
            result = aggregator.aggregate(result, aggregateCol(i, y0, ye, aggregator));
        }
        result = aggregator.aggregate(result, aggregateColWeighted(te, rightWeight, y0, ye, aggregator));
        return result;
    }

    private T aggregateColWeighted(final int time, final double weight, final int y0, final int ye,
            final Aggregator<T> aggregator) {
        T result = aggregator.identity();
        for (int i = y0; i < ye; i++) {
            final T atIndexOrIdentity = getAtIndexOrIdentity(time, i);
            final T next = aggregator.partial(atIndexOrIdentity, weight);
            result = aggregator.aggregate(result, next);
        }
        return result;
    }

    private T aggregateCol(final int time, final int y0, final int ye, final Aggregator<T> aggregator) {
        T result = aggregator.identity();
        for (int i = y0; i < ye; i++) {
            final T next = getAtIndexOrIdentity(time, i);
            result = aggregator.aggregate(result, next);
        }
        return result;
    }

    private int getTimeIndex(final long time) {
        if (time - getMinTime() < 0) {
            return -1;
        }
        return (int) ((time - getMinTime()) / this.timeIntervalSize);
    }

    private int getYIndex(final long y) {
        return (int) (y - getMinY());
    }

    @Override
    public long setFirstChild(final Component<T> component) {
        throw new UnsupportedOperationException("ArrayComponents do not have children");
    }

    @Override
    public long compress(final long compressionAmount) {
        return 0;
    }

}
