package io.gitlab.chaver.minimax.gibbs;

import com.google.gson.Gson;

import io.gitlab.chaver.minimax.experiments.util.ResultExpActive;
import io.gitlab.chaver.minimax.gibbs.rules.BinaryRule;
import io.gitlab.chaver.minimax.gibbs.sampling.SoftmaxTemp;
import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.learn.oracle.*;
import io.gitlab.chaver.minimax.learn.train.AbstractRankingLearning;
import io.gitlab.chaver.minimax.learn.train.iterative.*;
import io.gitlab.chaver.minimax.learn.util.*;
import io.gitlab.chaver.minimax.normalizer.INormalizer;
import io.gitlab.chaver.minimax.ranking.*;
import io.gitlab.chaver.minimax.score.*;
import io.gitlab.chaver.minimax.util.RandomUtil;
import io.gitlab.chaver.mining.rules.io.IRule;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static io.gitlab.chaver.minimax.learn.train.LearnUtil.*;
import static io.gitlab.chaver.minimax.rules.io.RuleMeasures.*;

public class KappalabTest {
    private long seed = 548636487354L;
    private String[] datasets = { "iris_bin" };
    private String expDirectory = "results/exp_active_GibbsChoquet/";
    private String learnDirectory = expDirectory + "learn/";
    private String rulesDirectory = "results/rules/";
    private int nbIterations = 100;
    private int k = 1;
    private String[] measureNames = { support, confidence };
    private int nbCriteria = measureNames.length;
    private double smoothCounts = 0.3d;
    private int timeLimit = 3600;
    private Gson gson = new Gson();

    private String getNbTransactionsPath(String dataset) {
        return rulesDirectory + dataset + "_prop.jsonl";
    }

    private String getDatasetPath(String dataset) {
        return "data/" + dataset + ".dat";
    }

    private RankingsProvider getRankingsProvider(List<IAlternative> trainingAlternatives,
            Comparator<IAlternative> oracle,
            IScoreFunction<IAlternative> initial) {
        return new MinGapsRankingsProvider(trainingAlternatives, oracle, initial, 50);
    }

    private List<String> getOracleNames() {
        return Arrays.asList("linear");
    }

    private List<Comparator<IAlternative>> getOracles(List<IAlternative> trainingAlt,
            int nbTransactions, List<IAlternative> allAlt, List<IRule> allRules) {
        // Initialize random weights for each oracle
        double[] weights = RandomUtil.getInstance().generateRandomWeights(measureNames.length);

        Comparator<IAlternative> linearOracle = new LinearFunctionOracle(weights);
        Comparator<IAlternative> owaOracle = new OWAOracle(weights);
        Comparator<IAlternative> choquetOracle = new ChoquetOracle(
                new CorrelationChoquetFuncBuilder(weights, trainingAlt.toArray(new IAlternative[0])).getCapacity());
        Comparator<IAlternative> chiSquaredOracle = new ChiSquaredOracle2(nbTransactions, allAlt, allRules);
        Comparator<IAlternative> giniOracle = new ScoreFunctionOracle(new OWAGini(nbCriteria));
        Comparator<IAlternative> lexminOracle = new ScoreFunctionOracle(new OWALexmin(0.01, nbCriteria));
        Comparator<IAlternative> generalizedGiniOracle = new ScoreFunctionOracle(new OWAGeneralizedGini(nbCriteria));
        return Arrays.asList(linearOracle, owaOracle, choquetOracle, chiSquaredOracle, giniOracle, lexminOracle,
                generalizedGiniOracle);
    }

    private AbstractRankingLearning getKappalab(List<IAlternative> trainingAlternatives,
            Comparator<IAlternative> oracle,
            IScoreFunction<IAlternative> initial) {
        KappalabSocketIterative kappalab = new KappalabSocketIterative(nbIterations,
                getRankingsProvider(trainingAlternatives, oracle, initial), initial, nbCriteria);
        kappalab.setTimeLimit(timeLimit);

        return kappalab;
    }

    private List<RankingMetric> getRankingMetrics(int nbRules) {
        int top1 = (int) (0.01 * nbRules);
        int top10 = (int) (0.1 * nbRules);
        return Arrays.asList(new KendallConcordanceCoeff(), new SpearmanRankCorrelationCoefficient(),
                new RecallMetric(top1), new RecallMetric(top10), new AveragePrecision(top1),
                new AveragePrecision(top10));
    }

