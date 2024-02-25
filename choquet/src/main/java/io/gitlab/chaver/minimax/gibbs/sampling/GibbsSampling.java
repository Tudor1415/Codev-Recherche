package io.gitlab.chaver.minimax.gibbs.sampling;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import io.gitlab.chaver.minimax.gibbs.rules.BinaryRule;

public class GibbsSampling {

    // Variables regarding the data set
    private int[][] transactions;
    private int nbItems;

    // Variables regarding the samples
    public BinaryRule[] sample;

    private BinaryRule J;

    // Getters and setters
    public int[][] getTransactions() {
        return transactions;
    }

    public void setTransactions(int[][] transactions) {
        this.transactions = transactions;
    }

    public int getNbItems() {
        return nbItems;
    }

    public void setNbItems(int nbItems) {
        this.nbItems = nbItems;
    }

    public BinaryRule[] getSample() {
        return sample;
    }

    public GibbsSampling(int[][] transactions, int nbItems, int[] consequent) {
        this.transactions = transactions;

        this.nbItems = nbItems;

        initializeSample(consequent);
    }

    private void initializeSample(int[] Y) {
        // Initialize the first sample randomly
        int[] X = new int[nbItems];

        Random random = new Random();
        for (int i = 0; i < nbItems; i++) {
            X[i] = (int) random.nextInt(2);
        }

        J = new BinaryRule(X, Y, this.transactions);
    }

    public double probability_function(BinaryRule J) {
        return 0.5;
    }

    private static int Bernoulli(double probability) {
        Random random = new Random();
        if (random.nextDouble() < probability) {
            return 1;
        }

        return 0;
    }
    
    public void sample(int nbIterations) {
        // Mock sample as a Set
        Set<BinaryRule> SampleSet = new HashSet<>();
        SampleSet.add(this.J);

        double probability;

        for (int itt = 1; itt < nbIterations; itt++) {
            int[] X = Arrays.copyOf(SampleSet.iterator().next().getX(), nbItems); // Assuming nbItems is the length of J.getX()

            for (int item_no = 0; item_no < nbItems; item_no++) {
                X[item_no] = 1;
                BinaryRule J_0 = new BinaryRule(Arrays.copyOf(X, X.length), J.getY(), this.transactions);

                X[item_no] = 0;
                BinaryRule J_1 = new BinaryRule(Arrays.copyOf(X, X.length), J.getY(), this.transactions);

                probability = probability_function(J_1) / (probability_function(J_1) + probability_function(J_0));

                X[item_no] = Bernoulli(probability);
            }

            BinaryRule sampledRule = new BinaryRule(Arrays.copyOf(X, X.length), J.getY(), this.transactions);
            if(sampledRule.getSupport() > 0.0) {
                SampleSet.add(sampledRule);
            }
        }

        SampleSet.remove(this.J);

        // Sort the SampleSet using g_function values (product of support and confidence)
        Set<BinaryRule> sortedSampleSet = SampleSet.stream()
                .sorted(Comparator.comparingDouble(rule -> - rule.getSupport() * rule.getConfidence()))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        this.sample = sortedSampleSet.toArray(new BinaryRule[0]);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%-55s%-10s%-10s%n", "Rule", "Support", "Confidence"));
        sb.append(String.format("%-55s%-10s%-10s%n", "----", "-------", "----------"));

        // Print each rule in the sample
        for (BinaryRule rule : this.sample) {
            int[] X = rule.getX();
            int[] Y = rule.getY();

            double support = rule.getSupport();
            double confidence = rule.getConfidence();

            sb.append(String.format("%-55s%-10.4f%-10.4f%n", Arrays.toString(X) + " => " + Arrays.toString(Y), support,
                    confidence));
        }

        return sb.toString();
    }

}
