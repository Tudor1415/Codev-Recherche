package io.gitlab.chaver.minimax.learn.train.passive;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.ranking.Ranking;
import io.gitlab.chaver.minimax.score.FunctionParameters;
import io.gitlab.chaver.minimax.score.FunctionParametersFactory;
import io.gitlab.chaver.minimax.score.LinearScoreFunction;
import io.gitlab.chaver.minimax.svmrank.algorithm.SvmRank;
import io.gitlab.chaver.minimax.svmrank.io.SvmQuery;
import io.gitlab.chaver.minimax.svmrank.io.SvmQueryInput;
import lombok.Setter;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class SVMRankLearn extends PassiveRankingLearning {

    @Setter
    private double regularisationParameter = 0.01;

    public SVMRankLearn(Ranking<IAlternative> expectedRanking) {
        super(expectedRanking);
    }

    private List<SvmQuery> getQueries() {
        List<SvmQuery> queries = new LinkedList<>();
        List<SvmQueryInput> inputs = new LinkedList<>();
        IAlternative[] objects = expectedRanking.getObjects();
        for (int i = 0; i < objects.length; i++) {
            inputs.add(new SvmQueryInput(objects[expectedRanking.getRanking()[i]], objects.length - i));
        }
        SvmQuery query = new SvmQuery(1, inputs);
        queries.add(query);
        return queries;
    }

    @Override
    public FunctionParameters learn() throws Exception {
        File trainingDataFile = File.createTempFile("training", ".txt");
        File modelFile = File.createTempFile("model", ".txt");
        SvmRank svmRank = new SvmRank(getQueries(), trainingDataFile, modelFile, regularisationParameter);
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
