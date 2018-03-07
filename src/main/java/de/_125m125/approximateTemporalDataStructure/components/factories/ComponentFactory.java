package de._125m125.approximateTemporalDataStructure.components.factories;

import de._125m125.approximateTemporalDataStructure.ComponentSettings;
import de._125m125.approximateTemporalDataStructure.components.Component;

public interface ComponentFactory<T> {
    public Component<T> generateComponent(int level, Component<T> blueprint);

    public Component<T> generateComponent(ComponentSettings<T> settings, int level, long minTime, long minY);

    public Component<T> generateComponent(ComponentSettings<T> settings, int level, long minTime, long maxTime,
            long minY, long maxY);

    public Component<T> performYSplit(ComponentSettings<T> settings, int level, long minTime, int minY);

}
