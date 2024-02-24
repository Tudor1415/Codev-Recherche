package io.gitlab.chaver.minimax.learn.oracle;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.score.IScoreFunction;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ScoreFunctionOracle extends ScoreOracle {

    private IScoreFunction<IAlternative> scoreFunction;

    @Override
    public double computeScore(IAlternative a) {
        return scoreFunction.computeScore(a);
    }
}
