package io.gitlab.chaver.minimax.learn.train;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.kappalab.algorithm.Kappalab;
import io.gitlab.chaver.minimax.kappalab.io.KappalabInput;
import io.gitlab.chaver.minimax.kappalab.io.KappalabInput2;
import io.gitlab.chaver.minimax.kappalab.io.KappalabMethod;
import io.gitlab.chaver.minimax.kappalab.io.KappalabOutput;
import io.gitlab.chaver.minimax.learn.util.AlternativesSelector;
import io.gitlab.chaver.minimax.ranking.Ranking;
import io.gitlab.chaver.minimax.score.ChoquetMobiusScoreFunction;
import io.gitlab.chaver.minimax.score.FunctionParameters;
import io.gitlab.chaver.minimax.score.FunctionParametersFactory;
import lombok.Setter;

import java.io.File;
import java.util.Comparator;
import java.util.concurrent.*;

import static io.gitlab.chaver.minimax.learn.train.LearnUtil.*;

public class KappalabIterative extends IterativeRankingLearning {

    @Setter
    private double delta = 0.00001;
    @Setter
    private int kAdditivity = 2;
    @Setter
    private KappalabMethod approach = KappalabMethod.gls;

    private KappalabInput2 input = new KappalabInput2();

    public KappalabIterative(int nbIterations, Comparator<IAlternative> oracle, AlternativesSelector alternativesSelector) {
        super(nbIterations, oracle, alternativesSelector);
    }

    @Override
    public FunctionParameters learn(Ranking<IAlternative> ranking) throws Exception {
        addRankingToKappalabInput(ranking, input, delta);
        KappalabInput mainInput = convertToKappalabInput(input, kAdditivity, approach);
        File inputFile = File.createTempFile("kappalab_input", ".json");
        File outputFile = File.createTempFile("kappalab_output", ".json");
        Kappalab kappalab = new Kappalab(inputFile, outputFile, mainInput);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        double start = System.currentTimeMillis();
        Future<KappalabOutput> res = executor.submit(kappalab);
        try {
            KappalabOutput output = timeLimit == 0 ? res.get() : res.get(timeLimit, TimeUnit.SECONDS);
            double time = (System.currentTimeMillis() - start) / 1000;
            return FunctionParametersFactory.getFunctionParameters(ChoquetMobiusScoreFunction.TYPE, nbMeasures, kAdditivity,
                    output.getCapacities(), time);
        }
        catch (TimeoutException e) {
            return timeOut();
        }
        finally {
            executor.shutdown();
        }
    }
}
