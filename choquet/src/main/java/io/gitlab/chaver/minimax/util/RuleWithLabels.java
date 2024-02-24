package io.gitlab.chaver.minimax.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public class RuleWithLabels {

    private String[] antecedent;
    private String[] consequent;
    private double[] measureValues;

    @Override
    public String toString() {
        return Arrays.toString(antecedent) + " => " + Arrays.toString(consequent);
    }
}
