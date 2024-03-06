package io.gitlab.chaver.minimax.kappalab.algorithm;

import io.gitlab.chaver.minimax.kappalab.io.KappalabInput;
import io.gitlab.chaver.minimax.kappalab.io.KappalabMethod;
import io.gitlab.chaver.minimax.kappalab.io.KappalabOutput;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KappalabTest {

    @Test
    void testKappalab() throws Exception {
        File inputFile = File.createTempFile("kappalab_input", ".json");
        File outputFile = File.createTempFile("kappalab_output", ".json");
        List<double[]> alternatives = new ArrayList<>();
        alternatives.add(new double[]{1,0.5,0.8,0.7});
        alternatives.add(new double[]{0.8,0.8,0.7,0.7});
        List<Number[]> preferences = new ArrayList<>();
        preferences.add(new Number[]{2,1,0.1});
        int k = 2;
        KappalabInput input = new KappalabInput(alternatives, preferences, k, KappalabMethod.mv);
        Kappalab kappalab = new Kappalab(inputFile, outputFile, input);
        KappalabOutput output = kappalab.call();
        assertArrayEquals(new double[]{0.0, 0.0798, 0.2571, 0.1862, 0.2216, 0.1667, -0.0106, 0.0426, 0.0426, -0.0106, 0.0248}, output.getCapacities());
    }
}