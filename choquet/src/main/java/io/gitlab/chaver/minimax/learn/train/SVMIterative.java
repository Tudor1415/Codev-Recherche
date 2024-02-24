package io.gitlab.chaver.minimax.learn.train;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.learn.util.AlternativesSelector;
import io.gitlab.chaver.minimax.ranking.Ranking;
import io.gitlab.chaver.minimax.score.FunctionParameters;
import io.gitlab.chaver.minimax.score.FunctionParametersFactory;
import io.gitlab.chaver.minimax.score.LinearScoreFunction;
import io.gitlab.chaver.minimax.svmrank.algorithm.SvmRank;
import io.gitlab.chaver.minimax.svmrank.io.SvmQuery;
import lombok.Setter;

import java.io.File;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import static io.gitlab.chaver.minimax.learn.train.LearnUtil.*;

public class SVMIterative extends IterativeRankingLearning {

    @Setter
    private double regularisationParameter = 3;

    private int qid = 1;
    private List<SvmQuery> queries = new LinkedList<>();

    public SVMIterative(int nbIterations, Comparator<IAlternative> oracle, AlternativesSelector alternativesSelector) {
        super(nbIterations, oracle, alternativesSelector);
    }

    @Override
    public FunctionParameters learn(Ranking<IAlternative> ranking) throws Exception {
        queries.add(convertRankingToSvmQuery(ranking, qid++));
        File trainingDataFile = File.createTempFile("training", ".txt");
        File modelFile = File.createTempFile("model", ".txt");
        SvmRank svmRank = new SvmRank(queries, trainingDataFile, modelFile, regularisationParameter);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        double start = System.currentTimeMillis();
        Future<double[]> res = executor.submit(svmRank);
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
