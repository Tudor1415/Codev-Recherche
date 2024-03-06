package io.gitlab.chaver.minimax.score;

import io.gitlab.chaver.minimax.io.IAlternative;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LinearScoreFunction implements IScoreFunction<IAlternative> {

    public static String TYPE = "linear";

    private double[] weights;

    @Override
    public double computeScore(IAlternative alternative) {
        assert alternative.getVector().length == weights.length;
        double score = 0;
        for (int i = 0; i < weights.length; i++) {
            score += weights[i] * alternative.getVector()[i];
        }
        return score;
    }
}
