package io.gitlab.chaver.minimax.experiments.launch;

import com.google.gson.Gson;
import io.gitlab.chaver.minimax.experiments.util.ResultExpEisen;
import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.learn.oracle.*;
import io.gitlab.chaver.minimax.learn.train.AbstractRankingLearning;
import io.gitlab.chaver.minimax.learn.train.passive.AHPRankLearn;
import io.gitlab.chaver.minimax.learn.train.passive.KappalabRankLearn;
import io.gitlab.chaver.minimax.learn.train.passive.SVMRankLearn;
import io.gitlab.chaver.minimax.normalizer.INormalizer;
import io.gitlab.chaver.minimax.ranking.*;
import io.gitlab.chaver.minimax.score.*;
import io.gitlab.chaver.minimax.util.RandomUtil;
import io.gitlab.chaver.minimax.util.RuleWithLabels;
import io.gitlab.chaver.mining.patterns.io.DatReader;
import io.gitlab.chaver.mining.patterns.io.Database;
import io.gitlab.chaver.mining.rules.io.IRule;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;

import static io.gitlab.chaver.minimax.learn.train.LearnUtil.*;
import static io.gitlab.chaver.minimax.rules.io.RuleMeasures.*;

public class ExperimentEisen {

    private long seed = 548636487354L;
    private String[] datasets = {"eisen"};
    private String expDirectory = "results/exp_eisen/";
    private String learnDirectory = expDirectory + "learn/";
    private String rulesDirectory = "results/rules/";
    private int[] foldsSize = {100};
    private int k = 1;
    private String[] measureNames = {yuleQ, cosine, kruskal, addedValue, certainty};
    private int nbCriteria = measureNames.length;
    private double smoothCounts = 0.1d;
    private int timeLimit = 3600;
    private Gson gson = new Gson();
    private int topK = 100;


    private String getRulesPath(String dataset) {
        return rulesDirectory + dataset + "_sols.jsonl";
    }

    private String getNbTransactionsPath(String dataset) {
        return rulesDirectory + dataset + "_prop.jsonl";
    }

    private String getDatasetPath() {
        return "data/eisen.dat";
    }

    private String getLabelsPath() {
        return "data/eisen.names";
    }

    private List<Comparator<IAlternative>> getOracles(List<IAlternative> trainingAlt, List<IRule> trainingRules,
                                                      int nbTransactions, List<IAlternative> allAlt, List<IRule> allRules,
                                                      List<IAlternative> testAlt) {
        double[] weights = RandomUtil.getInstance().generateRandomWeights(measureNames.length);
        //Comparator<IAlternative> linearOracle = new LinearFunctionOracle(weights);
        Comparator<IAlternative> owaOracle = new OWAOracle(weights);
        Comparator<IAlternative> choquetOracle = new ChoquetOracle(
                new CorrelationChoquetFuncBuilder(weights, trainingAlt.toArray(new IAlternative[0])).getCapacity()
        );
        //Comparator<IAlternative> chiSquaredOracle = new ChiSquaredOracle2(nbTransactions, allAlt, allRules);
        Comparator<IAlternative> giniOracle = new ScoreFunctionOracle(new OWAGini(nbCriteria));
        Comparator<IAlternative> lexminOracle = new ScoreFunctionOracle(new OWALexmin(0.01, nbCriteria));
        //Comparator<IAlternative> generalizedGiniOracle = new ScoreFunctionOracle(new OWAGeneralizedGini(nbCriteria));
        // Comparator<IAlternative> measureRankingOracle = new MeasureRankingOracle(trainingAlt, testAlt);
        //return Arrays.asList(linearOracle, owaOracle, choquetOracle, chiSquaredOracle, giniOracle, lexminOracle, generalizedGiniOracle);
        return Arrays.asList(owaOracle, choquetOracle, giniOracle, lexminOracle);
        //return Arrays.asList(choquetOracle, giniOracle, measureRankingOracle);
    }

    private List<String> getOracleNames() {
        return Arrays.asList("owa", "choquet", "gini", "lexmin");
        //return Arrays.asList("choquet", "gini", "measureRanking");
        //return Arrays.asList("linear", "owa", "choquet", "chiSquared", "gini", "lexmin", "Ggini");
    }

    private List<AbstractRankingLearning> getRankingAlgorithms(Ranking<IAlternative> expectedRanking) {
        AHPRankLearn ahp = new AHPRankLearn(expectedRanking);
        SVMRankLearn svm = new SVMRankLearn(expectedRanking);
        KappalabRankLearn kappalab = new KappalabRankLearn(expectedRanking);
        kappalab.setDelta(0.00001d);
        List<AbstractRankingLearning> algorithms = Arrays.asList(ahp, svm, kappalab);
        for (AbstractRankingLearning algo : algorithms) {
            algo.setTimeLimit(timeLimit);
        }
        return algorithms;
    }

