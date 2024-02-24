package io.gitlab.chaver.minimax.score;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Class to save the parameters of a score function in a file
 */
@Getter
@Setter
@ToString
public class FunctionParameters {

    private String functionType;
    private double[] weights;
    private int kAdditivity;
    private int nbCriteria;
    private double timeToLearn;
    private boolean timeOut;
    private String[] errorMessages;
    private int nbIterations;
    private double[] shapleyValues;
    private double[][] interactionIndices;
    private double[] obj;
}
