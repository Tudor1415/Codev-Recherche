package io.gitlab.chaver.minimax.learn.train;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.learn.util.AlternativesSelector;
import io.gitlab.chaver.minimax.ranking.Ranking;
import io.gitlab.chaver.minimax.score.FunctionParameters;
import io.gitlab.chaver.minimax.score.IScoreFunction;
import io.gitlab.chaver.minimax.score.ScoreFunctionFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static io.gitlab.chaver.minimax.learn.train.LearnUtil.*;

public abstract class IterativeRankingLearning extends AbstractRankingLearning implements LearnStep {

    private int nbIterations;
    private Comparator<IAlternative> oracle;
    private AlternativesSelector alternativesSelector;
    private IScoreFunction<IAlternative> func;

    public IterativeRankingLearning(int nbIterations, Comparator<IAlternative> oracle, AlternativesSelector alternativesSelector) {
        this.nbIterations = nbIterations;
        this.oracle = oracle;
        this.alternativesSelector = alternativesSelector;
    }

    @Override
    public FunctionParameters learn() throws Exception {
        FunctionParameters params = null;
        for (int i = 0; i < nbIterations; i++) {
            params = learn(computeRankingWithOracle(oracle, alternativesSelector.selectAlternatives(this)));
            func = ScoreFunctionFactory.getScoreFunction(params);
        }
        return params;
    }

    public abstract FunctionParameters learn(Ranking<IAlternative> ranking) throws Exception;

    @Override
    public List<IAlternative> getCurrentAlternatives() {
        return null;
    }

    @Override
    public Set<List<Integer>> getSelectedPairs() {
        return null;
    }

    @Override
    public IScoreFunction<IAlternative> getCurrentScoreFunction() {
        return func;
    }
}
