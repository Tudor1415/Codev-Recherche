package io.gitlab.chaver.minimax.util;

import java.util.BitSet;

public class BitSetUtil {

    /**
     * Convert an integer to BitSet.
     * @param value Value to convert.
     * @param capacity Initial capacity of the BitSet
     * @return the corresponding BitSet
     */
    public static BitSet intToBitSet(int value, int capacity) {
        BitSet bits = new BitSet(capacity);
        int index = 0;
        while (value != 0) {
            if (value % 2 != 0) {
                bits.set(index);
            }
            ++index;
            value = value >>> 1;
        }

        return bits;
    }

    /**
     * Check if b is a subset of b2
     * @param b BitSet
     * @param b2 BitSet
     * @return true if b is a subset of b2
     */
    public static boolean isSubsetOf(BitSet b, BitSet b2) {
        BitSet copy = (BitSet) b.clone();
        copy.andNot(b2);
        return copy.equals(new BitSet());
    }

    /**
     * Create a BitSet with the specified size and one bit set at the index posSetBit
     * @param size size of the BitSet
     * @param posSetBit index of the bit to set
     * @return the created BitSet
     */
    public static BitSet createBitSet(int size, int posSetBit) {
        BitSet bits = new BitSet(size);
        bits.set(posSetBit);
        return bits;
    }

    /**
     * Check if b is before b2 in lexicographic order
     * @param b BitSet
     * @param b2 BitSet
     * @param size Number of bits to consider
     * @return true if b is before b2 in lexicographic order (or b = b2)
     */
    public static boolean isLexBefore(BitSet b, BitSet b2, int size) {
        for (int i = 0; i < size; i++) {
            if (b.get(i) && !b2.get(i)) return true;
            if (!b.get(i) && b2.get(i)) return false;
        }
        return true;
    }

    /**
     * Return the min value considering only the bits set in b
     * @param b BitSet
     * @param vector Vector of values
     * @return the min value of the vector considering only bits set in b
     */
    public static double getMinVal(BitSet b, double[] vector) {
        double minVal = Double.MAX_VALUE;
        for (int i = b.nextSetBit(0); i > -1; i = b.nextSetBit(i+1)) {
            minVal = Math.min(minVal, vector[i]);
        }
        return minVal;
    }
}
