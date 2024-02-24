package io.gitlab.chaver.minimax.score;

import io.gitlab.chaver.minimax.capacity.MobiusCapacity;
import io.gitlab.chaver.minimax.capacity.NormalizedCapacity;
import io.gitlab.chaver.minimax.io.IAlternative;

import java.util.Objects;

public class ScoreFunctionFactory {

    /*public IScoreFunction getScoreFunction(String type, double[] weights, NormalizedCapacity capacity,
                                           int nbTransactions) {
        if (type.equals("linear")) {
            return new LinearScoreFunction(weights);
        }
        if (type.equals("X2")) {
            return new ChiSquaredScoreFunction(nbTransactions);
        }
        if (type.equals("choquet")) {
            return new ChoquetScoreFunction(capacity);
        }
        throw new RuntimeException("This score function type doesn't exist : " + type);
    }*/

    public static IScoreFunction<IAlternative> getScoreFunction(FunctionParameters params) {
        if (Objects.equals(params.getFunctionType(), LinearScoreFunction.TYPE) && params.getWeights().length != params.getNbCriteria()) {
            throw new RuntimeException("Linear score function with nbCriteria != weights.length");
        }
        if (params.getFunctionType().equals(LinearScoreFunction.TYPE)) {
            return new LinearScoreFunction(params.getWeights());
        }
        if (params.getFunctionType().equals(ChoquetMobiusScoreFunction.TYPE)) {
            return new ChoquetMobiusScoreFunction(new MobiusCapacity(params.getNbCriteria(), params.getKAdditivity(), params.getWeights()));
        }
        return null;
    }
}
