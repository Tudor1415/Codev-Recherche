package io.gitlab.chaver.minimax.learn.oracle;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.score.LinearScoreFunction;

public class LinearFunctionOracle extends ScoreOracle {

    public static final String TYPE = "linear";

    private LinearScoreFunction func;

    public LinearFunctionOracle(double[] weights) {
        func = new LinearScoreFunction(weights);
    }

    @Override
    public double computeScore(IAlternative a) {
        return func.computeScore(a);
    }
}
