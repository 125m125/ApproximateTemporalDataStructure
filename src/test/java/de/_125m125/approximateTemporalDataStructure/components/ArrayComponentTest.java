package de._125m125.approximateTemporalDataStructure.components;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de._125m125.approximateTemporalDataStructure.ComponentSettings;
import de._125m125.approximateTemporalDataStructure.aggregators.SumAggregator;
import de._125m125.approximateTemporalDataStructure.components.ArrayComponent;

public class ArrayComponentTest {

    ArrayComponent<Double> uut;

    @Before
    public void beforeArrayComponentTest() {
        this.uut = new ArrayComponent<>(new ComponentSettings<>(SumAggregator.getDoubleSumAggregator(), null), 0, 0,
                3600000l, 4, 4);
    }

    @Test
    public void testGetApproximateValue_unset() throws Exception {
        assertEquals(0d, this.uut.getApproximateValue(0l, 0), 1e-10);
    }

    @Test
    public void testGetApproximateValue() throws Exception {
        this.uut.addEntry(0l, 0l, 1.0);
        this.uut.addEntry(1800000l, 1l, 2.0);
        this.uut.addEntry(3599999l + 3600000l, 1l, 4.0);

        assertEquals(1d, this.uut.getApproximateValue(0l, 0), 1e-10);
        assertEquals(1d, this.uut.getApproximateValue(1800000l, 0), 1e-10);
        assertEquals(1d, this.uut.getApproximateValue(3599999l, 0), 1e-10);

        assertEquals(2d, this.uut.getApproximateValue(0l, 1l), 1e-10);
        assertEquals(2d, this.uut.getApproximateValue(1800000l, 1l), 1e-10);
        assertEquals(2d, this.uut.getApproximateValue(3599999l, 1l), 1e-10);

        assertEquals(4d, this.uut.getApproximateValue(0L + 3600000l, 1l), 1e-10);
        assertEquals(4d, this.uut.getApproximateValue(1800000l + 3600000l, 1l), 1e-10);
        assertEquals(4d, this.uut.getApproximateValue(3599999l + 3600000l, 1l), 1e-10);
    }

    @Test
    public void testGetApproximateValue_added() throws Exception {
        this.uut.addEntry(0l, 0l, 1.0);
        this.uut.addEntry(1800000l, 0l, 2.0);
        this.uut.addEntry(3599999l, 0l, 4.0);

        assertEquals(7d, this.uut.getApproximateValue(200L, 0), 1e-10);
    }

    @Test
    public void testGetAggregatedValue_singleLeftPartial() throws Exception {
        generateGrid();
        assertEquals(1800000d, this.uut.getAggregatedValue(0, 1800000, 0, 0), 1e-10);
        assertEquals(1800000d, this.uut.getAggregatedValue(-180000, 1800000, 0, 0), 1e-10);
        assertEquals(3600000d * 4 / 2, this.uut.getAggregatedValue(3600000 * 3, 3600000 * 3 + 1800000, 0, 0), 1e-10);
    }

    @Test
    public void testGetAggregatedValue_singleRightPartial() throws Exception {
        generateGrid();
        assertEquals(1800000d, this.uut.getAggregatedValue(1800000, 3600000, 0, 0), 1e-10);
        assertEquals(3600000d * 4 / 2, this.uut.getAggregatedValue(3600000 * 3 + 1800000, 3600000 * 4 + 1800000, 0, 0),
                1e-10);
        assertEquals(3600000d * 4 / 2, this.uut.getAggregatedValue(3600000 * 3 + 1800000, 3600000 * 4, 0, 0), 1e-10);
    }

    @Test
    public void testGetAggregatedValue_singleFull() throws Exception {
        generateGrid();
        assertEquals(3600000d, this.uut.getAggregatedValue(0, 3600000, 0, 0), 1e-10);
        assertEquals(3600000d, this.uut.getAggregatedValue(-3600000, 3600000, 0, 0), 1e-10);
        assertEquals(3600000d * 4, this.uut.getAggregatedValue(3600000 * 3, 3600000 * 4, 0, 0), 1e-10);
        assertEquals(3600000d * 4, this.uut.getAggregatedValue(3600000 * 3, 3600000 * 5, 0, 0), 1e-10);
    }

    @Test
    public void testGetAggregatedValue_multipleFull() throws Exception {
        generateGrid();
        assertEquals(3600000d * 3, this.uut.getAggregatedValue(0, 7200000, 0, 0), 1e-10);
        assertEquals(3600000d * 3, this.uut.getAggregatedValue(-3600000, 7200000, 0, 0), 1e-10);
        assertEquals(3600000d * 7, this.uut.getAggregatedValue(3600000 * 2, 3600000 * 4, 0, 0), 1e-10);
        assertEquals(3600000d * 7, this.uut.getAggregatedValue(3600000 * 2, 3600000 * 5, 0, 0), 1e-10);
        assertEquals(3600000d * 10, this.uut.getAggregatedValue(-3600000, 3600000 * 5, 0, 0), 1e-10);
    }

    @Test
    public void testGetAggregatedValue_multiplePartials() throws Exception {
        generateGrid();
        assertEquals(3600000d * 2.5, this.uut.getAggregatedValue(1800000, 7200000, 0, 0), 1e-10);
        assertEquals(3600000d * 2, this.uut.getAggregatedValue(0, 3600000 * 3 / 2, 0, 0), 1e-10);
        assertEquals(3600000d * 4, this.uut.getAggregatedValue(1800000, 3600000 * 5 / 2, 0, 0), 1e-10);
    }

    @Test
    public void testGetAggregatedValue_complete() throws Exception {
        generateGrid();
        assertEquals(3600000d * 136, this.uut.getAggregatedValue(0, 3600000 * 4, 0, 4), 1e-10);
        assertEquals(3600000d * 136, this.uut.getAggregatedValue(-7200000, 3600000 * 10, -2, 10), 1e-10);
    }

    public void generateGrid() {
        for (int t = 0; t < 4; t++) {
            for (int z = 0; z < 4; z++) {
                this.uut.addEntry(t * 3600000, z, (t + z * 4 + 1) * 3600000d);
            }
        }
    }

}
