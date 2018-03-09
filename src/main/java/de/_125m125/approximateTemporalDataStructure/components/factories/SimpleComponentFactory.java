package de._125m125.approximateTemporalDataStructure.components.factories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de._125m125.approximateTemporalDataStructure.ComponentSettings;
import de._125m125.approximateTemporalDataStructure.components.AggregatedComponent;
import de._125m125.approximateTemporalDataStructure.components.ArrayComponent;
import de._125m125.approximateTemporalDataStructure.components.Component;
import de._125m125.approximateTemporalDataStructure.components.D2BTreeComponent;

public class SimpleComponentFactory<T> implements ComponentFactory<T> {

    private final List<Boolean> yCompressions  = new ArrayList<>();
    private final List<long[]>  componentSizes = new ArrayList<>();

    private final int[]         timeEntries;
    private final int[]         yEntries;
    private final long          timeIntervalSize;

    public SimpleComponentFactory(final int[] d2bTreeTimeEntries, final int[] d2bTreeYEntries,
            final long timeIntervalSize) {
        this.timeEntries = d2bTreeTimeEntries;
        this.yEntries = d2bTreeYEntries;
        this.timeIntervalSize = timeIntervalSize;

        this.yCompressions.add(true);
        this.componentSizes.add(new long[] { this.timeEntries[0] * timeIntervalSize, this.yEntries[0] });
    }

    public SimpleComponentFactory(final int[] d2bTreeTimeEntries, final int[] d2bTreeYEntries) {
        this(d2bTreeTimeEntries, d2bTreeYEntries, 60_000);
    }

    public SimpleComponentFactory(final int arraySizeTime, final int arraySizeY, final int D2BTreeTimeEntries,
            final int D2BTreeYEntries, final long timeIntervalSize) {
        this(new int[] { arraySizeTime, D2BTreeTimeEntries }, new int[] { arraySizeY, D2BTreeYEntries },
                timeIntervalSize);
    }

    public SimpleComponentFactory() {
        this(new int[] { 5, 2, 6, 6, 2, 2, 7, 4, 12, 2 }, new int[] { 5, 2 }, 60_000);
        // 5 minutes, 10 minutes, 1 hour, 6 hours, 12 hours, 1 day, 1 week, 1
        // month, 1 year
    }

    @Override
    public Component<T> generateComponent(final int level, final Component<T> blueprint) {
        return generateComponent(blueprint.getSettings(), level, blueprint.getMinTime(), blueprint.getMaxTime(),
                blueprint.getMinY(), blueprint.getMaxY());
    }

    @Override
    public Component<T> generateComponent(final ComponentSettings<T> settings, final int level, final long minTime,
            final long minY) {
        return generateComponent(settings, level, minTime, -1, minY, -1);
    }

    @Override
    public Component<T> generateComponent(final ComponentSettings<T> settings, final int level, final long minTime,
            final long maxTime, final long minY, final long maxY) {
        fillLevels(level);
        final long timeSize = this.componentSizes.get(level)[0];
        final long ySize = this.componentSizes.get(level)[1];
        if ((maxTime != -1 && maxTime - minTime != timeSize) || (maxY != -1 && maxY - minY != ySize)) {
            throw new IllegalArgumentException("expected size does not fit to the selected component level");
        }
        if (level == 0) {
            return createArrayComponent(settings, level, minTime, minY);
        } else if (this.yCompressions.get(level)) {
            return createD2BTree(settings, level, minTime, minY, timeSize, ySize);
        } else {
            return createD2BTree(settings, level, minTime, minY, timeSize, 1);
        }
    }

    private Component<T> createArrayComponent(final ComponentSettings<T> settings, final int level, final long minTime,
            final long minY) {
        return new ArrayComponent<>(settings, minTime, minY, this.timeIntervalSize, this.timeEntries[0],
                this.yEntries[0]);
    }

    private Component<T> createD2BTree(final ComponentSettings<T> settings, final int level, final long minTime,
            final long minY, final long timeSize, final long ySize) {
        return new D2BTreeComponent<>(settings, minTime, minTime + this.componentSizes.get(level)[0], minY,
                minY + this.componentSizes.get(level)[1], level, getIndexOrLast(this.timeEntries, level),
                this.yCompressions.get(level) ? getIndexOrLast(this.yEntries, level) : 1);
    }

    @Override
    public Component<T> performYSplit(final ComponentSettings<T> settings, final int level, final long minTime,
            final int minY) {
        if (this.yCompressions.size() > level) {
            if (!this.yCompressions.get(level)) {
                throw new IllegalStateException("Level " + level + " was already defined as a non-split level");
            } else {
                return generateComponent(settings, level, minTime, minY);
            }
        }
        fillLevels(level - 1);
        this.yCompressions.add(true);
        this.componentSizes.add(new long[] {
                this.componentSizes.get(this.componentSizes.size() - 1)[0]
                        * getIndexOrLast(this.timeEntries, this.componentSizes.size()),
                this.componentSizes.get(this.componentSizes.size() - 1)[1]
                        * getIndexOrLast(this.yEntries, this.componentSizes.size()) });
        System.out.println(this.componentSizes.stream().map(Arrays::toString).collect(Collectors.joining(",")));
        return generateComponent(settings, level, minTime, minY);
    }

    private void fillLevels(final int level) {
        while (this.yCompressions.size() <= level) {
            System.out.println(this.componentSizes.stream().map(Arrays::toString).collect(Collectors.joining(",")));
            if (level < this.yEntries.length) {
                this.yCompressions.add(this.yEntries[level] > 1);
                this.componentSizes.add(new long[] {
                        this.componentSizes.get(this.componentSizes.size() - 1)[0]
                                * getIndexOrLast(this.timeEntries, this.componentSizes.size()),
                        this.componentSizes.get(this.componentSizes.size() - 1)[1] * this.yEntries[level] });
            } else {
                this.yCompressions.add(false);
                this.componentSizes.add(new long[] {
                        this.componentSizes.get(this.componentSizes.size() - 1)[0]
                                * getIndexOrLast(this.timeEntries, this.componentSizes.size()),
                        this.componentSizes.get(this.componentSizes.size() - 1)[1] });
            }
        }
    }

    private int getIndexOrLast(final int[] array, final int index) {
        if (index < array.length) {
            return array[index];
        } else {
            return array[array.length - 1];
        }
    }

    @Override
    public AggregatedComponent<T> generateAggregatedComponent(final ComponentSettings<T> settings, final long minTime,
            final long maxTime, final long minY, final long maxY, final int level) {
        return new AggregatedComponent<>(settings, minTime, maxTime, minY, maxY, level);
    }

}
