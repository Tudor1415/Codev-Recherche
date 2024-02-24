package io.gitlab.chaver.minimax.util;

import io.gitlab.chaver.minimax.capacity.NormalizedCapacity;

public class ChoquetUtil {

    /**
     * Convert an array of Choquet capacities to a Map which associates a BitSet key to each capacity
     * @param capacities array of capacities
     * @param nbCriteria number of criteria
     * @return the associated map
     * Example : capacities = {0, 0.1, 0.2, 0.5, 0.3, 0.6, 0.7, 1} means that \mu{1} = 0.1, \mu{2} = 0.2, \mu{1,2} = 0.5,
     * \mu{3} = 0.3, \mu{1,3} = 0.6, \mu{2,3} = 0.7
     */
    public static NormalizedCapacity convertToCapacity(double[] capacities, int nbCriteria) {
        NormalizedCapacity capacity = new NormalizedCapacity(nbCriteria);
        assert capacities.length == capacity.getNbCapacitySets();
        for (int i = 1; i < capacity.getNbCapacitySets() - 1; i++) {
            capacity.addCapacitySet(i, capacities[i]);
        }
        return capacity;
    }
}
