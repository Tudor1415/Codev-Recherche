package io.gitlab.chaver.minimax.ahp.algorithm;

import io.gitlab.chaver.minimax.ahp.util.AHPUtil;
import io.gitlab.chaver.minimax.learn.oracle.ScoreFunctionOracle;
import io.gitlab.chaver.minimax.learn.train.LearnUtil;
import io.gitlab.chaver.minimax.ranking.KendallConcordanceCoeff;
import io.gitlab.chaver.minimax.ranking.Ranking;
import io.gitlab.chaver.minimax.io.Alternative;
import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.score.LinearScoreFunction;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AHPRankTest {

    /*private int[] userRanking = {3, 7, 1, 8, 6, 2, 0, 4, 9, 5};

    private double[][] measures = {
            {0.47, 0.47, 0.76, 0.56, 0.59},
            {0.48, 0.66, 0.65, 0.10, 0.05},
            {0.75, 0.72, 0.78, 0.70, 0.61},
            {0.50, 0.68, 0.77, 0.50, 0.35},
            {0.62, 0.62, 0.66, 0.57, 0.27},
            {0.80, 0.49, 0.50, 0.65, 0.60},
            {0.95, 0.48, 0.79, 0.30, 0.80},
            {0.56, 0.65, 0.63, 0.69, 0.40},
            {0.02, 0.10, 0.05, 0.80, 0.25},
            {0.57, 0.50, 0.80, 0.40, 0.02}
    };

    private double[] expectedRes = {0.72, 0.68, 0.61, 0.54, 0.53, 0.34, 0.48, 0.33, 0.50, 0.18};*/

    private int[] trainingUserRanking = {2, 0, 4, 1, 3};

    private double[][] trainingPatternMeasures = {
            {0.47, 0.47, 0.76, 0.56, 0.59},
            {0.48, 0.66, 0.65, 0.10, 0.05},
            {0.75, 0.72, 0.78, 0.70, 0.61},
            {0.50, 0.68, 0.77, 0.50, 0.35},
            {0.62, 0.62, 0.66, 0.57, 0.27}
    };

    private double[][] allPatternMeasures = {
            {0.47, 0.47, 0.76, 0.56, 0.59},
            {0.48, 0.66, 0.65, 0.10, 0.05},
            {0.75, 0.72, 0.78, 0.70, 0.61},
            {0.50, 0.68, 0.77, 0.50, 0.35},
            {0.62, 0.62, 0.66, 0.57, 0.27},
            {0.80, 0.49, 0.50, 0.65, 0.60},
            {0.95, 0.48, 0.79, 0.30, 0.80},
            {0.56, 0.65, 0.63, 0.69, 0.40},
            {0.02, 0.10, 0.05, 0.80, 0.25},
            {0.57, 0.50, 0.80, 0.40, 0.02}
    };

    private int[] userRanking = {6, 2, 5, 0, 7, 9, 4, 1, 3, 8};

    private IAlternative[] getPatterns(double[][] measures) {
        IAlternative[] patterns = new IAlternative[measures.length];
        for (int i = 0; i < patterns.length; i++) {
            patterns[i] = new Alternative(measures[i]);
        }
        return patterns;
    }

    @Test
    void testAHPRank() throws Exception {
        IAlternative[] trainingPatterns = getPatterns(trainingPatternMeasures);
        List<Ranking<IAlternative>> userRankings = Arrays.asList(new Ranking<>(trainingUserRanking, trainingPatterns));
        AHPRank algorithm = new AHPRank(userRankings);
        double[] weights = algorithm.call();
        //System.out.println(Arrays.toString(weights));
        IAlternative[] allPatterns = getPatterns(allPatternMeasures);
        Comparator<IAlternative> predictOracle = new ScoreFunctionOracle(new LinearScoreFunction(weights));
        Ranking<IAlternative> predicted = LearnUtil.computeRankingWithOracle(predictOracle, allPatterns);
        assertEquals(0.93, new KendallConcordanceCoeff().compute(new Ranking<>(userRanking, allPatterns),
                predicted), 0.01);
    }
}
