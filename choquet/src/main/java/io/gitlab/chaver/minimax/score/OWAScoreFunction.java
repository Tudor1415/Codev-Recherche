package io.gitlab.chaver.minimax.score;

import io.gitlab.chaver.minimax.io.IAlternative;
import lombok.AllArgsConstructor;

/**
 * Ordered Weighted Average function (OWA)
 * Note that the vector is sorted in increasing order (and not decreasing !)
 */
@AllArgsConstructor
public class OWAScoreFunction implements IScoreFunction<IAlternative> {

    public static final String TYPE = "owa";

    private double[] weights;

    @Override
    public double computeScore(IAlternative alternative) {
        assert alternative.getVector().length == weights.length;
        double score = 0;
        for (int i = 0; i < weights.length; i++) {
            score += weights[i] * alternative.getOrderedValue(i);
        }
        return score;
    }
}
