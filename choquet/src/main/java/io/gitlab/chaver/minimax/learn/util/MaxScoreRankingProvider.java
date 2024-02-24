package io.gitlab.chaver.minimax.learn.util;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.learn.train.LearnStep;
import io.gitlab.chaver.minimax.ranking.Ranking;
import io.gitlab.chaver.minimax.score.IScoreFunction;

import java.util.*;

import static io.gitlab.chaver.minimax.learn.train.LearnUtil.*;

public class MaxScoreRankingProvider implements RankingsProvider {

    private List<IAlternative> alternatives;
    private Comparator<IAlternative> oracle;
    private Set<IAlternative> selectedAlternatives = new HashSet<>();

    public MaxScoreRankingProvider(List<IAlternative> alternatives, Comparator<IAlternative> oracle,
                                   IScoreFunction<IAlternative> initial) {
        this.alternatives = alternatives;
        this.oracle = oracle;
        selectedAlternatives.add(computeBestAlternative(alternatives, initial));
    }

    @Override
    public List<Ranking<IAlternative>> provideRankings(LearnStep step) {
        IScoreFunction<IAlternative> func = step.getCurrentScoreFunction();
        double maxScore = -Double.MAX_VALUE;
        IAlternative best = null;
        for (IAlternative a : alternatives) {
            if (selectedAlternatives.contains(a)) continue;
            double score = func.computeScore(a);
            if (score > maxScore) {
                best = a;
                maxScore = score;
            }
        }
        selectedAlternatives.add(best);
        Ranking<IAlternative> ranking = computeRankingWithOracle(oracle, new ArrayList<>(selectedAlternatives));
        return Collections.singletonList(ranking);
    }
}
