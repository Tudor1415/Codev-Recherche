package io.gitlab.chaver.minimax.learn.util;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.learn.train.LearnStep;
import io.gitlab.chaver.minimax.ranking.Ranking;
import io.gitlab.chaver.minimax.score.IScoreFunction;
import io.gitlab.chaver.minimax.util.RandomUtil;

import java.util.*;

import static java.lang.Math.abs;
import static io.gitlab.chaver.minimax.learn.train.LearnUtil.*;

/**
 * An alternative selection heuristic (section 4.3.1, page 6)
 * See Belmecheri et al. - Boosting the learning for ranking patterns
 */
public class MinGapsRankingsProvider implements RankingsProvider {

    private List<IAlternative> alternatives;
    private Comparator<IAlternative> oracle;
    private IScoreFunction<IAlternative> func;
    private int sampleSize;
    private RandomUtil random = RandomUtil.getInstance();
    private Set<List<Integer>> selectedPairs = new HashSet<>();
    private List<Ranking<IAlternative>> rankings = new ArrayList<>();

    public MinGapsRankingsProvider(List<IAlternative> alternatives, Comparator<IAlternative> oracle,
                                   IScoreFunction<IAlternative> func, int sampleSize) {
        this.alternatives = alternatives;
        this.oracle = oracle;
        this.func = func;
        this.sampleSize = sampleSize;
    }

    private double measureGap(IAlternative a, IAlternative b) {
        double num = abs(func.computeScore(a) - func.computeScore(b));
        double denum = 0d;
        for (int i = 0; i < a.getVector().length; i++) {
            denum += abs(a.getVector()[i] - b.getVector()[i]);
        }
        return num / denum;
    }

    private int[] randomSample() {
        return random.kFolds(1, alternatives.size(), sampleSize)[0];

    }

    @Override
    public List<Ranking<IAlternative>> provideRankings(LearnStep step) {
        func = step.getCurrentScoreFunction();
        int[] randomSample = randomSample();
        double minGap = Double.MAX_VALUE;
        int a1Index = -1;
        int a2Index = -1;
        for (int i = 0; i < sampleSize; i++) {
            for (int j = i + 1; j < sampleSize; j++) {
                int iIndex = randomSample[i];
                int jIndex = randomSample[j];
                if (selectedPairs.contains(Arrays.asList(iIndex, jIndex))) {
                    continue;
                }
                double measureGap = measureGap(alternatives.get(iIndex), alternatives.get(jIndex));
                if (measureGap < minGap) {
                    minGap = measureGap;
                    a1Index = iIndex;
                    a2Index = jIndex;
                }
            }
        }
        List<IAlternative> selected = Arrays.asList(alternatives.get(a1Index), alternatives.get(a2Index));
        selectedPairs.add(Arrays.asList(a1Index, a2Index));
        rankings.add(computeRankingWithOracle(oracle, selected));
        return rankings;
    }
}
