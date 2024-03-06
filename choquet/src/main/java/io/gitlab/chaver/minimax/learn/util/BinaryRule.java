package io.gitlab.chaver.minimax.learn.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

import io.gitlab.chaver.mining.rules.io.IRule;

@Getter
@Setter
@EqualsAndHashCode
public class BinaryRule implements IRule {
    private int[] x;
    private int[] y;
    private int[][] dataset;

    // Computed variables
    private int freqX;
    private int freqY;
    private int freqZ;

    public BinaryRule(int[] x, int[] y, int[][] dataset) {
        this.x = x;
        this.y = y;
        this.dataset = dataset;

        // Compute the frequencies
        freqX = computeFrequency(x);
        freqY = computeFrequency(y);

        int[] union = Union(x, y);
        freqZ = computeFrequency(union);
    }

    public double getSupport() {
        return (double) this.freqZ / this.dataset.length;
    }

    public double getConfidence() {
        if (this.freqX == 0) {
            return 0.0;
        }
        return (double) this.freqZ / this.freqX;
    }

    private int computeFrequency(int[] items) {
        int count = 0;
        for (int[] transaction : dataset) {
            if (containsItems(transaction, items)) {
                count++;
            }
        }
        return count;
    }

    public boolean containsItems(int[] largeArray, int[] subArray) {
        int subArrayLength = subArray.length;

        if (subArrayLength == 0) {
            return false;
        }

        int limit = largeArray.length - subArrayLength;

        for (int i = 0; i <= limit; i++) {
            if (subArray[0] == largeArray[i]) {
                boolean subArrayFound = true;

                for (int j = 1; j < subArrayLength; j++) {
                    if (subArray[j] != largeArray[i + j]) {
                        subArrayFound = false;
                        break;
                    }
                }

                if (subArrayFound) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int[] Union(int[] arr1, int[] arr2) {
        int[] combinedArray = new int[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, combinedArray, 0, arr1.length);
        System.arraycopy(arr2, 0, combinedArray, arr1.length, arr2.length);
        return combinedArray;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Arrays.toString(x)).append(" => ").append(Arrays.toString(y));
        return sb.toString();
    }
}
