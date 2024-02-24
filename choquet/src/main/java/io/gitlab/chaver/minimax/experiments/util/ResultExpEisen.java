package io.gitlab.chaver.minimax.experiments.util;

import io.gitlab.chaver.minimax.util.RuleWithLabels;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ResultExpEisen {

    private double timeToLearn;
    private boolean timeOut;
    private Map<String, Double> metricValues;
    private int foldSize;
    private int foldIdx;
    private String oracle;
    private String learningAlgorithm;
    private String dataset;
    private double[][] interactionIndices;
    private double[] shapleyValues;
    private List<RuleWithLabels> topK;
    private int[] oracleTopK;
}
