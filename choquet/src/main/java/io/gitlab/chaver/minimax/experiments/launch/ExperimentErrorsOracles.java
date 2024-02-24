package io.gitlab.chaver.minimax.experiments.launch;

import com.google.gson.Gson;
import io.gitlab.chaver.minimax.experiments.util.ResultExpActive;
import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.learn.oracle.*;
import io.gitlab.chaver.minimax.learn.train.AbstractRankingLearning;
import io.gitlab.chaver.minimax.learn.train.iterative.AHPIterative;
import io.gitlab.chaver.minimax.learn.train.iterative.BaselineIterative;
import io.gitlab.chaver.minimax.learn.train.iterative.KappalabSocketIterative;
import io.gitlab.chaver.minimax.learn.train.iterative.SVMIterative;
import io.gitlab.chaver.minimax.learn.util.MinGapsRankingsProvider;
import io.gitlab.chaver.minimax.learn.util.RankingMetricsDashboard;
import io.gitlab.chaver.minimax.learn.util.RankingsProvider;
import io.gitlab.chaver.minimax.normalizer.INormalizer;
import io.gitlab.chaver.minimax.ranking.*;
import io.gitlab.chaver.minimax.score.*;
import io.gitlab.chaver.minimax.util.RandomUtil;
import io.gitlab.chaver.mining.rules.io.IRule;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static io.gitlab.chaver.minimax.learn.train.LearnUtil.*;
import static io.gitlab.chaver.minimax.rules.io.RuleMeasures.*;

public class ExperimentErrorsOracles {

    private long seed = 548636487354L;
    private String[] datasets = {"mushroom", "hepatitis", "retail", "connect"};
    private String expDirectory = "results/exp_active_errors/";
    private String learnDirectory = expDirectory + "learn/";
    private String rulesDirectory = "results/rules/";
    private int nbIterations = 100;
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

    private List<Comparator<IAlternative>> getOracles(List<IAlternative> trainingAlt,
                                                      int nbTransactions, List<IAlternative> allAlt, List<IRule> allRules) {
        double[] weights = RandomUtil.getInstance().generateRandomWeights(measureNames.length);
        Comparator<IAlternative> choquetOracle = new ChoquetOracle(
                new CorrelationChoquetFuncBuilder(weights, trainingAlt.toArray(new IAlternative[0])).getCapacity()
        );
        return Arrays.asList(choquetOracle, choquetOracle, choquetOracle);
    }

    private List<String> getOracleNames() {
        String choquet = "choquet";
        return Arrays.asList(choquet, choquet, choquet);
    }

    private double[] getErrorProbability() {
        return new double[]{0.1, 0.2, 0.4};
    }

    private RankingsProvider getRankingsProvider(List<IAlternative> trainingAlternatives, Comparator<IAlternative> oracle,
                                                 IScoreFunction<IAlternative> initial) {
        return new MinGapsRankingsProvider(trainingAlternatives, oracle, initial, 50);
    }

