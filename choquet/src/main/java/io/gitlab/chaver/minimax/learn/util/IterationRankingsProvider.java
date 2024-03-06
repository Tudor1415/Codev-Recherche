package io.gitlab.chaver.minimax.learn.util;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.learn.train.LearnStep;
import io.gitlab.chaver.minimax.ranking.Ranking;

import java.util.ArrayList;
import java.util.List;

/**
 * At each iteration, add a new ranking to the list remainingRankings
 */
public class IterationRankingsProvider implements RankingsProvider {

    private List<Ranking<IAlternative>> remainingRankings;
    private List<Ranking<IAlternative>> nextRankings = new ArrayList<>();

    public IterationRankingsProvider(List<Ranking<IAlternative>> remainingRankings) {
        this.remainingRankings = remainingRankings;
    }

    @Override
    public List<Ranking<IAlternative>> provideRankings(LearnStep step) {
        nextRankings.add(remainingRankings.remove(0));
        return nextRankings;
    }
}
