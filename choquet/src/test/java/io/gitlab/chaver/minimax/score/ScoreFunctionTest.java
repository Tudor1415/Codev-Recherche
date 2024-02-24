package io.gitlab.chaver.minimax.score;

import io.gitlab.chaver.minimax.capacity.NormalizedCapacity;
import io.gitlab.chaver.minimax.io.Alternative;
import org.junit.jupiter.api.Test;

import static io.gitlab.chaver.minimax.util.ChoquetUtil.convertToCapacity;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScoreFunctionTest {

    @Test
    void testChoquet() {
        double[] capacities = {0, 0.1, 0.2, 0.5, 0.3, 0.6, 0.7, 1};
        NormalizedCapacity capacity = convertToCapacity(capacities, 3);
        ChoquetScoreFunction func = new ChoquetScoreFunction(capacity);
        assertEquals(0.75, func.computeScore(new Alternative(new double[]{0.7, 0.6, 1})), 0.001);
        assertEquals(0.74, func.computeScore(new Alternative(new double[]{0.8, 1, 0.6})), 0.001);
    }
}
