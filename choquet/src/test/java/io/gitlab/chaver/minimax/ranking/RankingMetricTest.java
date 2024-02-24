package io.gitlab.chaver.minimax.ranking;

import io.gitlab.chaver.minimax.io.IAlternative;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class RankingMetricTest {

    @Test
    void testAveragePrecision() {
        Ranking<IAlternative> ranking = new Ranking<>(new int[]{0,1,2}, null);
        assertEquals(1, new AveragePrecision(3).compute(ranking, ranking));
    }

    @Test
    void testAveragePrecision2() {
        int n = 10004;
        Set<Integer> relevant = new HashSet<>(Arrays.asList(10, 582, 877, 10003));
        Set<Integer> nonRelevant = IntStream
                .range(0, n)
                .boxed()
                .collect(Collectors.toSet());
        nonRelevant.removeAll(relevant);
        List<Integer> ranks = new ArrayList<>(n);
        ranks.addAll(relevant);
        ranks.addAll(nonRelevant);
        Ranking<IAlternative> refRanking = new Ranking<>(ranks.stream().mapToInt(i -> i).toArray(), null);
        List<Integer> predRanks = new ArrayList<>(Arrays.asList(582, 17, 5666, 10003, 10, 12, 18, 877));
        Set<Integer> predRanksSet = new HashSet<>(predRanks);
        for (int i = 0; i < n; i++) {
            if (!predRanksSet.contains(i)) {
                predRanks.add(i);
            }
        }
        Ranking<IAlternative> predRanking = new Ranking<>(predRanks.stream().mapToInt(i -> i).toArray(), null);
        assertEquals(260d/400, new AveragePrecision(relevant.size()).compute(refRanking, predRanking), 0.001);
    }

}