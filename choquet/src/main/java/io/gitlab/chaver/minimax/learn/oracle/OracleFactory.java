package io.gitlab.chaver.minimax.learn.oracle;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.mining.rules.io.IRule;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class OracleFactory {

    public static Comparator<IAlternative> create(String type, int nbTransactions,
                                           Map<IAlternative, List<IRule>> mapAlternativeToRules,
                                           double[] weights, int nbCriteria) {
        if (type.equals(LinearFunctionOracle.TYPE)) {
            return new LinearFunctionOracle(weights);
        }
        if (type.equals(OWAOracle.TYPE)) {
            return new OWAOracle(weights);
        }
        if (type.equals(ChiSquaredOracle.TYPE)) {
            return new ChiSquaredOracle(nbTransactions, mapAlternativeToRules);
        }
        if (type.equals(ChoquetOracle.TYPE)) {
            return new ChoquetOracle(weights, nbCriteria);
        }
        throw new RuntimeException("This oracle type doesn't exist : " + type);
    }
}
