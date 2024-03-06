package io.gitlab.chaver.minimax.ahp.algorithm;

import org.apache.commons.math3.util.Precision;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AHPTest {

    private void ahpTest(double[][] A, double[] expectedWeights, int scale) {
        int nbMeasures = A.length;
        AHP ahp = new AHP(nbMeasures);
        ahp.computeWeights(A);
        //System.out.println(Arrays.toString(ahp.getWeights()));
        for (int i = 0; i < expectedWeights.length; i++) {
            double expectedWeight = Precision.round(expectedWeights[i], scale);
            double weight = Precision.round(ahp.getWeights()[i], scale);
            assertEquals(expectedWeight, weight);
        }
    }

    @Test
    void testAHP() {
        double[][] A = {
                {1, 3, 6},
                {1d/3, 1, 2},
                {1d/6, 1d/2, 1}
        };
        double[] expectedWeights = {6d/9, 2d/9, 1d/9};
        ahpTest(A, expectedWeights, 3);
    }

    @Test
    void testAHP2() {
        double[][] A = {
                {1, 2, 8},
                {1d/2, 1, 1d/4},
                {1d/8, 4, 1}
        };
        double[] expectedWeights = {0.661, 0.131, 0.208};
        ahpTest(A, expectedWeights, 3);
    }

    @Test
    void testAHP3() {
        double[][] A = {
                {1, 1d/5, 1d/9, 1},
                {5, 1, 1, 5},
                {9, 1, 1, 5},
                {1, 1d/5, 1d/5, 1}
        };
        double[] expectedWeights = {0.0684, 0.3927, 0.4604, 0.0785};
        ahpTest(A, expectedWeights, 4);
    }
}
