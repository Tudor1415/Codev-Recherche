package io.gitlab.chaver.minimax.learn.oracle;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.score.OWAScoreFunction;

public class OWAOracle extends ScoreOracle {

    public static final String TYPE = "owa";

    private OWAScoreFunction func;

    public OWAOracle(double[] weights) {
        func = new OWAScoreFunction(weights);
    }

    @Override
    public double computeScore(IAlternative a) {
        return func.computeScore(a);
    }
}
