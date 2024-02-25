package io.gitlab.chaver.minimax.learn.train.iterative;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.kappalab.algorithm.KappalabSocket;
import io.gitlab.chaver.minimax.kappalab.io.KappalabInput;
import io.gitlab.chaver.minimax.kappalab.io.KappalabInput2;
import io.gitlab.chaver.minimax.kappalab.io.KappalabMethod;
import io.gitlab.chaver.minimax.kappalab.io.KappalabOutput;
import io.gitlab.chaver.minimax.learn.util.RankingsProvider;
import io.gitlab.chaver.minimax.ranking.Ranking;
import io.gitlab.chaver.minimax.score.ChoquetMobiusScoreFunction;
import io.gitlab.chaver.minimax.score.FunctionParameters;
import io.gitlab.chaver.minimax.score.FunctionParametersFactory;
import io.gitlab.chaver.minimax.score.IScoreFunction;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.*;

import static io.gitlab.chaver.minimax.learn.train.LearnUtil.*;

public class KappalabSocketIterative extends IterativeRankingLearn {

    @Setter
    private double delta = 0.00001d;
    @Setter
    private int kAdditivity = 2;
    @Setter
    private KappalabMethod approach = KappalabMethod.gls;
    @Setter
    private int sigf = 3;

    public KappalabSocketIterative(int nbIterations, RankingsProvider rankingsProvider, IScoreFunction<IAlternative> func,
                                   int nbMeasures) {
        super(nbIterations, rankingsProvider, func, nbMeasures);
        // System.out.println("Algorithm Initialized");

    }

    @Override
    public FunctionParameters learn(List<Ranking<IAlternative>> rankings) throws Exception {
        //System.out.println("Satring Learnig");
        
        KappalabInput2 input = new KappalabInput2();
        for (Ranking<IAlternative> ranking : rankings) {
            addRankingToKappalabInput(ranking, input, delta);
        }
        KappalabInput mainInput = convertToKappalabInput(input, kAdditivity, approach);
        mainInput.setSigf(sigf);
        KappalabSocket kappalab = new KappalabSocket(mainInput);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        long start = System.currentTimeMillis();
        Future<KappalabOutput> res = executor.submit(kappalab);
        try {
            KappalabOutput output = timeLimit == 0 ? res.get() : res.get(timeRemaining, TimeUnit.MILLISECONDS);
            long time = System.currentTimeMillis() - start;
            if (output.getErrorMessages() != null) {
                return errorFunction(output.getErrorMessages());
            }
            return FunctionParametersFactory.getFunctionParameters(ChoquetMobiusScoreFunction.TYPE, nbMeasures, kAdditivity,
                    output.getCapacities(), time / 1000d);
        }
        catch (TimeoutException e) {
            return timeOut();
        }
        finally {
            executor.shutdown();
        }
    }
}
