package io.gitlab.chaver.minimax.learn.oracle;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.score.ChiSquaredScoreFunction;
import io.gitlab.chaver.mining.rules.io.IRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChiSquaredOracle2 extends ScoreOracle {

    public static final String TYPE = "chiSquared";

    private ChiSquaredScoreFunction func;
    private Map<IAlternative, IRule> mapAlternativeToRule = new HashMap<>();

    public ChiSquaredOracle2(int nbTransactions, List<IAlternative> alternatives, List<IRule> rules) {
        for (int i = 0; i < alternatives.size(); i++) {
            mapAlternativeToRule.put(alternatives.get(i), rules.get(i));
        }
        func = new ChiSquaredScoreFunction(nbTransactions);
    }

    @Override
    public double computeScore(IAlternative a) {
        return func.computeScore(mapAlternativeToRule.get(a));
    }
}
