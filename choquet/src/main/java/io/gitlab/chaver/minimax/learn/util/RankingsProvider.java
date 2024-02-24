package io.gitlab.chaver.minimax.learn.util;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.learn.train.LearnStep;
import io.gitlab.chaver.minimax.ranking.Ranking;

import java.util.List;

/**
 * Provide a list of rankings
 */
public interface RankingsProvider {

    List<Ranking<IAlternative>> provideRankings(LearnStep step);
}
