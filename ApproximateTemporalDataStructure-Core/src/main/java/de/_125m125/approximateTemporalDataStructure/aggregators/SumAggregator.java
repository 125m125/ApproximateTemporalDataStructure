package de._125m125.approximateTemporalDataStructure.aggregators;

import java.util.function.BiFunction;

public class SumAggregator<T extends Number> implements Aggregator<T> {
    public static SumAggregator<Double> getDoubleSumAggregator() {
        return new SumAggregator<>(0d, (a, b) -> a + b, (a, b) -> a * b);
    }

    public static SumAggregator<Integer> getIntegerSumAggregator() {
        return new SumAggregator<>(0, (a, b) -> a + b, (a, b) -> (int) Math.round(a * b));
    }

    private final T                        identity;
    private final BiFunction<T, T, T>      sum;
    private final BiFunction<T, Double, T> product;

    public SumAggregator(final T identity, final BiFunction<T, T, T> sum, final BiFunction<T, Double, T> product) {
        this.identity = identity;
        this.sum = sum;
        this.product = product;
    }

    @Override
    public T aggregate(final T a, final T b) {
        return this.sum.apply(a, b);
    }

    @Override
    public T partial(final T value, final double percentage) {
        return this.product.apply(value, percentage);
    }

    @Override
    public T identity() {
        return this.identity;
    }

}
