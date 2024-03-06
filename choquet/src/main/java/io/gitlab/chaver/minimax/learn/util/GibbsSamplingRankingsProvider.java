package io.gitlab.chaver.minimax.learn.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.gitlab.chaver.minimax.io.Alternative;
import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.learn.train.LearnStep;
import io.gitlab.chaver.minimax.ranking.Ranking;
import io.gitlab.chaver.minimax.rules.io.RuleMeasures;
import io.gitlab.chaver.minimax.score.IScoreFunction;
import io.gitlab.chaver.mining.rules.io.IRule;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GibbsSamplingRankingsProvider implements RankingsProvider{

    private Comparator<IAlternative> oracle;
    private IScoreFunction<IAlternative> func;

    private int nbItems;
    int[][] discretizations;
    private double temp;
    private int[][] transactions;

    String[] measureNames;
    int nbTransactions;
    double smoothCounts;

    public GibbsSamplingRankingsProvider(Comparator<IAlternative> oracle,
                                        IScoreFunction<IAlternative> func, 
                                        double temp, int[][] transactions, 
                                        String[] measureNames, double smoothCounts,
                                        int[][] discretizations) {
        this.oracle = oracle;
        this.func = func;
        this.temp = temp;

        this.transactions = transactions;
        this.nbTransactions = transactions.length;
        this.nbItems = discretizations.length;
        this.discretizations = discretizations;

        this.measureNames = measureNames;
        this.smoothCounts = smoothCounts;
    }

	private BinaryRule initializeSample(int consequent) {
        // Initialize the first sample randomly
        int[] X = new int[this.nbItems];
        int[] Y = new int[]{0};
        
        Random random = new Random();
        for (int i = 0; i < this.nbItems; i++) {
            X[i] = (int) random.nextInt(2);
        }
        
        Y[0] = consequent;
        return new BinaryRule(X, Y, this.transactions);
    }

	public double importance_function(IAlternative J) {
        return Math.exp(temp * func.computeScore(J));
    }
    
    private static int RandomChoice(Double[] probabilities) {
        Random random = new Random();
        double randomDouble = random.nextDouble();

        double precedentProb = 0;

        for(int idx = 0; idx<probabilities.length; idx++) {
            if ((precedentProb < randomDouble) && (randomDouble <= precedentProb + probabilities[idx])) {
                return idx;
            }
        }

        return -1;
    }
    
    public IAlternative computeAlternative(IRule rule, int index) {
        return new Alternative(new RuleMeasures(rule, this.nbTransactions, this.smoothCounts).computeMeasures(this.measureNames), index);
    }

    public List<IAlternative> computeAlternatives(Set<? extends IRule> rules) {
        List<IAlternative> alternatives = new ArrayList<>(rules.size());
        int i = 0;
        
        for (IRule rule : rules) {
            alternatives.add(new Alternative(new RuleMeasures(rule, this.nbTransactions, this.smoothCounts).computeMeasures(this.measureNames), i++));
        }
        
        return alternatives;
    }
    
    public List<IAlternative> sample(int nbItterations) {
        List<IAlternative> alternatives = new ArrayList<>();
        Set<BinaryRule> sampledRulesSet;
        Set<int[]> sampledAntConsSet; // Array of X + Y

        List<Double> importance;
        List<Double> condProb;
        double importanceSum;

        double probability;

        sampledRulesSet = new HashSet<>();
        sampledAntConsSet = new HashSet<>();

        int[] init = new int[nbItems];
        Arrays.fill(init, 0);
        sampledAntConsSet.add(init);

        for (int itt = 1; itt < nbItterations; itt++) {
            int[] ruleArray = Arrays.copyOf(sampledAntConsSet.iterator().next(), this.nbItems);
            int n = ruleArray.length;
        
            for (int item_no = 0; item_no < this.nbItems; item_no++) {
                int index = 0;

                condProb = new ArrayList<>();
                importance = new ArrayList<>();

                importanceSum = 0;

                for(int possibility : this.discretizations[item_no]) { // For all discretizations on each dimension
                    // Create array X containing all elements of the ruleArray up to n-1
                    int[] X = Arrays.copyOf(ruleArray, n - 1);

                    // Create array Y containing the last element of the ruleArray (we also sample on the consequent)
                    int[] Y = new int[]{ruleArray[n - 1]};

                    X[item_no] = possibility;

                    // System.out.println("Debug: X[" + item_no + "] set to " + possibility + " : " + Arrays.toString(X));
                    BinaryRule J = new BinaryRule(X, Y, this.transactions);
    
                    // System.out.println("Debug: J: " + J.toString());
                    double impJ = importance_function(this.computeAlternative(J, index++));

                    importance.add(impJ);
                    importanceSum += impJ;
                }

                for(int i = 0; i < this.discretizations.length; i++) {
                    probability = importance.get(item_no) / importanceSum;
                    condProb.add(probability);
                }

                ruleArray[item_no] = this.discretizations[item_no][RandomChoice(condProb.toArray(new Double[0]))];
            }

            BinaryRule sampledRule = new BinaryRule(Arrays.copyOf(ruleArray, n - 1), new int[]{ruleArray[n - 1]}, this.transactions);
        
            if (sampledRule.getSupport() > 0.0) {
                sampledRulesSet.add(sampledRule);
            }
        }

        alternatives.addAll(computeAlternatives(sampledRulesSet));

        return alternatives;
    }

    @Override
    public List<Ranking<IAlternative>> provideRankings(LearnStep step) {
        func = step.getCurrentScoreFunction();
        List<IAlternative> sampledAlternatives = this.sample(10000);

        // Step 1: Count occurrences of each alternative
        Map<IAlternative, Long> alternativeCounts = sampledAlternatives.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // Step 2: Sort alternatives based on occurrences
        List<Map.Entry<IAlternative, Long>> sortedAlternatives = alternativeCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList());

        // Step 3: Get the top two alternatives
        List<IAlternative> topTwoAlternatives = sortedAlternatives.stream()
                .limit(2)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
}
