package de._125m125.approximateTemporalDataStructure;

import de._125m125.approximateTemporalDataStructure.aggregators.Aggregator;
import de._125m125.approximateTemporalDataStructure.components.factories.ComponentFactory;

public class ComponentSettings<T> {
    private final Aggregator<T>       aggregator;
    private final ComponentFactory<T> factory;

    public ComponentSettings(final Aggregator<T> aggregator, final ComponentFactory<T> factory) {
        this.factory = factory;
        this.aggregator = aggregator;
    }

    public Aggregator<T> getAggregator() {
        return this.aggregator;
    }

    public ComponentFactory<T> getComponentFactory() {
        return this.factory;
    }

}
