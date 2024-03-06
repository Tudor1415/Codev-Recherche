package io.gitlab.chaver.minimax.score;

import io.gitlab.chaver.minimax.capacity.MobiusCapacity;
import io.gitlab.chaver.minimax.io.Alternative;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChoquetMobiusScoreFunctionTest {

    private double[][] alternatives = {
            {18, 11, 11, 11, 18},
            {18, 11, 18, 11, 11},
            {11, 11, 18, 11, 18},
            {18, 18, 11, 11, 11},
            {11, 11, 18, 18, 11},
            {11, 11, 18, 11, 11},
            {11, 11, 11, 11, 18},
            {10, 11, 12, 13, 14}
    };

    private double[] capacities = {0.000000000, 0.311649944, 0.176033391, 0.214285785, 0.142417995, 0.142857337,
            -0.130540333, -0.025935850, -0.011987137, 0.116921121, 0.010362019, 0.049201130, 0.002543699, -0.070989410,
            0.071428152, 0.001752156};

    private double[] expectedScores = {15, 14.5, 14, 13.5, 13, 12.5, 12, 11.67};

    @Test
    void testChoquet() {
        MobiusCapacity capacity = new MobiusCapacity(5, 2, capacities);
        ChoquetMobiusScoreFunction func = new ChoquetMobiusScoreFunction(capacity);
        for (int i = 0; i < alternatives.length; i++) {
            assertEquals(expectedScores[i], func.computeScore(new Alternative(alternatives[i])), 0.01);
        }
    }

}