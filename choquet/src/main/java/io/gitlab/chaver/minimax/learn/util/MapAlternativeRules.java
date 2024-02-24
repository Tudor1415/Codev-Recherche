package io.gitlab.chaver.minimax.learn.util;

import io.gitlab.chaver.minimax.io.Alternative;
import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.mining.rules.io.IRule;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Useful for saving map alternative -> rules in JSON format
 */
@AllArgsConstructor
@Getter
public class MapAlternativeRules {

    /** Vectors of the alternatives */
    private double[][] alternatives;

    /** Index of the corresponding rules */
    private int[][] rulesIdx;

    public Map<IAlternative, List<IRule>> convertToMap(List<IRule> rules) {
        Map<IAlternative, List<IRule>> map = new HashMap<>();
        for (int i = 0; i < alternatives.length; i++) {
            List<IRule> selectedRules = Arrays.stream(rulesIdx[i]).mapToObj(rules::get).collect(Collectors.toList());
            map.put(new Alternative(alternatives[i]), selectedRules);
        }
        return map;
    }

    public int size() {
        return alternatives.length;
    }

    public MapAlternativeRules subset(int[] idx) {
        double[][] selectedAlternatives = Arrays.stream(idx).mapToObj(i -> alternatives[i]).toArray(double[][]::new);
        int[][] selectedRulesIdx = Arrays.stream(idx).mapToObj(i -> rulesIdx[i]).toArray(int[][]::new);
        return new MapAlternativeRules(selectedAlternatives, selectedRulesIdx);
    }
}
