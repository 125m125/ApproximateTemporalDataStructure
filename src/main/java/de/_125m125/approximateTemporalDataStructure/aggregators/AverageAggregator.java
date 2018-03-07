package de._125m125.approximateTemporalDataStructure.aggregators;

import java.util.function.Function;

public class AverageAggregator<T extends Number> implements Aggregator<WeightedValue<T>> {
    public static AverageAggregator<Double> getDoubleAverageAggregator() {
        return new AverageAggregator<>(e -> e);
    }

    public static AverageAggregator<Integer> getIntegerAverageAggregator() {
        return new AverageAggregator<>(e -> {
            final long round = Math.round(e);
            if (round > Integer.MAX_VALUE || round < Integer.MIN_VALUE) {
                throw new ArithmeticException(round + " is out of bounds");
            } else {
                return (int) round;
            }
        });
    }

    private final WeightedValue<T>    IDENTITY;
    private final Function<Double, T> converter;

    public AverageAggregator(final Function<Double, T> converter) {
        this.converter = converter;
        this.IDENTITY = new WeightedValue<>(converter.apply(1d), 0d);
    }

    @Override
    public WeightedValue<T> aggregate(final WeightedValue<T> a, final WeightedValue<T> b) {
        if (a == this.IDENTITY && b == this.IDENTITY) {
            return this.IDENTITY;
        }
        final double totalWeight = a.getWeight() + b.getWeight();
        final double sum = a.getValue().doubleValue() * a.getWeight() + b.getValue().doubleValue() * b.getWeight();
        return new WeightedValue<>(this.converter.apply(sum / totalWeight), totalWeight);
    }

    @Override
    public WeightedValue<T> partial(final WeightedValue<T> value, final double percentage) {
        return new WeightedValue<>(value.getValue(), percentage * value.getWeight());
    }

    @Override
    public WeightedValue<T> identity() {
        return this.IDENTITY;
    }
}
