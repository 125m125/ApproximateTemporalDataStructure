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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
        long temp;
        temp = Double.doubleToLongBits(this.weight);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        if (!(obj instanceof WeightedValue)) {
            return false;
        }
        final WeightedValue<?> other = (WeightedValue<?>) obj;
        if (this.value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!this.value.equals(other.value)) {
            return false;
        }
        if (Double.doubleToLongBits(this.weight) != Double.doubleToLongBits(other.weight)) {
            return false;
        }
        return true;
    }

}
