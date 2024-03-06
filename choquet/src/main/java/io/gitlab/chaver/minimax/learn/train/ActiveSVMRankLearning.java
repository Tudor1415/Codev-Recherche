package io.gitlab.chaver.minimax.learn.train;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.score.FunctionParameters;

import java.util.Comparator;

public class ActiveSVMRankLearning extends ActiveRankingLearning {

    public ActiveSVMRankLearning(Comparator<IAlternative> oracle, IAlternative[] alternatives) {
        super(oracle, alternatives);
    }

    @Override
    public FunctionParameters learn() throws Exception {
        return null;
    }
}
