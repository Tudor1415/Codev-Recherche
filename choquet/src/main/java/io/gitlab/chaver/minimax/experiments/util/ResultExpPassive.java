package io.gitlab.chaver.minimax.experiments.util;

import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
public class ResultExpPassive {

    private double timeToLearn;
    private boolean timeOut;
    private Map<String, Double> metricValues;
    private int foldSize;
    private int foldIdx;
    private String oracle;
    private String learningAlgorithm;
    private String dataset;
}
