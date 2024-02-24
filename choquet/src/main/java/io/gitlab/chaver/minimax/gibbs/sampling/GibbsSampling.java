package io.gitlab.chaver.minimax.gibbs.sampling;

import java.util.Arrays;
import java.util.Random;

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

    public GibbsSampling(int[][] transactions, int nbItems) {
        this.transactions = transactions;

        this.nbItems = nbItems;

        initializeSample();
    }

    private void initializeSample() {
        // Initialize the first sample randomly
        int[] X = new int[nbItems];
        int[] Y = new int[] { 0 };

        Random random = new Random();
        for (int i = 0; i < nbItems; i++) {
            X[i] = (int) random.nextInt(2);
        }

        // Setting the class to 0
        Y[0] = 0;
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
        // Mock sample
        BinaryRule[] mockSample = new BinaryRule[nbIterations];
        mockSample[0] = this.J;

        double probability;

        for (int itt = 1; itt < nbIterations; itt++) {
            int[] X = Arrays.copyOf(mockSample[itt - 1].getX(), mockSample[itt - 1].getX().length); // Create a deep
                                                                                                    // copy of J.getX()

            for (int item_no = 0; item_no < nbItems; item_no++) {
                X[item_no] = 1;
                BinaryRule J_0 = new BinaryRule(Arrays.copyOf(X, X.length), J.getY(), this.transactions);

                X[item_no] = 0;
                BinaryRule J_1 = new BinaryRule(Arrays.copyOf(X, X.length), J.getY(), this.transactions);

                probability = probability_function(J_1) / (probability_function(J_1) + probability_function(J_0));

                X[item_no] = Bernoulli(probability);
            }

            BinaryRule sampledRule = new BinaryRule(Arrays.copyOf(X, X.length), J.getY(), this.transactions);
            mockSample[itt] = sampledRule;
        }

        // Filter out rules with support 0 from the mock sample to create the real
        // sample
        BinaryRule[] filteredSample = Arrays.stream(mockSample)
                .filter(rule -> rule.getSupport() > 0)
                .toArray(BinaryRule[]::new);

        this.sample = filteredSample;

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%-45s%-10s%-10s%n", "Rule", "Support", "Confidence"));
        sb.append(String.format("%-45s%-10s%-10s%n", "----", "-------", "----------"));

        // Print each rule in the sample
        for (BinaryRule rule : this.sample) {
            int[] X = rule.getX();
            int[] Y = rule.getY();

            double support = rule.getSupport();
            double confidence = rule.getConfidence();

            sb.append(String.format("%-45s%-10.4f%-10.4f%n", Arrays.toString(X) + " => " + Arrays.toString(Y), support,
                    confidence));
        }

        return sb.toString();
    }

}