    private List<AbstractRankingLearning> getRankingAlgorithms(List<IAlternative> trainingAlternatives,
                                                               Comparator<IAlternative> oracle,
                                                               IScoreFunction<IAlternative> initial) {
        BaselineIterative baseline = new BaselineIterative(nbIterations, getRankingsProvider(trainingAlternatives, oracle, initial), initial, nbCriteria);
        AHPIterative ahp = new AHPIterative(nbIterations, getRankingsProvider(trainingAlternatives, oracle, initial), initial, nbCriteria);
        SVMIterative svm = new SVMIterative(nbIterations, getRankingsProvider(trainingAlternatives, oracle, initial), initial, nbCriteria);
        KappalabSocketIterative kappalab = new KappalabSocketIterative(nbIterations, getRankingsProvider(trainingAlternatives, oracle, initial), initial, nbCriteria);
        //GLSCplexIterative kappalab = new GLSCplexIterative(nbIterations, getRankingsProvider(trainingAlternatives, oracle, initial), initial, nbCriteria);
        List<AbstractRankingLearning> algorithms = Arrays.asList(baseline, ahp, svm, kappalab);
        //List<AbstractRankingLearning> algorithms = Arrays.asList(kappalab);
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

    private void writeResult(ResultExpActive res) throws IOException {
        String resPath = learnDirectory + res.getDataset() + "_" + res.getLearningAlgorithm() + "_" + res.getOracle() +
                "_" + res.getFoldIdx() + "_" + res.getErrorProbability() + ".json";
        BufferedWriter writer = new BufferedWriter(new FileWriter(resPath));
        gson.toJson(res, writer);
        writer.close();
    }

    private void launchExp(String dataset) throws Exception {
        List<IRule> rules = getDistinctRules(readAssociationRules(getRulesPath(dataset)));
        int nbTransactions = getNbTransactions(getNbTransactionsPath(dataset));
        List<IAlternative> alternatives = computeAlternatives(rules, measureNames, nbTransactions, smoothCounts);
        alternatives = INormalizer.tchebychefNormalize(alternatives);
        int[][] folds = RandomUtil.getInstance().kFolds(k, alternatives.size());
        List<List<IAlternative>> trainingAlternatives = getTrainingData(alternatives, folds);
        List<List<IAlternative>> testAlternatives = getTestData(alternatives, folds);
        double[] initialWeigths = new double[nbCriteria];
        for (int i = 0; i < nbCriteria; i++) {
            initialWeigths[i] = 1d;
        }
        IScoreFunction<IAlternative> initialFunc = new LinearScoreFunction(initialWeigths);
        for (int i = 0; i < k; i++) {
            List<Comparator<IAlternative>> oracles = getOracles(trainingAlternatives.get(i),
                    nbTransactions, alternatives, rules);
            List<String> oracleNames = getOracleNames();
            double[] errorProbability = getErrorProbability();
            for (int j = 0; j < oracles.size(); j++) {
                Comparator<IAlternative> oracle = oracles.get(j);
                String oracleName = oracleNames.get(j);
                List<AbstractRankingLearning> rankingAlgorithms = getRankingAlgorithms(testAlternatives.get(i),
                        new ErrorOracle(oracle, errorProbability[j]), initialFunc);
                Ranking<IAlternative> refRanking = computeRankingWithOracle(oracle, trainingAlternatives.get(i));
                for (AbstractRankingLearning algorithm : rankingAlgorithms) {
                    System.out.println(dataset + " fold " + i + " oracle " + oracleName + " algorithm " + algorithm.getClass().getSimpleName());
                    try {
                        RankingMetricsDashboard dashboard = new RankingMetricsDashboard(getRankingMetrics(testAlternatives.get(i).size()), refRanking,
                                getRankingMetricLabels(testAlternatives.get(i).size()));
                        algorithm.addObserver(dashboard);
                        FunctionParameters func = algorithm.learn();
                        ResultExpActive res = new ResultExpActive();
                        res.setMetricValues(dashboard.getMetricValues());
                        res.setOracle(oracleName);
                        res.setErrorProbability(errorProbability[j]);
                        res.setLearningAlgorithm(algorithm.getClass().getSimpleName());
                        res.setDataset(dataset);
                        res.setFoldIdx(i);
                        res.setNbIterations(nbIterations);
                        res.setTimeToLearn(func.getTimeToLearn());
                        if (func.isTimeOut()) {
                            res.setTimeOut(true);
                            res.setTimeToLearn(timeLimit);
                        }
                        else if (func.getErrorMessages() != null) {
                            res.setErrorMessages(func.getErrorMessages());
                        }
                        writeResult(res);
                    }
                    catch (Exception e) {
                        System.out.println("Error for " + algorithm + " ");
                        e.printStackTrace();
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
        new ExperimentErrorsOracles().run();
    }
}
