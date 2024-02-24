package io.gitlab.chaver.minimax.kappalab.io;

import io.gitlab.chaver.minimax.io.IAlternative;
import lombok.Getter;

import java.util.*;

@Getter
public class KappalabInput2 {

    private List<IAlternative> alternatives = new ArrayList<>();
    private Map<IAlternative, Integer> alternativeMap = new HashMap<>();
    private List<Number[]> preferences = new ArrayList<>();

    /**
     * Add alternative if not in alternative Map and return its index
     * @param a alternative
     * @return idx of the alternative
     */
    private int addAlternative(IAlternative a) {
        if (alternativeMap.containsKey(a)) {
            return alternativeMap.get(a);
        }
        alternativeMap.put(a, alternativeMap.size());
        alternatives.add(a);
        return alternativeMap.size() - 1;
    }

    public void addPreference(IAlternative a, IAlternative b, double delta) {
        int aIndex = addAlternative(a) + 1;
        int bIndex = addAlternative(b) + 1;
        preferences.add(new Number[]{aIndex, bIndex, delta});
    }
}
