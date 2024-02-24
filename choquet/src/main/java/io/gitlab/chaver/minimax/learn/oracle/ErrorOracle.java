package io.gitlab.chaver.minimax.learn.oracle;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.util.RandomUtil;

import java.util.Comparator;

public class ErrorOracle implements Comparator<IAlternative> {

    private Comparator<IAlternative> oracle;
    private double errorProbability;
    private RandomUtil random = RandomUtil.getInstance();

    public ErrorOracle(Comparator<IAlternative> oracle, double errorProbability) {
        this.oracle = oracle;
        this.errorProbability = errorProbability;
    }

    @Override
    public int compare(IAlternative a1, IAlternative a2) {
        if (random.bernoulli(errorProbability)) {
            return -oracle.compare(a1, a2);
        }
        return oracle.compare(a1, a2);
    }
}
