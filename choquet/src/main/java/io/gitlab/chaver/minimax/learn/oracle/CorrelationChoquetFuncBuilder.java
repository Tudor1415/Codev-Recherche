package io.gitlab.chaver.minimax.learn.oracle;

import io.gitlab.chaver.minimax.capacity.NormalizedCapacity;
import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.util.BitSetUtil;
import lombok.Getter;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import java.util.BitSet;

import static io.gitlab.chaver.minimax.util.BitSetUtil.intToBitSet;

/**
 * Build the capacity of Choquet using correlations between measures of the alternatives
 */
public class CorrelationChoquetFuncBuilder {

    public static String TYPE = "corrChoquet";

    @Getter
    private NormalizedCapacity capacity;

    private double computeCapacityValue(BitSet capacitySet, RealMatrix correlations, NormalizedCapacity capacity) {
        double maxCapacity = -Double.MAX_VALUE;
        int maxCapacityIdx = -1;
        for (int i = capacitySet.nextSetBit(0); i > -1; i = capacitySet.nextSetBit(i+1)) {
            BitSet subCapacitySet = (BitSet) capacitySet.clone();
            subCapacitySet.set(i, false);
            if (capacity.getCapacityValue(subCapacitySet) > maxCapacity) {
                maxCapacity = capacity.getCapacityValue(subCapacitySet);
                maxCapacityIdx = i;
            }
        }
        double sumCorrelation = 0;
        for (int i = capacitySet.nextSetBit(0); i > -1; i = capacitySet.nextSetBit(i+1)) {
            if (i != maxCapacityIdx) {
                sumCorrelation += correlations.getEntry(maxCapacityIdx, i);
            }
        }
        return Math.min(1.0,
                maxCapacity + (1 - sumCorrelation / (capacitySet.cardinality() - 1)) * capacity.getCapacityValue(maxCapacityIdx));
    }

    public CorrelationChoquetFuncBuilder(double[] individualWeights, IAlternative[] alternatives) {
        int nbRows = alternatives.length;
        int nbCols = alternatives[0].getVector().length;
        RealMatrix alternativesMatrix = new Array2DRowRealMatrix(nbRows, nbCols);
        for (int i = 0; i < nbRows; i++) {
            for (int j = 0; j < nbCols; j++) {
                alternativesMatrix.addToEntry(i, j, alternatives[i].getVector()[j]);
            }
        }
        RealMatrix correlations = new PearsonsCorrelation().computeCorrelationMatrix(alternativesMatrix);
        capacity = new NormalizedCapacity(nbCols);
        for (int i = 0; i < individualWeights.length; i++) {
            capacity.addCapacitySet(BitSetUtil.createBitSet(nbCols, i), individualWeights[i]);
        }
        int nbCapacitySet = capacity.getNbCapacitySets();
        for (int i = 0; i < nbCapacitySet; i++) {
            BitSet capacitySet = intToBitSet(i, nbCols);
            if (!capacity.containsCapacitySet(capacitySet)) {
                capacity.addCapacitySet(capacitySet, computeCapacityValue(capacitySet, correlations, capacity));
            }
        }
    }
}
