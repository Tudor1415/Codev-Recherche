package io.gitlab.chaver.minimax.learn.util;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.learn.train.LearnStep;
import io.gitlab.chaver.minimax.ranking.Ranking;

import java.util.Arrays;
import java.util.List;

public class OneIterationRankingsProvider implements RankingsProvider {

    private Ranking<IAlternative> expectedRanking;

    public OneIterationRankingsProvider(Ranking<IAlternative> expectedRanking) {
        this.expectedRanking = expectedRanking;
    }

    @Override
    public List<Ranking<IAlternative>> provideRankings(LearnStep step) {
        return Arrays.asList(expectedRanking);
    }
}
