package io.gitlab.chaver.minimax.learn.oracle;

import io.gitlab.chaver.minimax.capacity.NormalizedCapacity;
import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.score.ChoquetScoreFunction;

public class ChoquetOracle extends ScoreOracle {

    public static final String TYPE = "choquet";

    private ChoquetScoreFunction func;

    public ChoquetOracle(double[] weights, int nbCriteria) {
        NormalizedCapacity capacity = new NormalizedCapacity(nbCriteria);
        for (int i = 0; i < weights.length; i++) {
            capacity.addCapacitySet(i, weights[i]);
        }
        func = new ChoquetScoreFunction(capacity);
    }

    public ChoquetOracle(NormalizedCapacity capacity) {
        func = new ChoquetScoreFunction(capacity);
    }

    @Override
    public double computeScore(IAlternative a) {
        return func.computeScore(a);
    }
}
