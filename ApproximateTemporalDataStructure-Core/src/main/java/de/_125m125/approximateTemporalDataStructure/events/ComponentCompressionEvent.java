package de._125m125.approximateTemporalDataStructure.events;

import de._125m125.approximateTemporalDataStructure.components.Component;

public class ComponentCompressionEvent<T> {
    private final Component<T> component;

    public ComponentCompressionEvent(final Component<T> component) {
        super();
        this.component = component;
    }

    public Component<T> getComponent() {
        return this.component;
    }

}
