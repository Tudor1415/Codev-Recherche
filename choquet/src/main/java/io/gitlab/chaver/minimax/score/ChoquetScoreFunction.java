package io.gitlab.chaver.minimax.score;

import io.gitlab.chaver.minimax.capacity.NormalizedCapacity;
import io.gitlab.chaver.minimax.io.IAlternative;
import lombok.AllArgsConstructor;

import java.util.BitSet;

@AllArgsConstructor
public class ChoquetScoreFunction implements IScoreFunction<IAlternative> {

    private NormalizedCapacity capacity;

    @Override
    public double computeScore(IAlternative alternative) {
        int nbCriteria = alternative.getVector().length;
        BitSet capacitySet = new BitSet(nbCriteria);
        capacitySet.set(0, nbCriteria);
        double score = 0d;
        double prevValue = 0d;
        for (int i = 0; i < nbCriteria; i++) {
            score += (alternative.getOrderedValue(i) - prevValue) * capacity.getCapacityValue(capacitySet);
            capacitySet.set(alternative.getOrderedPermutation()[i], false);
            prevValue = alternative.getOrderedValue(i);
        }
        return score;
    }
}
