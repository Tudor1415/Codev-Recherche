package io.gitlab.chaver.minimax.score;

import io.gitlab.chaver.minimax.rules.io.RuleMeasures;
import io.gitlab.chaver.mining.rules.io.IRule;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChiSquaredScoreFunction implements IScoreFunction<IRule> {

    private int nbTransactions;

    @Override
    public double computeScore(IRule rule) {
        return new RuleMeasures(rule, nbTransactions, 0).computeMeasures(new String[]{RuleMeasures.phi})[0];
    }
}
