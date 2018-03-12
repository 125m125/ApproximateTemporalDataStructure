package de._125m125.approximateTemporalDataStructure.events;

import de._125m125.approximateTemporalDataStructure.components.Component;

public class ValueModificationEvent<T> {
    private final T            modificationValue;
    private final int          timeIndex;
    private final int          yIndex;
    private final Component<T> container;

    public ValueModificationEvent(final T modificationValue, final int timeIndex, final int yIndex,
            final Component<T> container) {
        super();
        this.modificationValue = modificationValue;
        this.timeIndex = timeIndex;
        this.yIndex = yIndex;
        this.container = container;
    }

    public T getModificationValue() {
        return this.modificationValue;
    }

    public int getTimeIndex() {
        return this.timeIndex;
    }

    public int getyIndex() {
        return this.yIndex;
    }

    public Component<T> getContainer() {
        return this.container;
    }

}
