package io.gitlab.chaver.minimax.learn.oracle;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.ranking.Ranking;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static io.gitlab.chaver.minimax.learn.train.LearnUtil.*;

class MeasureRankingOracleTest {

    @Test
    void testOracle() {
        double[][] vectors = {
                {2, 1},
                {3, 3},
                {1, 2}
        };
        List<IAlternative> alternatives = vectorsToAlternatives(vectors);
        Comparator<IAlternative> oracle = new MeasureRankingOracle(alternatives, alternatives);
        Ranking<IAlternative> ranking = computeRankingWithOracle(oracle, alternatives);
        System.out.println(ranking);
    }

}