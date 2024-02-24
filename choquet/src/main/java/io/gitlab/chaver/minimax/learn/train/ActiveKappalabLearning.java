package io.gitlab.chaver.minimax.learn.train;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.score.FunctionParameters;

import java.util.Comparator;

public class ActiveKappalabLearning extends ActiveRankingLearning {

    public ActiveKappalabLearning(Comparator<IAlternative> oracle, IAlternative[] alternatives) {
        super(oracle, alternatives);
    }

    @Override
    public FunctionParameters learn() throws Exception {
        return null;
    }
}
