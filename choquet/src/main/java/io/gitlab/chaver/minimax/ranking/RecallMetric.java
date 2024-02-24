package io.gitlab.chaver.minimax.ranking;

import io.gitlab.chaver.minimax.io.IAlternative;

import java.util.HashSet;
import java.util.Set;

public class RecallMetric implements RankingMetric {

    public static final String TYPE = "rec";

    private int k;

    public RecallMetric(int k) {
        this.k = k;
    }

    @Override
    public String getName() {
        return TYPE + "@" + k;
    }

    @Override
    public double compute(Ranking<IAlternative> refRanking, Ranking<IAlternative> predictedRanking) {
        Set<IAlternative> topKA = new HashSet<>();
        Set<IAlternative> topKB = new HashSet<>();
        for (int i = 0; i < k; i++) {
            topKA.add(refRanking.getObjects()[refRanking.getRanking()[i]]);
            topKB.add(predictedRanking.getObjects()[predictedRanking.getRanking()[i]]);
        }
        topKA.retainAll(topKB);
        double value = (double) topKA.size() / k;
        if (value < 0 || value > 1) {
            throw new RuntimeException("R@k must be between 0 and 1");
        }
        return value;
    }
}
