package de._125m125.approximateTemporalDataStructure.aggregators;

public interface Aggregator<T> {
    public T aggregate(T a, T b);

    public T partial(T value, double percentage);

    public T identity();

}
