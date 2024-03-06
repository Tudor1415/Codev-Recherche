package io.gitlab.chaver.minimax.ahp.util;


import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.ranking.Ranking;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;

public class AHPUtil {

    /**
     * Compute ranks of the alternatives in desc order
     * @param alternatives alternatives to consider
     * @param measureIdx index of the measure
     * @return a ranking w.r.t. the measure at index measureIdx
     */
    public static Ranking<IAlternative> getRanking(IAlternative[] alternatives, int measureIdx) {
        double[] alternativeMeasures = Arrays.stream(alternatives).mapToDouble(p -> p.getVector()[measureIdx]).toArray();
        return getRanking(alternativeMeasures, alternatives);
    }

    /**
     * Compute ranks of the alternatives in desc order
     * @param alternativeMeasures alternatives to consider
     * @return a ranking w.r.t. the measure at index measureIdx
     */
    public static Ranking<IAlternative> getRanking(double[] alternativeMeasures, IAlternative[] alternatives) {
        int[] orderedIdxDesc = IntStream
                .range(0, alternativeMeasures.length)
                .boxed()
                .sorted(Comparator.comparingDouble(i -> -alternativeMeasures[i]))
                .mapToInt(i -> i)
                .toArray();
        return new Ranking<>(orderedIdxDesc, alternatives);
    }
}
