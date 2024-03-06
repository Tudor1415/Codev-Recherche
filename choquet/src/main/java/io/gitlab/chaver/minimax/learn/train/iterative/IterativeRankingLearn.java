package io.gitlab.chaver.minimax.learn.train.iterative;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.learn.train.AbstractRankingLearning;
import io.gitlab.chaver.minimax.learn.train.LearnStep;
import io.gitlab.chaver.minimax.learn.util.RankingsProvider;
import io.gitlab.chaver.minimax.ranking.Ranking;
import io.gitlab.chaver.minimax.score.FunctionParameters;
import io.gitlab.chaver.minimax.score.IScoreFunction;
import io.gitlab.chaver.minimax.score.ScoreFunctionFactory;

import java.util.List;
import java.util.Set;

public abstract class IterativeRankingLearn extends AbstractRankingLearning implements LearnStep {

    private int nbIterations;
    private RankingsProvider rankingsProvider;

    private IScoreFunction<IAlternative> func;
    // Time remaining in ms
    protected long timeRemaining;


    public IterativeRankingLearn(int nbIterations, RankingsProvider rankingsProvider, IScoreFunction<IAlternative> func,
                                 int nbMeasures) {
        this.nbIterations = nbIterations;
        this.rankingsProvider = rankingsProvider;
        this.func = func;
        this.nbMeasures = nbMeasures;
    }

    @Override
    public FunctionParameters learn() throws Exception {
        FunctionParameters params = new FunctionParameters();
        params.setNbIterations(nbIterations);
        for (int i = 0; i < nbIterations; i++) {
            double timeToLearn = params.getTimeToLearn();
            params = learn(rankingsProvider.provideRankings(this));
            params.setTimeToLearn(timeToLearn + params.getTimeToLearn());
            if (params.getErrorMessages() != null) {
                params.setNbIterations(i+1);
                return params;
            }
            func = ScoreFunctionFactory.getScoreFunction(params);
            support.firePropertyChange("func", null, func);
        }
        return params;
    }

    public abstract FunctionParameters learn(List<Ranking<IAlternative>> rankings) throws Exception;

    @Override
    public IScoreFunction<IAlternative> getCurrentScoreFunction() {
        return func;
    }

    @Override
    public Set<List<Integer>> getSelectedPairs() {
        return null;
    }

    @Override
    public List<IAlternative> getCurrentAlternatives() {
        return null;
    }

    @Override
    public void setTimeLimit(int timeLimit) {
        super.setTimeLimit(timeLimit);
        timeRemaining = timeLimit * 1000L;

    }
}
