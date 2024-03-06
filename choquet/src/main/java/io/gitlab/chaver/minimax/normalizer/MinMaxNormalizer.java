package io.gitlab.chaver.minimax.normalizer;

import lombok.AllArgsConstructor;

/**
 * Normalize the vector in [0,1] using min and max values
 */
@AllArgsConstructor
public class MinMaxNormalizer implements INormalizer {

    public static final String TYPE = "minmax";

    private double[] minValues;
    private double[] maxValues;

    @Override
    public double[] normalize(double[] vector) {
        double[] normalized = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            if (vector[i] < minValues[i]) normalized[i] = 0;
            else if (vector[i] > maxValues[i]) normalized[i] = 1;
            else normalized[i] = (vector[i] - minValues[i]) / (maxValues[i] - minValues[i]);
        }
        return normalized;
    }
}
