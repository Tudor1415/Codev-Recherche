package io.gitlab.chaver.minimax.ranking;

import io.gitlab.chaver.minimax.io.IAlternative;

import java.util.Arrays;
import java.util.stream.IntStream;

public class KendallConcordanceCoeff implements RankingMetric {

    public static final String TYPE = "kendall";

    @Override
    public String getName() {
        return TYPE;
    }

    @Override
    public double compute(Ranking<IAlternative> refRanking, Ranking<IAlternative> predictedRanking) {
        int[] ranksA = refRanking.getRankingPos();
        int[] ranksB = predictedRanking.getRankingPos();
        if (ranksA.length <= 1 || ranksA.length != ranksB.length) {
            throw new RuntimeException("Error while computing Kendall : the two rankings must have the same size > 1");
        }
        int[] sumRank = IntStream.range(0, ranksA.length).map(i -> ranksA[i] + ranksB[i]).toArray();
        double mean = Arrays.stream(sumRank).mapToDouble(r -> (double) r / ranksA.length).sum();
        double alpha = 0;
        for (int i = 0; i < ranksA.length; i++) {
            alpha += Math.pow(sumRank[i] - mean, 2);
        }
        double value = 3 * alpha / (Math.pow(ranksA.length, 3) - ranksA.length);
        if (value < 0 || value > 1) {
            throw new RuntimeException("Kendall must be between 0 and 1");
        }
        return value;
    }
}
