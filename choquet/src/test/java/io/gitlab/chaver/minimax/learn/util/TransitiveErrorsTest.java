package io.gitlab.chaver.minimax.learn.util;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.learn.oracle.LinearFunctionOracle;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static io.gitlab.chaver.minimax.learn.train.LearnUtil.*;

class TransitiveErrorsTest {

    @Test
    void test() {
        double[][] vectors = {
                {0.5, 0.6, 0.3},
                {0.8, 0.4, 0.3},
                {0.4, 0.2, 0.9},
                {1.0, 0.5, 0.1},
                {0.2, 0.8, 0.3},
                {0.0, 0.5, 0.7}
        };
        List<IAlternative> alternatives = vectorsToAlternatives(vectors);
        Comparator<IAlternative> oracle = new LinearFunctionOracle(new double[]{1,1,1});
        RankingsProvider provider = new TransitiveErrors(alternatives, oracle, 2, 1).getRankingsProvider();
        System.out.println(provider);
    }

}