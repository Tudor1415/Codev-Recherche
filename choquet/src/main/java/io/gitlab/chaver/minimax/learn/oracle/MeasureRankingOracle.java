package io.gitlab.chaver.minimax.learn.oracle;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.ranking.Ranking;

import java.util.*;

import static io.gitlab.chaver.minimax.learn.train.LearnUtil.computeRankingWithOracle;

/**
 * Compute the ranking of each alternative according to each given measure and then average the rankings to give a score
 */
public class MeasureRankingOracle implements Comparator<IAlternative> {

    private Map<IAlternative, Double> trainingAltMap;
    private Map<IAlternative, Double> testAltMap;
    private int nbMeasures;

    private Map<IAlternative, Double> computeScoreMap(List<IAlternative> alternatives) {
        Map<IAlternative, Double> scoreMap = new HashMap<>();
        List<Ranking<IAlternative>> measureRankings = new ArrayList<>();
        for (int i = 0; i < nbMeasures; i++) {
            int finalI = i;
            Comparator<IAlternative> measureOracle = new ScoreOracle() {
                @Override
                public double computeScore(IAlternative a) {
                    return a.getVector()[finalI];
                }
            };
            measureRankings.add(computeRankingWithOracle(measureOracle, alternatives));
        }
        for (int i = 0; i < alternatives.size(); i++) {
            int finalI = i;
            double score = measureRankings
                    .stream()
                    .mapToDouble(r -> r.getRankingPos()[finalI])
                    .average()
                    .getAsDouble();
            scoreMap.put(alternatives.get(i), score);
        }
        return scoreMap;
    }

    public MeasureRankingOracle(List<IAlternative> trainingAlt, List<IAlternative> testAlt) {
        nbMeasures = trainingAlt.get(0).getVector().length;
        trainingAltMap = computeScoreMap(trainingAlt);
        testAltMap = computeScoreMap(testAlt);
    }

    @Override
    public int compare(IAlternative a1, IAlternative a2) {
        if (trainingAltMap.containsKey(a1)) {
            return Double.compare(trainingAltMap.get(a1), trainingAltMap.get(a2));
        }
        return Double.compare(testAltMap.get(a1), testAltMap.get(a2));
    }
}
