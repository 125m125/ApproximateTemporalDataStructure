package de._125m125.approximateTemporalDataStructure;

public class SelectionWindow {
    private final long startTime;
    private final long endTime;
    private final long minY;
    private final long maxY;

    public SelectionWindow(final long startTime, final long endTime, final long minY, final long maxY) {
        super();
        this.startTime = startTime;
        this.endTime = endTime;
        this.minY = minY;
        this.maxY = maxY;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public long getMinY() {
        return this.minY;
    }

    public long getMaxY() {
        return this.maxY;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (this.endTime ^ (this.endTime >>> 32));
        result = prime * result + (int) (this.maxY ^ (this.maxY >>> 32));
        result = prime * result + (int) (this.minY ^ (this.minY >>> 32));
        result = prime * result + (int) (this.startTime ^ (this.startTime >>> 32));
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SelectionWindow)) {
            return false;
        }
        final SelectionWindow other = (SelectionWindow) obj;
        if (this.endTime != other.endTime) {
            return false;
        }
        if (this.maxY != other.maxY) {
            return false;
        }
        if (this.minY != other.minY) {
            return false;
        }
        if (this.startTime != other.startTime) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SelectionWindow [startTime=" + this.startTime + ", endTime=" + this.endTime + ", minY=" + this.minY
                + ", maxY=" + this.maxY + "]";
    }

}
