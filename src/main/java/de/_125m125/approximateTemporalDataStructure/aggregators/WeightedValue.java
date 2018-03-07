package de._125m125.approximateTemporalDataStructure.aggregators;

public class WeightedValue<T> {
    private final T      value;
    private final double weight;

    public WeightedValue(final T value, final double weight) {
        super();
        this.value = value;
        this.weight = weight;
    }

    public T getValue() {
        return this.value;
    }

    public double getWeight() {
        return this.weight;
    }

}
