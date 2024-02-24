package io.gitlab.chaver.minimax.learn.train.passive;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.learn.train.AbstractRankingLearning;
import io.gitlab.chaver.minimax.ranking.Ranking;


/**
 * Passive learning of ranking (i.e. the user is able to provide a complete ranking of the alternatives)
 */
public abstract class PassiveRankingLearning extends AbstractRankingLearning {

    /** Expected ranking of the alternatives */
    protected Ranking<IAlternative> expectedRanking;

    public PassiveRankingLearning(Ranking<IAlternative> expectedRanking) {
        this.expectedRanking = expectedRanking;
        this.nbMeasures = expectedRanking.getObjects()[0].getVector().length;
    }
}
