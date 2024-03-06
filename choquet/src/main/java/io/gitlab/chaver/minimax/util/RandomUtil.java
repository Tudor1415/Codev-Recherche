package io.gitlab.chaver.minimax.util;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.gitlab.chaver.minimax.util.BitSetUtil.intToBitSet;
import static io.gitlab.chaver.minimax.util.BitSetUtil.isSubsetOf;

public class RandomUtil {

    private Random random = new Random();
    private static RandomUtil INSTANCE;

    private RandomUtil() {}

    public static RandomUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RandomUtil();
        }
        return INSTANCE;
    }

    /**
     * Generate k array of integers, where each array represents the indexes of the training data for a fold
     * @param k number of folds
     * @param size number of data rows
     * @return the k folds
     */
    public int[][] kFolds(int k, int size) {
        List<Integer> idx = IntStream.range(0, size).boxed().collect(Collectors.toList());
        Collections.shuffle(idx, random);
        int foldSize = size / k;
        int[][] folds = new int[k][];
        for (int i = 0; i < k; i++) {
            folds[i] = new int[foldSize];
            for (int j = 0; j < foldSize; j++) {
                folds[i][j] = idx.get(i * foldSize + j);
            }
        }
        return folds;
    }

    /**
     * Generate k array of integers, where each array represents the indexes of the training data for a fold
     * @param k number of folds
     * @param size number of data rows
     * @param foldSize size of each fold
     * @return the k folds
     */
    public int[][] kFolds(int k, int size, int foldSize) {
        List<Integer> idx = IntStream.range(0, size).boxed().collect(Collectors.toList());
        int[][] folds = new int[k][];
        for (int i = 0; i < k; i++) {
            Collections.shuffle(idx, random);
            folds[i] = new int[foldSize];
            for (int j = 0; j < foldSize; j++) {
                folds[i][j] = idx.get(j);
            }
        }
        return folds;
    }

    /**
     * k folds but returns for each int between [0, size-1] the corresponding folds
     * ex: [0,1,1,0] means that objects at index 0 and 3 belong to fold 0, objects at index 1 and 2 to fold 1
     * @param k
     * @param size
     * @return
     */
    public int[] kFoldsIndex(int k, int size) {
        int[][] kFolds = kFolds(k, size);
        int[] foldsIndex = new int[size];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < kFolds[i].length; j++) {
                int idx = kFolds[i][j];
                foldsIndex[idx] = i;
            }
        }
        return foldsIndex;
    }

    /**
     * Generate an array of random doubles in [0,1] such that the sum of the array is equal to 1
     * @param nbWeights size of the array
     * @return the array of random doubles
     */
    public double[] generateRandomWeights(int nbWeights) {
        assert nbWeights >= 1;
        double[] weights = new double[nbWeights];
        double sum = 0;
        for (int i = 0; i < nbWeights; i++) {
            weights[i] = -1 * Math.log(1.0 - random.nextDouble()) + 0.01;
            sum += weights[i];
        }
        for (int i = 0; i < nbWeights; i++) {
            weights[i] = weights[i] / sum;
        }
        return weights;
    }

    public Map<BitSet, Double> generateRandomChoquetCapacities(int nbCriteria) {
        Map<BitSet, Double> capacities = new HashMap<>();
        int nbCapacity = (int) Math.pow(2, nbCriteria);
        for (int i = 1; i < nbCapacity - 1; i++) {
            BitSet capacity = intToBitSet(i, nbCriteria);
            double capacityValue = random.nextDouble();
            capacities.put(capacity, capacityValue);
        }
        capacities.put(intToBitSet(0, nbCriteria), 0d);
        capacities.put(intToBitSet(nbCapacity - 1, nbCriteria), 1d);
        for (BitSet a : capacities.keySet()) {
            for (BitSet b : capacities.keySet()) {
                if (isSubsetOf(a, b) && capacities.get(a) > capacities.get(b)) {
                    capacities.put(b, capacities.get(a));
                }
            }
        }
        return capacities;
    }

    public double nextDouble() {
        return random.nextDouble();
    }

    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    public void setSeed(long seed) {
        random.setSeed(seed);
    }

    public boolean bernoulli(double p) {
        return p >= nextDouble();
    }

    public double[][] generateRandomVectors(int n, int vectorSize) {
        double[][] randomVectors = new double[n][];
        for (int i = 0; i < n; i++) {
            randomVectors[i] = new double[vectorSize];
            for (int j = 0; j < vectorSize; j++) {
                randomVectors[i][j] = random.nextDouble();
            }
        }
        return randomVectors;
    }

    public static String getFoldPath(String alternativesPath, String type, int foldIdx) {
        return alternativesPath.substring(0, alternativesPath.length() - 5) + type + foldIdx + ".json";
    }

    public void shuffle(List<?> l) {
        Collections.shuffle(l, random);
    }

    public <T> T selectRandomElement(List<T> l) {
        return l.get(nextInt(l.size()));
    }

    public int selectRandomElement(int[] elt, int maxIndex) {
        return elt[nextInt(maxIndex)];
    }
}
