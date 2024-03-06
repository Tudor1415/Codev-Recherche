package io.gitlab.chaver.minimax.io;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;

@ToString(of = {"index", "vector"})
public class Alternative implements IAlternative {

    /** Values of the vector */
    private @Getter double[] vector;
    /** Ordered indexes of x such that x[orderedPermutation[i]] <= x[orderedPermutation[i+1]] */
    private int[] orderedPermutation;
    /** Index of the alternative */
    private @Getter @Setter int index;

    public Alternative(double[] vector) {
        this.vector = vector;
    }

    public Alternative(double[] vector, int index) {
        this.vector = vector;
        this.index = index;
    }

    @Override
    public int[] getOrderedPermutation() {
        if (orderedPermutation == null) {
            orderedPermutation = IntStream
                    .range(0, vector.length)
                    .boxed()
                    .sorted(Comparator.comparingDouble(i -> vector[i]))
                    .mapToInt(i -> i)
                    .toArray();
        }
        return orderedPermutation;
    }

    @Override
    public double getOrderedValue(int i) {
        return vector[getOrderedPermutation()[i]];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alternative that = (Alternative) o;
        return Arrays.equals(vector, that.vector);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(vector);
    }
}
