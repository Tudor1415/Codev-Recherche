package io.gitlab.chaver.minimax.learn.train.iterative;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.learn.util.RankingsProvider;
import io.gitlab.chaver.minimax.ranking.Ranking;
import io.gitlab.chaver.minimax.score.FunctionParameters;
import io.gitlab.chaver.minimax.score.FunctionParametersFactory;
import io.gitlab.chaver.minimax.score.IScoreFunction;
import io.gitlab.chaver.minimax.score.LinearScoreFunction;
import io.gitlab.chaver.minimax.svmrank.algorithm.SvmRank;
import io.gitlab.chaver.minimax.svmrank.io.SvmQuery;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static io.gitlab.chaver.minimax.learn.train.LearnUtil.*;

public class SVMIterative extends IterativeRankingLearn {

    @Setter
    private double regularisationParameter = 3d;

    public SVMIterative(int nbIterations, RankingsProvider rankingsProvider, IScoreFunction<IAlternative> func,
                        int nbMeasures) {
        super(nbIterations, rankingsProvider, func, nbMeasures);
    }

    @Override
    public FunctionParameters learn(List<Ranking<IAlternative>> rankings) throws Exception {
        List<SvmQuery> queries = new ArrayList<>();
        int qid = 1;
        for (Ranking<IAlternative> ranking : rankings) {
            queries.add(convertRankingToSvmQuery(ranking, qid++));
        }
        File trainingDataFile = File.createTempFile("training", ".txt");
        File modelFile = File.createTempFile("model", ".txt");
        SvmRank svmRank = new SvmRank(queries, trainingDataFile, modelFile, regularisationParameter);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        long start = System.currentTimeMillis();
        Future<double[]> res = executor.submit(svmRank);
        try {
            double[] weights = timeLimit == 0 ? res.get() : res.get(timeRemaining, TimeUnit.MILLISECONDS);
            long time = System.currentTimeMillis() - start;
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
