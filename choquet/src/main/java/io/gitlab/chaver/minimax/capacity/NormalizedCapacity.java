package io.gitlab.chaver.minimax.capacity;

import lombok.Getter;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import static io.gitlab.chaver.minimax.util.BitSetUtil.intToBitSet;

public class NormalizedCapacity {

    private Map<BitSet, Double> capacities = new HashMap<>();
    private @Getter int nbCriteria;
    private @Getter int nbCapacitySets;

    public NormalizedCapacity(int nbCriteria) {
        this.nbCriteria = nbCriteria;
        nbCapacitySets = (int) Math.pow(2, nbCriteria);
        capacities.put(intToBitSet(0, nbCriteria), 0.0);
        capacities.put(intToBitSet(nbCapacitySets - 1, nbCriteria), 1.0);
    }

    public NormalizedCapacity(int nbCriteria, double[] weights) {
        this.nbCriteria = nbCriteria;
        nbCapacitySets = (int) Math.pow(2, nbCriteria);
        for (int i = 0; i < nbCapacitySets; i++) {
            capacities.put(intToBitSet(i, nbCriteria), weights[i]);
        }
    }

    public double getCapacityValue(BitSet capacitySet) {
        return capacities.get(capacitySet);
    }

    public double getCapacityValue(int capacitySetIndex) {
        return capacities.get(intToBitSet(capacitySetIndex, nbCriteria));
    }

    public void addCapacitySet(BitSet capacitySet, double value) {
        assert value >= 0 && value <= 1;
        capacities.put(capacitySet, value);
    }

    public void addCapacitySet(int capacitySetIndex, double value) {
        addCapacitySet(intToBitSet(capacitySetIndex, nbCriteria), value);
    }

    public boolean containsCapacitySet(BitSet capacitySet) {
        return capacities.containsKey(capacitySet);
    }

    public boolean containsCapacitySet(int capacitySetIndex) {
        return containsCapacitySet(intToBitSet(capacitySetIndex, nbCriteria));
    }

    public double[] getWeights() {
        double[] weights = new double[nbCapacitySets];
        for (int i = 0; i < nbCapacitySets; i++) {
            weights[i] = capacities.get(intToBitSet(i, nbCriteria));
        }
        return weights;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < nbCapacitySets; i++) {
            BitSet currentCapacitySet = intToBitSet(i, nbCriteria);
            str.append(currentCapacitySet + " : " + capacities.get(currentCapacitySet) + "\n");
        }
        return str.toString();
    }


}
