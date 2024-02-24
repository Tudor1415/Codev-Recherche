package io.gitlab.chaver.minimax.kappalab.io;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KappalabOutput {

    private double[] capacities;
    private String[] errorMessages;
    private double[] shapleyValues;
    private double[][] interactionIndices;
    private double[] obj;
}
