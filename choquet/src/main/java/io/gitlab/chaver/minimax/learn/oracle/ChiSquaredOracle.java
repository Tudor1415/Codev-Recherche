package io.gitlab.chaver.minimax.learn.oracle;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.score.ChiSquaredScoreFunction;
import io.gitlab.chaver.mining.rules.io.IRule;

import java.util.List;
import java.util.Map;

public class ChiSquaredOracle extends ScoreOracle {

    public static final String TYPE = "chiSquared";

    private ChiSquaredScoreFunction func;
    private Map<IAlternative, List<IRule>> mapAlternativeToRules;

    public ChiSquaredOracle(int nbTransactions, Map<IAlternative, List<IRule>> mapAlternativeToRules) {
        this.mapAlternativeToRules = mapAlternativeToRules;
        this.func = new ChiSquaredScoreFunction(nbTransactions);
    }

    @Override
    public double computeScore(IAlternative a) {
        return func.computeScore(mapAlternativeToRules.get(a).get(0));
    }
}
