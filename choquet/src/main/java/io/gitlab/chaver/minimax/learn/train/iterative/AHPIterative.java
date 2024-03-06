package io.gitlab.chaver.minimax.learn.train.iterative;

import io.gitlab.chaver.minimax.ahp.algorithm.AHPRank;
import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.learn.util.RankingsProvider;
import io.gitlab.chaver.minimax.ranking.Ranking;
import io.gitlab.chaver.minimax.score.FunctionParameters;
import io.gitlab.chaver.minimax.score.FunctionParametersFactory;
import io.gitlab.chaver.minimax.score.IScoreFunction;
import io.gitlab.chaver.minimax.score.LinearScoreFunction;

import java.util.List;
import java.util.concurrent.*;

public class AHPIterative extends IterativeRankingLearn {

    public AHPIterative(int nbIterations, RankingsProvider rankingsProvider, IScoreFunction<IAlternative> func, int nbMeasures) {
        super(nbIterations, rankingsProvider, func, nbMeasures);
    }

    @Override
    public FunctionParameters learn(List<Ranking<IAlternative>> rankings) throws Exception {
        AHPRank ahpRank = new AHPRank(rankings);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        long start = System.currentTimeMillis();
        Future<double[]> res = executor.submit(ahpRank);
        try {
            double[] weights = timeLimit == 0 ? res.get() : res.get(timeRemaining, TimeUnit.MILLISECONDS);
            long time = System.currentTimeMillis() - start;
            timeRemaining -= time;
            return FunctionParametersFactory.getFunctionParameters(LinearScoreFunction.TYPE, nbMeasures, 0,
                    weights, time / 1000d);
        }
        catch (TimeoutException e) {
            return timeOut();
        }
        finally {
            executor.shutdown();
        }
    }
}
