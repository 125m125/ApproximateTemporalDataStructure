package de._125m125.approximateTemporalDataStructure.events;

public interface ComponentEventHandler<T> {
    public void onComponentCreation(ComponentCreationEvent<T> componentCreationEvent);

    public void onComponentCompression(ComponentCompressionEvent<T> componentCompressionEvent);

    public void onValueModification(ValueModificationEvent<T> valueModificationEvent);
}
