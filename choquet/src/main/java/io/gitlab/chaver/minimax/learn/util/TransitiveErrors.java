package io.gitlab.chaver.minimax.learn.util;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.ranking.Ranking;
import io.gitlab.chaver.minimax.util.RandomUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static io.gitlab.chaver.minimax.learn.train.LearnUtil.*;

public class TransitiveErrors {

    private List<IAlternative> trainingAlternatives;
    private Comparator<IAlternative> oracle;
    private int nbIterations;
    private int nbTransitiveErrors;

    public TransitiveErrors(List<IAlternative> trainingAlternatives, Comparator<IAlternative> oracle, int nbIterations,
                            int nbTransitiveErrors) {
        this.trainingAlternatives = trainingAlternatives;
        this.oracle = oracle;
        this.nbIterations = nbIterations;
        this.nbTransitiveErrors = nbTransitiveErrors;
    }

    public RankingsProvider getRankingsProvider() {
        RandomUtil random = RandomUtil.getInstance();
        int[][] idx = random.kFolds(nbIterations, trainingAlternatives.size(), 3);
        List<Ranking<IAlternative>> rankings = new ArrayList<>(nbIterations);
        for (int i = 0; i < nbIterations; i++) {
            rankings.add(computeRankingWithOracle(oracle, Arrays.stream(idx[i]).mapToObj(j -> trainingAlternatives.get(j)).collect(Collectors.toList())));
        }
        for (int i = 0; i < nbTransitiveErrors; i++) {
            List<IAlternative> ranking = rankings.get(random.nextInt(nbIterations)).rankObjects();
            rankings.add(
                    new Ranking<>(new int[]{0,1}, new IAlternative[]{ranking.get(2), ranking.get(0)})
            );
        }
        random.shuffle(rankings);
        return new IterationRankingsProvider(rankings);
    }
}
