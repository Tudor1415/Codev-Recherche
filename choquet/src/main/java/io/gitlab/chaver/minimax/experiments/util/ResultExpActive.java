package io.gitlab.chaver.minimax.experiments.util;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ResultExpActive {

    private double timeToLearn;
    private boolean timeOut;
    private Map<String, List<Double>> metricValues;
    private int nbIterations;
    private String oracle;
    private String learningAlgorithm;
    private String dataset;
    private int foldIdx;
    private String[] errorMessages;
    private double errorProbability;
}
