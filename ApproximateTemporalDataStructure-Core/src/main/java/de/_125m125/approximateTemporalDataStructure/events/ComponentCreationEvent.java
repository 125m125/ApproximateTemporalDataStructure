package de._125m125.approximateTemporalDataStructure.events;

import de._125m125.approximateTemporalDataStructure.components.Component;

public class ComponentCreationEvent<T> {
    private final Component<T> newComponent;
    private final Component<T> container;
    private final int          timeIndex;
    private final int          yIndex;

    public ComponentCreationEvent(final Component<T> newComponent, final Component<T> container, final int timeIndex,
            final int yIndex) {
        super();
        this.newComponent = newComponent;
        this.container = container;
        this.timeIndex = timeIndex;
        this.yIndex = yIndex;
    }

    public Component<T> getNewComponent() {
        return this.newComponent;
    }

    public Component<T> getContainer() {
        return this.container;
    }

    public int getTimeIndex() {
        return this.timeIndex;
    }

    public int getyIndex() {
        return this.yIndex;
    }

}