    private static int[][] readDataset(String filePath) throws IOException {
        List<int[]> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.trim().split(" ");
                int[] row = new int[values.length];

                for (int i = 0; i < values.length; i++) {
                    row[i] = Integer.parseInt(values[i]);
                }

                rows.add(row);
            }
        }

        // Convert the list to a 2D array
        int numRows = rows.size();
        int numCols = rows.get(0).length;
        int[][] dataset = new int[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            dataset[i] = rows.get(i);
        }

        return dataset;
    }

    private Map<String, String> getRankingMetricLabels(int nbRules) {
        int top1 = (int) (0.01 * nbRules);
        int top10 = (int) (0.1 * nbRules);
        Map<String, String> labels = new HashMap<>();
        labels.put(KendallConcordanceCoeff.TYPE, KendallConcordanceCoeff.TYPE);
        labels.put(SpearmanRankCorrelationCoefficient.TYPE, SpearmanRankCorrelationCoefficient.TYPE);
        labels.put(RecallMetric.TYPE + "@" + top1, RecallMetric.TYPE + "@1%");
        labels.put(RecallMetric.TYPE + "@" + top10, RecallMetric.TYPE + "@10%");
        labels.put(AveragePrecision.TYPE + "@" + top1, AveragePrecision.TYPE + "@1%");
        labels.put(AveragePrecision.TYPE + "@" + top10, AveragePrecision.TYPE + "@10%");
        return labels;
    }

    private void writeResult(ResultExpActive res) throws IOException {
        String resPath = learnDirectory + res.getDataset() + "_" + res.getLearningAlgorithm() + "_" + res.getOracle() +
                "_" + res.getFoldIdx() + ".json";
        BufferedWriter writer = new BufferedWriter(new FileWriter(resPath));
        gson.toJson(res, writer);
        writer.close();
    }

    private void launchExp(String dataset) throws Exception {
        // General data about the dataset
        int nbTransactions = getNbTransactions(getNbTransactionsPath(dataset));

        int[][] transactions = readDataset(getDatasetPath(dataset));

        int numIterations = 50000;
        int nbItems = 12;

        SoftmaxTemp softmaxTemp = new SoftmaxTemp(transactions, nbItems, "prod", 6, new int[]{0, 0, 1});
        softmaxTemp.sample(numIterations);

        BinaryRule[] sampleArray = softmaxTemp.getSample();

        List<IRule> rules = Arrays.asList(sampleArray);

        System.out.println(softmaxTemp.toString());
        System.out.println("Rules Length: " + rules.size());

        // Computation of the alternatives for the oracle
        List<IAlternative> alternatives = computeAlternatives(rules, measureNames, nbTransactions, smoothCounts);
        alternatives = INormalizer.tchebychefNormalize(alternatives);

        // Cross-Validation
        int[][] folds = RandomUtil.getInstance().kFolds(k, alternatives.size());
        List<List<IAlternative>> trainingAlternatives = getTrainingData(alternatives, folds);
        List<List<IAlternative>> testAlternatives = getTestData(alternatives, folds);

        // Initialize random function
        double[] initialWeigths = new double[nbCriteria];
        for (int i = 0; i < nbCriteria; i++) {
            initialWeigths[i] = 1d;
        }
        IScoreFunction<IAlternative> initialFunc = new LinearScoreFunction(initialWeigths);

        // Do one itteration on all the folds for cross validation
        for (int i = 0; i < k; i++) {
            List<Comparator<IAlternative>> oracles = getOracles(trainingAlternatives.get(i),
                    nbTransactions, alternatives, rules);
            List<String> oracleNames = getOracleNames();
            for (int j = 0; j < oracles.size(); j++) {
                Comparator<IAlternative> oracle = oracles.get(j);
                String oracleName = oracleNames.get(j);
                AbstractRankingLearning algorithm = getKappalab(testAlternatives.get(i), oracle,
                        initialFunc);
                Ranking<IAlternative> refRanking = computeRankingWithOracle(oracle, trainingAlternatives.get(i));
                System.out.println(dataset + " fold " + i + " oracle " + oracleName + " algorithm "
                        + algorithm.getClass().getSimpleName());
                try {
                    RankingMetricsDashboard dashboard = new RankingMetricsDashboard(
                            getRankingMetrics(testAlternatives.get(i).size()), refRanking,
                            getRankingMetricLabels(testAlternatives.get(i).size()));
                    algorithm.addObserver(dashboard);
                    FunctionParameters func = algorithm.learn();
                    ResultExpActive res = new ResultExpActive();
                    res.setMetricValues(dashboard.getMetricValues());
                    res.setOracle(oracleName);
                    res.setLearningAlgorithm(algorithm.getClass().getSimpleName());
                    res.setDataset(dataset);
                    res.setFoldIdx(i);
                    res.setNbIterations(nbIterations);
                    res.setTimeToLearn(func.getTimeToLearn());
                    if (func.isTimeOut()) {
                        res.setTimeOut(true);
                        res.setTimeToLearn(timeLimit);
                    } else if (func.getErrorMessages() != null) {
                        res.setErrorMessages(func.getErrorMessages());
                    }
                    writeResult(res);
                } catch (Exception e) {
                    System.out.println("Error for " + algorithm + " ");
                    e.printStackTrace();
                }
            }
        }
    }

    public void run() throws Exception {
        RandomUtil.getInstance().setSeed(seed);
        for (String dataset : datasets) {
            launchExp(dataset);
        }
    }

    public static void main(String[] args) throws Exception {
        new KappalabTest().run();
    }
}
