package io.gitlab.chaver.minimax.normalizer;

import io.gitlab.chaver.minimax.io.Alternative;
import io.gitlab.chaver.minimax.io.IAlternative;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Normalize the vector of an alternative
 */
public interface INormalizer {

    double[] normalize(double[] vector);

    static List<IAlternative> normalize(List<IAlternative> alternatives, INormalizer normalizer) {
        return alternatives
                .stream()
                .map(a -> new Alternative(normalizer.normalize(a.getVector())))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    static double[] nadir(List<IAlternative> alternatives) {
        int n = alternatives.get(0).getVector().length;
        double[] nadir = new double[n];
        for (int i = 0; i < n; i++) {
            nadir[i] = Double.MAX_VALUE;
        }
        for (IAlternative a : alternatives) {
            for (int i = 0; i < n; i++) {
                if (nadir[i] > a.getVector()[i]) {
                    nadir[i] = a.getVector()[i];
                }
            }
        }
        return nadir;
    }

    static double[] ideal(List<IAlternative> alternatives) {
        int n = alternatives.get(0).getVector().length;
        double[] ideal = new double[n];
        for (int i = 0; i < n; i++) {
            ideal[i] = -Double.MAX_VALUE;
        }
        for (IAlternative a : alternatives) {
            for (int i = 0; i < n; i++) {
                if (ideal[i] < a.getVector()[i]) {
                    ideal[i] = a.getVector()[i];
                }
            }
        }
        return ideal;
    }

    static List<IAlternative> tchebychefNormalize(List<IAlternative> alternatives) {
        double[] nadir = nadir(alternatives);
        double[] ideal = ideal(alternatives);
        return normalize(alternatives, new MinMaxNormalizer(nadir, ideal));
    }
}
