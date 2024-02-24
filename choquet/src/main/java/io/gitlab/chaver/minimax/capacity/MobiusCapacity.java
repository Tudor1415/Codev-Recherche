package io.gitlab.chaver.minimax.capacity;

import lombok.Getter;

import java.util.*;

import static io.gitlab.chaver.minimax.util.BitSetUtil.intToBitSet;
import static io.gitlab.chaver.minimax.util.BitSetUtil.isLexBefore;

public class MobiusCapacity {

    private Map<BitSet, Double> capacities = new HashMap<>();
    private @Getter int nbCriteria;
    private @Getter int kAdditivity;
    private @Getter BitSet[] orderedCapacitySets;

    public MobiusCapacity(int nbCriteria, int kAdditivity, Map<BitSet, Double> capacities) {
        this.nbCriteria = nbCriteria;
        this.kAdditivity = kAdditivity;
        this.capacities = capacities;
        orderCapacitySets(capacities.keySet());
    }

    public MobiusCapacity(int nbCriteria, int kAdditivity, double[] capacityArray) {
        this.nbCriteria = nbCriteria;
        this.kAdditivity = kAdditivity;
        int maxNbCapacitySets = (int) Math.pow(2, nbCriteria);
        Set<BitSet> selectedCapacitySets = new HashSet<>();
        for (int i = 0; i < maxNbCapacitySets; i++) {
            BitSet capacitySet = intToBitSet(i, nbCriteria);
            if (capacitySet.cardinality() <= kAdditivity) {
                selectedCapacitySets.add(capacitySet);
            }
        }
        orderCapacitySets(selectedCapacitySets);
        for (int i = 0; i < orderedCapacitySets.length; i++) {
            capacities.put(orderedCapacitySets[i], capacityArray[i]);
        }
    }

    private void orderCapacitySets(Set<BitSet> selectedCapacitySets) {
        orderedCapacitySets = new ArrayList<>(selectedCapacitySets).stream().sorted((o1, o2) -> {
            if (o1.cardinality() < o2.cardinality()) {
                return -1;
            }
            if (o2.cardinality() < o1.cardinality()) {
                return 1;
            }
            if (isLexBefore(o1, o2, nbCriteria)) {
                return -1;
            }
            if (isLexBefore(o2, o1, nbCriteria)) {
                return 1;
            }
            return 0;
        }).toArray(BitSet[]::new);
    }

    public double getCapacityValue(BitSet capacitySet) {
        return capacities.get(capacitySet);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (BitSet capacitySet : orderedCapacitySets) {
            str.append(capacitySet).append(" ").append(capacities.get(capacitySet)).append("\n");
        }
        return str.toString();
    }

    public double[] getOrderedCapacityValues() {
        return Arrays.stream(orderedCapacitySets).mapToDouble(b -> getCapacityValue(b)).toArray();
    }
}
