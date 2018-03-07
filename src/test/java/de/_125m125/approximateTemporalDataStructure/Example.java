package de._125m125.approximateTemporalDataStructure;

import java.util.Random;

import de._125m125.approximateTemporalDataStructure.ApproximateTemporalDataStructure;
import de._125m125.approximateTemporalDataStructure.ComponentSettings;
import de._125m125.approximateTemporalDataStructure.aggregators.AverageAggregator;
import de._125m125.approximateTemporalDataStructure.aggregators.WeightedValue;
import de._125m125.approximateTemporalDataStructure.components.ArrayComponent;
import de._125m125.approximateTemporalDataStructure.components.D2BTreeComponent;
import de._125m125.approximateTemporalDataStructure.components.factories.SimpleComponentFactory;

public class Example {
    public static final long HALF_TIME_BOUND = 365l * 24 * 60 * 60 * 1000 * 2;

    public static void main(final String[] args) {
        long size = 2_000_000;
        if (args.length > 0) {
            size = Long.parseLong(args[0]);
        }

        final ApproximateTemporalDataStructure<WeightedValue<Double>> structure = new ApproximateTemporalDataStructure<>(
                new ComponentSettings<>(AverageAggregator.getDoubleAverageAggregator(), new SimpleComponentFactory<>()),
                0l);

        final Random r = new Random();
        System.out.println("generating data structure of size " + size + "...");
        long start = System.nanoTime();
        for (long i = 0; i < size; i++) {
            if (i % (size / 100) == 0) {
                System.out.println(i + "/" + size);
            }
            structure.addEntry(r.nextLong() % Example.HALF_TIME_BOUND + Example.HALF_TIME_BOUND, r.nextInt(320),
                    new WeightedValue<>(r.nextDouble(), 1));
        }
        System.out.println("generated structure in " + (System.nanoTime() - start) / 1_000_000_000 + " seconds");
        System.out.println("=========================================================");
        ArrayComponent.touchCount = 0;
        D2BTreeComponent.lowestLevel = Integer.MAX_VALUE;

        System.out.println("gettings first 1000 days with aggregation-bounds");
        start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            structure.getAggregatedValue(i * 86400000l, (i + 1) * 86400000l, 0, 160).getValue();
        }
        System.out.println(
                "get with aggregation-bounds took " + (System.nanoTime() - start) / 1_000_000_000.0 + " seconds");
        System.out.println("times an arraycomponent was touched: " + ArrayComponent.touchCount);
        System.out.println("lowest accessed D2BTreeComponent: " + D2BTreeComponent.lowestLevel);

        System.out.println("=========================================================");
        ArrayComponent.touchCount = 0;
        D2BTreeComponent.lowestLevel = Integer.MAX_VALUE;
        System.out.println("gettings first ~1000 days without aggregation-bounds");
        start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            structure.getAggregatedValue(i * 86400000l - 1, (i + 1) * 86400000l - 1, 0, 159);
        }
        System.out.println(
                "get without aggregation-bounds took " + (System.nanoTime() - start) / 1_000_000_000.0 + " seconds");
        System.out.println("times an arraycomponent was touched: " + ArrayComponent.touchCount);
        System.out.println("lowest accessed D2BTreeComponent: " + D2BTreeComponent.lowestLevel);
    }
}
