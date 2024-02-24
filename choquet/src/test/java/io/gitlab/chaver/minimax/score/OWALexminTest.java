package io.gitlab.chaver.minimax.score;

import io.gitlab.chaver.minimax.io.Alternative;
import io.gitlab.chaver.minimax.io.IAlternative;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class OWALexminTest {

    @Test
    void test() {
        OWALexmin func = new OWALexmin(0.01, 4);
        double[][] alt = {
                {0.6, 0.7, 0.8, 1.0},
                {0.6, 0.8, 0.9, 1.0},
                {0.4, 0.7, 0.9, 1.0}
        };
        IAlternative[] alternatives = Arrays.stream(alt).map(Alternative::new).toArray(IAlternative[]::new);
        assertTrue(func.computeScore(alternatives[0]) < func.computeScore(alternatives[1]));
        assertTrue(func.computeScore(alternatives[2]) < func.computeScore(alternatives[0]));
    }

}