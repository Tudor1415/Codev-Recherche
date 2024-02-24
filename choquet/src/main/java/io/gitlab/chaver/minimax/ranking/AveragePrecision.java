package io.gitlab.chaver.minimax.ranking;

import io.gitlab.chaver.minimax.io.IAlternative;
import lombok.AllArgsConstructor;

import java.util.stream.IntStream;

/**
 * See <a href="https://ad-teaching.informatik.uni-freiburg.de/InformationRetrievalWS1920/lecture-02.mp4">this</a>
 * Slide 22
 */
@AllArgsConstructor
public class AveragePrecision implements RankingMetric {

    public static final String TYPE = "AP";

    private int k;

    @Override
    public String getName() {
        return TYPE + "@" + k;
    }

    @Override
    public double compute(Ranking<IAlternative> refRanking, Ranking<IAlternative> predictedRanking) {
        int[] topK = IntStream
                .range(0, k)
                .map(i -> predictedRanking.getRankingPos()[refRanking.getRanking()[i]])
                .sorted()
                .toArray();
        double avgPrecision = 0d;
        for (int i = 0; i < k; i++) {
            avgPrecision = (avgPrecision * i / (i + 1)) + (1d / (topK[i] + 1));
        }
        if (avgPrecision < 0 || avgPrecision > 1) {
            throw new RuntimeException("average precision must be between 0 and 1");
        }
        return avgPrecision;
    }
}
