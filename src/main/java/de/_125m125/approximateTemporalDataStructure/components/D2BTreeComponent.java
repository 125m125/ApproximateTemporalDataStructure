package de._125m125.approximateTemporalDataStructure.components;

import de._125m125.approximateTemporalDataStructure.ComponentSettings;

public class D2BTreeComponent<T> extends Component<T> {

    public static int                        lowestLevel = Integer.MAX_VALUE;

    private final AggregatedComponent<T>[][] children;

    @SuppressWarnings("unchecked")
    public D2BTreeComponent(final ComponentSettings<T> settings, final long minTime, final long maxTime,
            final long minY, final long maxY, final int level, final int width, final int height) {
        super(settings, minTime, maxTime, minY, maxY, level, width * height);

        final long timeAdd = (maxTime - minTime) / width;
        final long yAdd = (maxY - minY) / height;
        this.children = new AggregatedComponent[width][height];
        // System.out.println("======");
        long currTime = minTime;
        for (int i = 0; i < width; i++) {
            long currY = minY;
            for (int j = 0; j < height; j++) {
                this.children[i][j] = new AggregatedComponent<>(settings, currTime, currTime + timeAdd, currY,
                        currY += yAdd, level - 1);
                // System.out.print(this.children[i][j].getMinTime() + "->" +
                // this.children[i][j].getMaxTime() + "|");
            }
            currTime += timeAdd;
            // System.out.println();
        }
    }

    @Override
    protected long innerAddEntry(final long time, final long y, final T value) {
        return this.children[getLeftTimeIndex(time)][getLeftYIndex(y)].addEntry(time, y, value);
    }

    @Override
    public T getApproximateValue(final long time, final long y) {
        return this.children[getLeftTimeIndex(time)][getLeftYIndex(y)].getApproximateValue(time, y);
    }

    @Override
    public T getAggregatedValue(final long startTime, final long endTime, final long minY, final long maxY) {
        D2BTreeComponent.lowestLevel = Math.min(D2BTreeComponent.lowestLevel, getLevel());
        final int startTIndex = getLeftTimeIndex(startTime);
        final int endTIndex = getRightTimeIndex(endTime);
        if (startTIndex == -1 || endTIndex == -1) {
            return getSettings().getAggregator().identity();
        }
        final int startYIndex = getLeftYIndex(minY);
        final int endYIndex = getRightYIndex(maxY);
        if (startYIndex == -1 || endYIndex == -1) {
            return getSettings().getAggregator().identity();
        }

        T result = getSettings().getAggregator().identity();
        for (int i = startTIndex; i <= endTIndex; i++) {
            for (int j = startYIndex; j <= endYIndex; j++) {
                result = getSettings().getAggregator().aggregate(result,
                        this.children[i][j].getAggregatedValue(startTime, endTime, minY, maxY));
            }
        }
        return result;
    }

    private int getLeftTimeIndex(final long time) {
        if (time <= this.children[0][0].getMinTime()) {
            return 0;
        }
        if (time >= this.children[this.children.length - 1][0].getMaxTime()) {
            return -1;
        }
        for (int i = 0; i < this.children.length; i++) {
            if (this.children[i][0].getMaxTime() > time) {
                return i;
            }
        }
        throw new RuntimeException("this should never be reached");
    }

    private int getRightTimeIndex(final long time) {
        if (time <= this.children[0][0].getMinTime()) {
            return -1;
        }
        if (time >= this.children[this.children.length - 1][0].getMaxTime()) {
            return this.children.length - 1;
        }
        for (int i = this.children.length - 1; i >= 0; i--) {
            if (this.children[i][0].getMinTime() < time) {
                return i;
            }
        }
        throw new RuntimeException("this should never be reached");
    }

    private int getLeftYIndex(final long y) {
        if (y <= this.children[0][0].getMinY()) {
            return 0;
        }
        if (y >= this.children[0][this.children[0].length - 1].getMaxY()) {
            return -1;
        }
        for (int i = 0; i < this.children[0].length; i++) {
            if (this.children[0][i].getMaxY() > y) {
                return i;
            }
        }
        throw new RuntimeException("this should never be reached");
    }

    private int getRightYIndex(final long y) {
        if (y <= this.children[0][0].getMinY()) {
            return -1;
        }
        if (y >= this.children[0][this.children[0].length - 1].getMaxY()) {
            return this.children[0].length - 1;
        }
        for (int i = this.children[0].length - 1; i >= 0; i--) {
            if (this.children[0][i].getMinY() < y) {
                return i;
            }
        }
        throw new RuntimeException("this should never be reached");
    }

    @Override
    public long setFirstChild(final Component<T> component) {
        setWeight(getWeight() + component.getWeight());
        return this.children[0][0].setFirstChild(component);
    }

    @Override
    public long compress(final long compressionAmount) {
        long remaining = compressionAmount;
        outer: for (int i = 0; i < this.children.length; i++) {
            for (int j = 0; j < this.children[0].length; j++) {
                remaining -= this.children[i][j].compress(remaining);
                if (remaining <= 0) {
                    break outer;
                }
            }
        }
        final long compressedWeight = compressionAmount - remaining;
        setWeight(getWeight() - compressedWeight);
        return compressedWeight;
    }

}
