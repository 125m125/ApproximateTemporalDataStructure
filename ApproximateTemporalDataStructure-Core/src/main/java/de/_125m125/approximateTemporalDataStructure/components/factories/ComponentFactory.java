package de._125m125.approximateTemporalDataStructure.components.factories;

import de._125m125.approximateTemporalDataStructure.ComponentSettings;
import de._125m125.approximateTemporalDataStructure.SelectionWindow;
import de._125m125.approximateTemporalDataStructure.components.AggregatedComponent;
import de._125m125.approximateTemporalDataStructure.components.Component;

public interface ComponentFactory<T> {
    public Component<T> generateComponent(int level, Component<T> blueprint);

    public Component<T> generateComponent(ComponentSettings<T> settings, int level, long minTime, long minY);

    public Component<T> generateComponent(ComponentSettings<T> settings, int level, long minTime, long maxTime,
            long minY, long maxY);

    public Component<T> performYSplit(ComponentSettings<T> settings, int level, long minTime, int minY);

    public AggregatedComponent<T> generateAggregatedComponent(final ComponentSettings<T> settings, final long minTime,
            final long maxTime, final long minY, final long maxY, final int level);

    public default SelectionWindow[][] getRecommendedSelectionWindows(final long minTime, final long maxTime,
            final long minY, final long maxY, final long realStartTime, final int timeCount, final int yCount) {
        final SelectionWindow[][] result = new SelectionWindow[timeCount][yCount];
        final double timeStepSize = (maxTime - minTime) / timeCount;
        final double yStepSize = (minY - maxY) / yCount;
        double currTime = minTime;
        for (int i = 0; i < timeCount; i++) {
            double currY = minY;
            for (int j = 0; j < yCount; j++) {
                result[i][j] = new SelectionWindow(Math.round(currTime), Math.round(currTime + timeStepSize),
                        Math.round(currY), Math.round(currY += yStepSize));
            }
            currTime += timeStepSize;
        }
        return result;
    }

}
