package io.gitlab.chaver.minimax.ranking;

import io.gitlab.chaver.minimax.io.IAlternative;

public interface RankingMetric {

    String getName();


    /**
     * Compute the metric between two rankings
     * @param refRanking the reference ranking
     * @param predictedRanking the predicted ranking
     * @return the value of the corresponding ranking metric
     */
    double compute(Ranking<IAlternative> refRanking, Ranking<IAlternative> predictedRanking);
}