    private List<RankingMetric> getRankingMetrics(int nbRules) {
        int top1 = (int) (0.01 * nbRules);
        int top10 = (int) (0.1 * nbRules);
        return Arrays.asList(new KendallConcordanceCoeff(), new SpearmanRankCorrelationCoefficient(),
                new RecallMetric(top1), new RecallMetric(top10), new AveragePrecision(top1), new AveragePrecision(top10));
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

    private void writeResult(ResultExpEisen res) throws IOException {
        String resPath = learnDirectory + res.getDataset() + "_" + res.getLearningAlgorithm() + "_" + res.getOracle() +
                "_" + res.getFoldSize() + "_" + res.getFoldIdx() + ".json";
        BufferedWriter writer = new BufferedWriter(new FileWriter(resPath));
        gson.toJson(res, writer);
        writer.close();
    }

    private void launchExp(String dataset) throws Exception {
        List<IRule> rules = getDistinctRules(readAssociationRules(getRulesPath(dataset)));
        System.out.println(dataset + ": " + rules.size() + " distinct rules");
        int nbTransactions = getNbTransactions(getNbTransactionsPath(dataset));
        List<IAlternative> alternatives = computeAlternatives(rules, measureNames, nbTransactions, smoothCounts);
        alternatives = INormalizer.tchebychefNormalize(alternatives);
        Map<IAlternative, IRule> mapAlternativeToRule = mapAlternativeToRule(alternatives, rules);
        Database database = new DatReader(getDatasetPath(), 0, true).readFiles();
        Map<Integer, Integer> itemsMap = database.getItemsMap();
        String[] itemLabels = Files
                .readAllLines(Paths.get(getLabelsPath()))
                .stream()
                .toArray(String[]::new);
        for (int foldSize : foldsSize) {
            int[][] folds = RandomUtil.getInstance().kFolds(k, rules.size(), foldSize);
            List<List<IAlternative>> trainingAlternatives = getTrainingData(alternatives, folds);
            List<List<IRule>> trainingRules = getTrainingData(rules, folds);
            List<List<IAlternative>> testAlternatives = getTestData(alternatives, folds);
            List<List<IRule>> testRules = getTestData(rules, folds);
            for (int i = 0; i < k; i++) {
                List<Comparator<IAlternative>> oracles = getOracles(trainingAlternatives.get(i), trainingRules.get(i),
                        nbTransactions, alternatives, rules, testAlternatives.get(i));
                List<String> oracleNames = getOracleNames();
                for (int j = 0; j < oracles.size(); j++) {
                    Comparator<IAlternative> oracle = oracles.get(j);
                    String oracleName = oracleNames.get(j);
                    //System.out.println("Oracle: " + oracleName);
                    //System.out.println("________");
                    Ranking<IAlternative> expectedRanking = computeRankingWithOracle(oracle,
                            trainingAlternatives.get(i).stream().toArray(IAlternative[]::new));
                    List<AbstractRankingLearning> rankingAlgorithms = getRankingAlgorithms(expectedRanking);
                    List<Ranking<IAlternative>> rankings = new ArrayList<>();
                    Ranking<IAlternative> expectedTestRanking = computeRankingWithOracle(oracle, testAlternatives.get(i));
                    for (AbstractRankingLearning algorithm : rankingAlgorithms) {
                        FunctionParameters func = algorithm.learn();
                        if (func.isTimeOut()) {
                            ResultExpEisen res = new ResultExpEisen();
                            res.setTimeOut(true);
                            res.setTimeToLearn(timeLimit);
                            res.setFoldSize(foldSize);
                            res.setFoldIdx(i);
                            res.setOracle(oracleName);
                            res.setLearningAlgorithm(algorithm.getClass().getSimpleName());
                            res.setDataset(dataset);
                            writeResult(res);
                        }
                        else {
                            //System.out.println(algorithm.getClass().getSimpleName() + ":");
                            Comparator<IAlternative> predicted = new ScoreFunctionOracle(ScoreFunctionFactory.getScoreFunction(func));
                            List<RankingMetric> rankingMetrics = getRankingMetrics(testAlternatives.get(i).size());
                            Map<String, String> metricLabels = getRankingMetricLabels(testAlternatives.get(i).size());
                            double[] metricValues = computeRankingMetrics(testAlternatives.get(i), oracle, predicted, rankingMetrics);
                            Map<String, Double> metrics = new HashMap<>();
                            for (int k = 0; k < metricValues.length; k++) {
                                metrics.put(metricLabels.get(rankingMetrics.get(k).getName()), metricValues[k]);
                            }
                            Ranking<IAlternative> predictedRanking = computeRankingWithOracle(predicted, testAlternatives.get(i));
                            List<RuleWithLabels> topKRules =
                                    convertToRulesWithLabels(getTopK(mapAlternativeToRule, topK, predictedRanking), itemsMap, itemLabels);
                            /*for (int l = 0; l < topKRules.size(); l++) {
                                System.out.println((l+1) + " " + topKRules.get(l));
                            }*/
                            //System.out.println();
                            rankings.add(predictedRanking);
                            ResultExpEisen res = new ResultExpEisen();
                            res.setTimeToLearn(func.getTimeToLearn());
                            res.setFoldSize(foldSize);
                            res.setFoldIdx(i);
                            res.setOracle(oracleName);
                            res.setLearningAlgorithm(algorithm.getClass().getSimpleName());
                            res.setMetricValues(metrics);
                            res.setDataset(dataset);
                            res.setInteractionIndices(func.getInteractionIndices());
                            res.setShapleyValues(func.getShapleyValues());
                            res.setTopK(topKRules);
                            res.setOracleTopK(
                                    IntStream
                                            .range(0, topK)
                                            .map(pos -> predictedRanking.getRankingPos()[expectedTestRanking.getRanking()[pos]])
                                            .toArray()
                            );
                            writeResult(res);
                        }
                    }
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
        new ExperimentEisen().run();
    }
}
