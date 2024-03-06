package io.gitlab.chaver.minimax.normalizer;

import io.gitlab.chaver.minimax.io.Alternative;
import io.gitlab.chaver.minimax.io.IAlternative;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class INormalizerTest {

    @Test
    void tchebychefNormalize() {
        double[][] vectors = {
                {7, 13},
                {10, 10},
                {8, 7},
                {11, 6},
                {13, 8},
                {15, 4},
                {14, 1},
                {12, 9}
        };
        List<IAlternative> alternatives = Arrays
                .stream(vectors)
                .map(Alternative::new)
                .collect(Collectors.toCollection(ArrayList::new));
        List<IAlternative> normalized = INormalizer.tchebychefNormalize(alternatives);
        for (IAlternative a : normalized) {
            for (double val : a.getVector()) {
                assertTrue(val >= 0 && val <= 1);
            }
        }
    }
}