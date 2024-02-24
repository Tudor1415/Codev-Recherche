package io.gitlab.chaver.minimax.score;

import io.gitlab.chaver.minimax.io.IAlternative;

import static java.lang.Math.*;

/**
 * Implementation of Lexmin using OWA operator
 * See Yager - On the analytical representation of the Leximin ordering and its application to flexible constraint propagation
 */
public class OWALexmin implements IScoreFunction<IAlternative> {

    private OWAScoreFunction func;

    public OWALexmin(double delta, int nbCriteria) {
        double[] weights = new double[nbCriteria];
        for (int i = 0; i < nbCriteria - 1; i++) {
            weights[i] = pow(delta, i) / pow(1 + delta, i+1);
        }
        int i = nbCriteria - 1;
        weights[i] = pow(delta, i) / pow(1 + delta, i);
        func = new OWAScoreFunction(weights);
    }

    @Override
    public double computeScore(IAlternative a) {
        return func.computeScore(a);
    }
}
