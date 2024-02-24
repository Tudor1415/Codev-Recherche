package io.gitlab.chaver.minimax.score;

import io.gitlab.chaver.minimax.io.IAlternative;

import static java.lang.Math.*;

/**
 * See Golden and Perny - Infine order Lorenz dominance for fair multiagent optimization
 */
public class OWAGeneralizedGini implements IScoreFunction<IAlternative> {

    private OWAScoreFunction func;

    public OWAGeneralizedGini(int nbCriteria) {
        double[] weights = new double[nbCriteria];
        for (int i = 0; i < nbCriteria; i++) {
            weights[nbCriteria - i - 1] = sin((nbCriteria - i) * PI / (2 * nbCriteria + 1));
        }
        func = new OWAScoreFunction(weights);
    }

    @Override
    public double computeScore(IAlternative a) {
        return func.computeScore(a);
    }
}
