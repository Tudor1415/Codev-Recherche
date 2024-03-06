package io.gitlab.chaver.minimax.learn.train;

import io.gitlab.chaver.minimax.io.IAlternative;

import java.util.Comparator;

/**
 * Active learning of the ranking (i.e. the user is simulated by an oracle which is used to compare the patterns)
 */
public abstract class ActiveRankingLearning extends AbstractRankingLearning {

    /** Oracle to compare alternatives between them */
    protected Comparator<IAlternative> oracle;
    /** Alternatives to rank */
    protected IAlternative[] alternatives;

    public ActiveRankingLearning(Comparator<IAlternative> oracle, IAlternative[] alternatives) {
        this.oracle = oracle;
        this.alternatives = alternatives;
        this.nbMeasures = alternatives[0].getVector().length;
    }
}
