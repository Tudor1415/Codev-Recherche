package io.gitlab.chaver.minimax.score;

import io.gitlab.chaver.minimax.io.IAlternative;

/**
 * See Lesca and Perny - LP Solvable models for multiagent fair allocation problems
 */
public class OWAGini implements IScoreFunction<IAlternative> {

    private OWAScoreFunction func;

    public OWAGini(int nbCriteria) {
        double[] weights = new double[nbCriteria];
        for (int i = 0; i < nbCriteria; i++) {
            weights[i] = (2d * (nbCriteria - i - 1) + 1) / Math.pow(nbCriteria, 2);
        }
        func = new OWAScoreFunction(weights);
    }

    @Override
    public double computeScore(IAlternative a) {
        return func.computeScore(a);
    }
}
