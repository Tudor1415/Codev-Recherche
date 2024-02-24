package io.gitlab.chaver.minimax.learn.train.iterative;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.learn.util.RankingsProvider;
import io.gitlab.chaver.minimax.ranking.Ranking;
import io.gitlab.chaver.minimax.score.FunctionParameters;
import io.gitlab.chaver.minimax.score.IScoreFunction;
import io.gitlab.chaver.minimax.score.LinearScoreFunction;

import java.util.List;

public class BaselineIterative extends IterativeRankingLearn {

    private double[] weights;

    public BaselineIterative(int nbIterations, RankingsProvider rankingsProvider, IScoreFunction<IAlternative> func, int nbMeasures) {
        super(nbIterations, rankingsProvider, func, nbMeasures);
        weights = new double[nbMeasures];
        for (int i = 0; i < nbMeasures; i++) {
            weights[i] = 1d;
        }
    }

    @Override
    public FunctionParameters learn(List<Ranking<IAlternative>> rankings) throws Exception {
        FunctionParameters params = new FunctionParameters();
        params.setFunctionType(LinearScoreFunction.TYPE);
        params.setWeights(weights);
        params.setNbCriteria(nbMeasures);
        return params;
    }
}
