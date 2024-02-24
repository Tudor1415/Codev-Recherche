package io.gitlab.chaver.minimax.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RandomUtilTest {

    private RandomUtil random = RandomUtil.getInstance();

    @Test
    void testKFolds() {
        int size = 10;
        int[][] folds = random.kFolds(5, size);
        Set<Integer> idx = new HashSet<>();
        for (int[] fold : folds) {
            for (int i : fold) {
                idx.add(i);
            }
        }
        assertEquals(size, idx.size());
    }

    @Test
    void testKFolds2() {
        int size = 10;
        int[][] folds = random.kFolds(3, size);
        Set<Integer> idx = new HashSet<>();
        for (int[] fold : folds) {
            for (int i : fold) {
                idx.add(i);
            }
        }
        assertEquals(9, idx.size());
    }

    @Test
    void testGenerateRandomWeights() {
        assertEquals(1.0, Arrays.stream(random.generateRandomWeights(5)).sum(), 0.001);
    }

}