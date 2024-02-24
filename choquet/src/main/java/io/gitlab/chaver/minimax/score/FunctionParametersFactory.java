package io.gitlab.chaver.minimax.score;

public class FunctionParametersFactory {

    public static FunctionParameters getFunctionParameters(String functionType, int nbCriteria,
                                                           int kAdditivity, double[] weights, double timeToLearn) {
        FunctionParameters params = new FunctionParameters();
        params.setFunctionType(functionType);
        params.setWeights(weights);
        params.setNbCriteria(nbCriteria);
        params.setKAdditivity(kAdditivity);
        params.setTimeToLearn(timeToLearn);
        return params;
    }
}
