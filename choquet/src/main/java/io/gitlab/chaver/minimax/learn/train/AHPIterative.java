package io.gitlab.chaver.minimax.learn.train;

import io.gitlab.chaver.minimax.ahp.algorithm.AHPRank;
import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.learn.util.AlternativesSelector;
import io.gitlab.chaver.minimax.ranking.Ranking;
import io.gitlab.chaver.minimax.score.FunctionParameters;
import io.gitlab.chaver.minimax.score.FunctionParametersFactory;
import io.gitlab.chaver.minimax.score.LinearScoreFunction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

public class AHPIterative extends IterativeRankingLearning {

    private List<Ranking<IAlternative>> rankings = new ArrayList<>();

    public AHPIterative(int nbIterations, Comparator<IAlternative> oracle, AlternativesSelector alternativesSelector) {
        super(nbIterations, oracle, alternativesSelector);
    }

    @Override
    public FunctionParameters learn(Ranking<IAlternative> ranking) throws Exception {
        rankings.add(ranking);
        AHPRank ahpRank = new AHPRank(rankings);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        double start = System.currentTimeMillis();
        Future<double[]> res = executor.submit(ahpRank);
        try {
            double[] weights = timeLimit == 0 ? res.get() : res.get(timeLimit, TimeUnit.SECONDS);
            double time = (System.currentTimeMillis() - start) / 1000;
            return FunctionParametersFactory.getFunctionParameters(LinearScoreFunction.TYPE, nbMeasures, 0,
                    weights, time);
        }
        catch (TimeoutException e) {
            return timeOut();
        }
        finally {
            executor.shutdown();
        }
    }
}
