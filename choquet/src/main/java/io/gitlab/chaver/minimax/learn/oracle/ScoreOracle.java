package io.gitlab.chaver.minimax.learn.oracle;

import io.gitlab.chaver.minimax.io.IAlternative;

import java.util.Comparator;

public abstract class ScoreOracle implements Comparator<IAlternative> {

    @Override
    public int compare(IAlternative a, IAlternative b) {
        double scoreA = computeScore(a);
        double scoreB = computeScore(b);
        return -Double.compare(scoreA, scoreB);
    }

    public abstract double computeScore(IAlternative a);
}
