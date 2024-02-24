package io.gitlab.chaver.minimax.learn.train.passive;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.kappalab.algorithm.Kappalab;
import io.gitlab.chaver.minimax.kappalab.io.KappalabInput;
import io.gitlab.chaver.minimax.kappalab.io.KappalabMethod;
import io.gitlab.chaver.minimax.kappalab.io.KappalabOutput;
import io.gitlab.chaver.minimax.ranking.Ranking;
import io.gitlab.chaver.minimax.score.ChoquetMobiusScoreFunction;
import io.gitlab.chaver.minimax.score.FunctionParameters;
import io.gitlab.chaver.minimax.score.FunctionParametersFactory;
import lombok.Setter;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class KappalabRankLearn extends PassiveRankingLearning {

    @Setter
    private double delta = 0.00001d;
    @Setter
    private int kAdditivity = 2;
    @Setter
    private KappalabMethod approach = KappalabMethod.gls;
    @Setter
    private int sigf = 3;

    public KappalabRankLearn(Ranking<IAlternative> expectedRanking) {
        super(expectedRanking);
    }

    @Override
    public FunctionParameters learn() throws Exception {
        List<double[]> kapaAlternatives = Arrays
                .stream(expectedRanking.getObjects())
                .map(a -> a.getVector())
                .collect(Collectors.toList());
        List<Number[]> preferences = new LinkedList<>();
        int[] ranks = expectedRanking.getRanking();
        for (int i = 0; i < kapaAlternatives.size() - 1; i++) {
            preferences.add(new Number[]{(ranks[i]+1), (ranks[i+1]+1), delta});
        }
        KappalabInput input = new KappalabInput(kapaAlternatives, preferences, kAdditivity, approach);
        input.setSigf(sigf);
        File inputFile = File.createTempFile("kappalab_input", ".json");
        File outputFile = File.createTempFile("kappalab_output", ".json");
        Kappalab kappalab = new Kappalab(inputFile, outputFile, input);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        double start = System.currentTimeMillis();
        Future<KappalabOutput> res = executor.submit(kappalab);
        try {
            KappalabOutput output = timeLimit == 0 ? res.get() : res.get(timeLimit, TimeUnit.SECONDS);
            double time = (System.currentTimeMillis() - start) / 1000;
            FunctionParameters params = FunctionParametersFactory.getFunctionParameters(ChoquetMobiusScoreFunction.TYPE, nbMeasures, kAdditivity,
                    output.getCapacities(), time);
            params.setShapleyValues(output.getShapleyValues());
            params.setInteractionIndices(output.getInteractionIndices());
            if (output.getObj() != null) {
                params.setObj(output.getObj());
            }
            return params;
        }
        catch (TimeoutException e) {
            return timeOut();
        }
        finally {
            executor.shutdown();
        }
    }
}
