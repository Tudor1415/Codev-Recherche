package io.gitlab.chaver.minimax.score;

import io.gitlab.chaver.minimax.io.Alternative;
import io.gitlab.chaver.minimax.io.IAlternative;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class OWAGeneralizedGiniTest {

    @Test
    void test() {
        double[][] vectors = {
                {3,2,3,2},
                {3,3,3,0},
                {1,3,2,4}
        };
        IAlternative[] alternatives = Arrays
                .stream(vectors)
                .map(Alternative::new)
                .toArray(IAlternative[]::new);
        IScoreFunction<IAlternative> func = new OWAGeneralizedGini(vectors[0].length);
        double zScore = func.computeScore(alternatives[2]);
        double yScore = func.computeScore(alternatives[1]);
        double xScore = func.computeScore(alternatives[0]);
        assertTrue(zScore > yScore);
        assertTrue(zScore > xScore);
        assertTrue(xScore > yScore);
    }

}