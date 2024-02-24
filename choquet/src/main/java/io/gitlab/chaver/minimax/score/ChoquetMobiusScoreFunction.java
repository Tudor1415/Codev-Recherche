package io.gitlab.chaver.minimax.score;

import io.gitlab.chaver.minimax.capacity.MobiusCapacity;
import io.gitlab.chaver.minimax.io.IAlternative;
import lombok.AllArgsConstructor;

import java.util.BitSet;

@AllArgsConstructor
public class ChoquetMobiusScoreFunction implements IScoreFunction<IAlternative> {

    public static String TYPE = "mobiusChoquet";

    private MobiusCapacity capacity;

    private double getMinVal(BitSet capacitySet, IAlternative a) {
        double minVal = Double.MAX_VALUE;
        for (int i = capacitySet.nextSetBit(0); i > -1; i = capacitySet.nextSetBit(i+1)) {
            minVal = Math.min(minVal, a.getVector()[i]);
        }
        return minVal;
    }

    @Override
    public double computeScore(IAlternative alternative) {
        double score = 0;
        BitSet[] orderedCapacitySets = capacity.getOrderedCapacitySets();
        for (int i = 1; i < orderedCapacitySets.length  ; i++) {
            BitSet capacitySet = orderedCapacitySets[i];
            score += getMinVal(capacitySet, alternative) * capacity.getCapacityValue(capacitySet);
        }
        return score;
    }
}
