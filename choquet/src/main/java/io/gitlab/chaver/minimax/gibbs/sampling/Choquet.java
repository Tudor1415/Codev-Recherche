package io.gitlab.chaver.minimax.gibbs.sampling;

import io.gitlab.chaver.minimax.gibbs.rules.BinaryRule;
import io.gitlab.chaver.minimax.score.FunctionParameters;

public class Choquet extends GibbsSampling {

    public Choquet(int[][] transactions, int nbItems) {
        super(transactions, nbItems);
    }

    protected double g_function(BinaryRule J, FunctionParameters params) {
        return 0.5;
    }
}
