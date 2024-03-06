package io.gitlab.chaver.minimax.learn.util;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.learn.train.LearnStep;
import io.gitlab.chaver.minimax.score.IScoreFunction;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.lang.Math.*;

/**
 * An alternative selection heuristic (section 4.3.1, page 6)
 * See Belmecheri et al. - Boosting the learning for ranking patterns
 */
public class MinGapsHeuristic implements AlternativesSelector {

    private IScoreFunction<IAlternative> func;

    private double measureGap(IAlternative a, IAlternative b) {
        double num = abs(func.computeScore(a) - func.computeScore(b));
        double denum = 0d;
        for (int i = 0; i < a.getVector().length; i++) {
            denum += abs(a.getVector()[i] - b.getVector()[i]);
        }
        return num / denum;
    }

    @Override
    public List<IAlternative> selectAlternatives(LearnStep step) {
        List<IAlternative> alternatives = step.getCurrentAlternatives();
        Set<List<Integer>> selectedPairs = step.getSelectedPairs();
        func = step.getCurrentScoreFunction();
        int n = alternatives.size();
        double minGap = Double.MAX_VALUE;
        int a1Index = -1;
        int a2Index = -1;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (selectedPairs.contains(Arrays.asList(i, j))) continue;
                double measureGap = measureGap(alternatives.get(i), alternatives.get(j));
                if (measureGap < minGap) {
                    minGap = measureGap;
                    a1Index = i;
                    a2Index = j;
                }
            }
        }
        selectedPairs.add(Arrays.asList(a1Index, a2Index));
        return Arrays.asList(alternatives.get(a1Index), alternatives.get(a2Index));
    }
}
