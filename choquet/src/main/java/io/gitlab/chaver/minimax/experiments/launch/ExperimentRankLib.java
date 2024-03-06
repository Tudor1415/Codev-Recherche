package io.gitlab.chaver.minimax.experiments.launch;

import com.google.gson.Gson;
import io.gitlab.chaver.minimax.experiments.util.ResultExpPassive;
import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.learn.oracle.*;
import io.gitlab.chaver.minimax.normalizer.INormalizer;
import io.gitlab.chaver.minimax.ranking.*;
import io.gitlab.chaver.minimax.ranklib.call.RanklibCall;
import io.gitlab.chaver.minimax.ranklib.learn.RankLibResult;
import io.gitlab.chaver.minimax.ranklib.learn.RanklibPassive;
import io.gitlab.chaver.minimax.score.*;
import io.gitlab.chaver.minimax.util.RandomUtil;
import io.gitlab.chaver.mining.rules.io.IRule;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static io.gitlab.chaver.minimax.learn.train.LearnUtil.*;
import static io.gitlab.chaver.minimax.rules.io.RuleMeasures.*;
import static io.gitlab.chaver.minimax.ranklib.call.RanklibCall.Ranker.*;

public class ExperimentRankLib {

    private long seed = 548636487354L;
    private String[] datasets = {"retail", "hepatitis", "mushroom", "connect"};
    private String expDirectory = "results/exp_passive_ranklib/";
    private String learnDirectory = expDirectory + "learn/";
    private String rulesDirectory = "results/rules/";
    private int[] foldsSize = {10, 20, 50, 100};
    private int k = 5;
    private String[] measureNames = {yuleQ, cosine, kruskal, addedValue, certainty};
    private int nbCriteria = measureNames.length;
    private double smoothCounts = 0.1d;
    private int timeLimit = 3600;
    private Gson gson = new Gson();


    private String getRulesPath(String dataset) {
        return rulesDirectory + dataset + "_sols.jsonl";
    }

    private String getNbTransactionsPath(String dataset) {
        return rulesDirectory + dataset + "_prop.jsonl";
    }

    private List<Comparator<IAlternative>> getOracles(List<IAlternative> trainingAlt, List<IRule> trainingRules,
                                                      int nbTransactions, List<IAlternative> allAlt, List<IRule> allRules) {
        double[] weights = RandomUtil.getInstance().generateRandomWeights(measureNames.length);
        Comparator<IAlternative> linearOracle = new LinearFunctionOracle(weights);
        Comparator<IAlternative> owaOracle = new OWAOracle(weights);
        Comparator<IAlternative> choquetOracle = new ChoquetOracle(
                new CorrelationChoquetFuncBuilder(weights, trainingAlt.toArray(new IAlternative[0])).getCapacity()
        );
        Comparator<IAlternative> chiSquaredOracle = new ChiSquaredOracle2(nbTransactions, allAlt, allRules);
        Comparator<IAlternative> giniOracle = new ScoreFunctionOracle(new OWAGini(nbCriteria));
        Comparator<IAlternative> lexminOracle = new ScoreFunctionOracle(new OWALexmin(0.01, nbCriteria));
        Comparator<IAlternative> generalizedGiniOracle = new ScoreFunctionOracle(new OWAGeneralizedGini(nbCriteria));
        return Arrays.asList(linearOracle, owaOracle, choquetOracle, chiSquaredOracle, giniOracle, lexminOracle, generalizedGiniOracle);
    }

    private List<String> getOracleNames() {
        return Arrays.asList("linear", "owa", "choquet", "chiSquared", "gini", "lexmin", "Ggini");
    }

    private List<RanklibPassive> getRankingAlgorithms(Ranking<IAlternative> expectedRanking) {
        String metric2Optimize = "RR@100";
        List<RanklibPassive> rankingAlgorithms = new ArrayList<>();
        List<RanklibCall.Ranker> rankers = Arrays.asList(MART, RankNet, RankBoost, RandomForests, ListNet);
        for (RanklibCall.Ranker ranker : rankers) {
            rankingAlgorithms.add(new RanklibPassive(expectedRanking, ranker, metric2Optimize));
        }
        return rankingAlgorithms;
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

    private void writeResult(ResultExpPassive res) throws IOException {
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
        for (int foldSize : foldsSize) {
            int[][] folds = RandomUtil.getInstance().kFolds(k, rules.size(), foldSize);
            List<List<IAlternative>> trainingAlternatives = getTrainingData(alternatives, folds);
            List<List<IRule>> trainingRules = getTrainingData(rules, folds);
            List<List<IAlternative>> testAlternatives = getTestData(alternatives, folds);
            for (int i = 0; i < k; i++) {
                List<Comparator<IAlternative>> oracles = getOracles(trainingAlternatives.get(i), trainingRules.get(i),
                        nbTransactions, alternatives, rules);
                List<String> oracleNames = getOracleNames();
                for (int j = 0; j < oracles.size(); j++) {
                    Comparator<IAlternative> oracle = oracles.get(j);
                    String oracleName = oracleNames.get(j);
                    Ranking<IAlternative> expectedRanking = computeRankingWithOracle(oracle,
                            trainingAlternatives.get(i).stream().toArray(IAlternative[]::new));
                    List<RanklibPassive> rankingAlgorithms = getRankingAlgorithms(expectedRanking);
                    for (RanklibPassive algorithm : rankingAlgorithms) {
                        System.out.println(foldSize + " " + i + " " + oracleName + " " + algorithm.getRanker());
                        RankLibResult res = algorithm.learn();
                        List<IAlternative> test = testAlternatives.get(i);
                        List<RankingMetric> rankingMetrics = getRankingMetrics(test.size());
                        Ranking<IAlternative> testRanking = computeRankingWithOracle(oracle, test);
                        Ranking<IAlternative> predictedRanking = computeRankingWithScore(test,
                                algorithm.computeScore(res.getResultModel(), test));
                        Map<String, Double> metrics = new HashMap<>();
                        Map<String, String> metricLabels = getRankingMetricLabels(test.size());
                        for (RankingMetric metric : rankingMetrics) {
                            metrics.put(metricLabels.get(metric.getName()), metric.compute(testRanking, predictedRanking));
                        }
                        //System.out.println(algorithm.getRanker() + " : " + metrics);
                        ResultExpPassive result = new ResultExpPassive();
                        result.setTimeToLearn(res.getTimeToLearn());
                        result.setLearningAlgorithm(algorithm.getRanker().name());
                        result.setMetricValues(metrics);
                        result.setDataset(dataset);
                        result.setOracle(oracleName);
                        result.setFoldSize(foldSize);
                        result.setFoldIdx(i);
                        System.out.println(result);
                        writeResult(result);
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
        new ExperimentRankLib().run();
    }
}
