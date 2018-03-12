package de._125m125.approximateTemporalDataStructure;

import java.util.Optional;

import de._125m125.approximateTemporalDataStructure.aggregators.Aggregator;
import de._125m125.approximateTemporalDataStructure.components.factories.ComponentFactory;
import de._125m125.approximateTemporalDataStructure.events.ComponentCompressionEvent;
import de._125m125.approximateTemporalDataStructure.events.ComponentCreationEvent;
import de._125m125.approximateTemporalDataStructure.events.ComponentEventHandler;
import de._125m125.approximateTemporalDataStructure.events.ValueModificationEvent;

public class ComponentSettings<T> implements ComponentEventHandler<T> {
    private final Aggregator<T>                      aggregator;
    private final ComponentFactory<T>                factory;
    private final Optional<ComponentEventHandler<T>> eventhandler;

    public ComponentSettings(final Aggregator<T> aggregator, final ComponentFactory<T> factory) {
        this.factory = factory;
        this.aggregator = aggregator;
        this.eventhandler = Optional.empty();
    }

    public ComponentSettings(final Aggregator<T> aggregator, final ComponentFactory<T> factory,
            final ComponentEventHandler<T> eventhandler) {
        this.factory = factory;
        this.aggregator = aggregator;
        this.eventhandler = Optional.of(eventhandler);
    }

    public Aggregator<T> getAggregator() {
        return this.aggregator;
    }

    public ComponentFactory<T> getComponentFactory() {
        return this.factory;
    }

    @Override
    public void onComponentCreation(final ComponentCreationEvent<T> componentCreationEvent) {
        this.eventhandler.ifPresent(eh -> eh.onComponentCreation(componentCreationEvent));
    }

    @Override
    public void onComponentCompression(final ComponentCompressionEvent<T> componentCompressionEvent) {
        this.eventhandler.ifPresent(eh -> eh.onComponentCompression(componentCompressionEvent));
    }

    @Override
    public void onValueModification(final ValueModificationEvent<T> valueModificationEvent) {
        this.eventhandler.ifPresent(eh -> eh.onValueModification(valueModificationEvent));
    }

}
