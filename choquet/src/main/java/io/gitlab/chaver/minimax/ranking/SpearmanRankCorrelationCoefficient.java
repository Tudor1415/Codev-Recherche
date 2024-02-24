package io.gitlab.chaver.minimax.ranking;

import io.gitlab.chaver.minimax.io.IAlternative;

import java.util.stream.IntStream;

public class SpearmanRankCorrelationCoefficient implements RankingMetric {

    public static final String TYPE = "spearman";

    @Override
    public String getName() {
        return TYPE;
    }

    @Override
    public double compute(Ranking<IAlternative> refRanking, Ranking<IAlternative> predictedRanking) {
        int[] ranksA = refRanking.getRankingPos();
        int[] ranksB = predictedRanking.getRankingPos();
        if (ranksA.length <= 1 || ranksA.length != ranksB.length) {
            throw new RuntimeException("Error while computing Spearman : the two rankings must have the same size > 1");
        }
        double sum = IntStream
                .range(0, ranksA.length)
                .map(i -> ranksA[i] - ranksB[i])
                .mapToDouble(i -> Math.pow(i, 2))
                .sum();
        int n = ranksA.length;
        double value = 1 - 6 * sum / (n * (Math.pow(n, 2) - 1));
        if (value < -1 || value > 1) {
            throw new RuntimeException("spearman must be between -1 and 1");
        }
        return value;
    }
}
